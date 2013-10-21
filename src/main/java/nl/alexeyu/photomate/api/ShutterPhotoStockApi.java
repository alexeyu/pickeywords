package nl.alexeyu.photomate.api;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.PhotoFactory;
import nl.alexeyu.photomate.model.RemotePhoto;
import nl.alexeyu.photomate.model.ResultProcessor;
import nl.alexeyu.photomate.service.TaskWeight;
import nl.alexeyu.photomate.service.WeighedTask;
import nl.alexeyu.photomate.util.ConfigReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class ShutterPhotoStockApi extends AbstractPhotoApi<RemotePhoto> implements PhotoStockApi {

    private static final String BASE_URI = "http://api.shutterstock.com/images/";

    @Inject
    private ConfigReader configReader;
    
    @Inject
    private ExecutorService executor;
    
    @Inject 
    private PhotoFactory photoFactory;
    
    private HttpClient client;
    
    private int resultsPerPage;

    @Inject
    public void init() {
        String name = configReader.getProperty("stock.shutter.api.name", "");
        String apiKey = configReader.getProperty("stock.shutter.api.key", "");
        resultsPerPage = Integer.valueOf(configReader.getProperty("stock.shutter.api.resultsPerPage", "10"));
        this.client = createClient(name, apiKey);
    }
    
    private HttpClient createClient(String name, String apiKey) {
        Credentials credentials = new UsernamePasswordCredentials(name, apiKey);
        CredentialsProvider credProvider = new BasicCredentialsProvider();
        credProvider.setCredentials(AuthScope.ANY, credentials);
        return HttpClientBuilder
                .create()
                .setDefaultCredentialsProvider(credProvider)
                .setConnectionManager(new PoolingHttpClientConnectionManager())
                .build();
    }

    @Override
    public List<Photo> search(String keywords) {
        String requestUri = String.format("%ssearch.json?searchterm=%s&results_per_page=%s", 
                BASE_URI, encode(keywords), resultsPerPage);
        ShutterSearchResult searchResult = doRequest(requestUri, new JsonResponseHandler<>(ShutterSearchResult.class));
        List<Photo> photos = new ArrayList<>();
        for (ShutterPhotoDescription photoDescr : searchResult.getPhotoDescriptions()) {
            photos.add(photoFactory.createRemotePhoto(photoDescr.getUrl(), photoDescr.getThumbailUrl(), this));
        }
        return photos;
    }
    
    private String encode(String keywords) {
        try {
            return URLEncoder.encode(keywords, "UTF-8");
        } catch (Exception ex) {
            throw new IllegalArgumentException(keywords, ex);
        }
    }

    @Override
    public void provideThumbnail(RemotePhoto photo, ResultProcessor<ImageIcon> filler) {
        doRequest(photo.getThumbnailUrl(), new ImageResponseHandler(), new ProxyResultProcessor<>("thumbnail", filler));        
    }


    @Override
    public void provideKeywords(RemotePhoto photo, final ResultProcessor<List<String>> resultProcessor) {
        doRequest(photo.getUrl() + ".json", new JsonResponseHandler<>(ShutterPhotoDetails.class), new ResultProcessor<ShutterPhotoDetails>() {

            @Override
            public void process(ShutterPhotoDetails result) {
                resultProcessor.process(result.getKeywords());
            }
            
        });        
    }

    private <T> T doRequest(String url, ResponseHandler<T> responseHandler, ResultProcessor<T> filler) {
        executor.submit(new AsyncRestServiceCaller<>(url, responseHandler, filler));
        return null;
    }

    private <T> T doRequest(String url, ResponseHandler<T> responseHandler) {
        try {
            return executor.submit(new SyncRestServiceCaller<>(url, responseHandler)).get();
        } catch (InterruptedException | ExecutionException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static class JsonResponseHandler<T> implements ResponseHandler<T> {

        private final ObjectMapper objectMapper = new ObjectMapper();
        
        private final Class<T> clazz;
        
        public JsonResponseHandler(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
        public T handleResponse(HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = IOUtils.toString(entity.getContent());
                EntityUtils.consume(entity);
                return objectMapper.readValue(result, clazz);
            } else {
                throw new IllegalStateException(response.getStatusLine().getReasonPhrase());
            }
        }

    }

    private static class ImageResponseHandler implements ResponseHandler<ImageIcon> {

        @Override
        public ImageIcon handleResponse(HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                byte[] content = IOUtils.toByteArray(entity.getContent());
                EntityUtils.consume(entity);
                return new ImageIcon(content);
            } else {
                throw new IllegalStateException(response.getStatusLine().getReasonPhrase());
            }
        }

    }

    private class AsyncRestServiceCaller<T> implements Runnable, WeighedTask {
        
        private final String url;
        
        private final ResponseHandler<T> responseHandler;
        
        private final ResultProcessor<T> resultProcessor;

        public AsyncRestServiceCaller(String url, ResponseHandler<T> responseHandler, ResultProcessor<T> filler) {
            this.url = url;
            this.responseHandler = responseHandler;
            this.resultProcessor = filler;
        }
        
        @Override
        public void run() {
            try {
                HttpGet httpget = new HttpGet(url);
                resultProcessor.process(client.execute(httpget, responseHandler));
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public TaskWeight getWeight() {
            return TaskWeight.MEDIUM;
        }
        
    }
    
    private class SyncRestServiceCaller<T> implements Callable<T>, WeighedTask {
        
        private final String url;
        
        private final ResponseHandler<T> responseHandler;
        
        public SyncRestServiceCaller(String url, ResponseHandler<T> responseHandler) {
            this.url = url;
            this.responseHandler = responseHandler;
        }
        
        @Override
        public T call() throws Exception {
            HttpGet httpget = new HttpGet(url);
            return client.execute(httpget, responseHandler);
        }

        @Override
        public TaskWeight getWeight() {
            return TaskWeight.MEDIUM;
        }
        
    }

}
