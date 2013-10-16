package nl.alexeyu.photomate.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.model.RemotePhoto;
import nl.alexeyu.photomate.model.ResultFiller;
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
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class ShutterPhotoStockApi extends AbstractPhotoApi implements PhotoStockApi {

    private static final String BASE_URI = "http://api.shutterstock.com/images/";

    private String name;
    private String apiKey;

    @Inject
    private ConfigReader configReader;
    
    @Inject
    private ExecutorService executor;

    @Inject
    public void init() {
        this.name = configReader.getProperty("stock.shutter.api.name", "");
        this.apiKey = configReader.getProperty("stock.shutter.api.key", "");
    }
    
    private HttpClient createClient() {
        DefaultHttpClient client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(name, apiKey);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
        return client;
    }

    @Override
    public List<Photo> search(String keyword) {
        String requestUri = String.format("%ssearch.json?searchterm=%s&results_per_page=10", BASE_URI, keyword);
        ShutterSearchResult searchResult = doRequest(requestUri, new JsonResponseHandler<>(ShutterSearchResult.class));
        List<Photo> photos = new ArrayList<>();
        for (ShutterPhotoDescription photoDescr : searchResult.getPhotoDescriptions()) {
            photos.add(new RemotePhoto(photoDescr.getUrl(), photoDescr.getThumbailUrl(), this));
        }
        return photos;
    }

    @Override
    public void provideThumbnail(String tumbnailUrl, ResultFiller<ImageIcon> filler) {
        doRequest(tumbnailUrl, new ImageResponseHandler(), new ProxyFiller<>("thumbnail", filler));        
    }


    @Override
    public void provideKeywords(String url, final ResultFiller<List<String>> filler) {
        doRequest(url + ".json", new JsonResponseHandler<>(ShutterPhotoDetails.class), new ResultFiller<ShutterPhotoDetails>() {

            @Override
            public void fill(ShutterPhotoDetails result) {
                filler.fill(result.getKeywords());
            }
            
        });        
    }

    private <T> T doRequest(String url, ResponseHandler<T> responseHandler, ResultFiller<T> filler) {
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
        
        private final ResultFiller<T> filler;

        public AsyncRestServiceCaller(String url, ResponseHandler<T> responseHandler, ResultFiller<T> filler) {
            this.url = url;
            this.responseHandler = responseHandler;
            this.filler = filler;
        }
        
        @Override
        public void run() {
            try {
                HttpGet httpget = new HttpGet(url);
                filler.fill(createClient().execute(httpget, responseHandler));
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
            return createClient().execute(httpget, responseHandler);
        }

        @Override
        public TaskWeight getWeight() {
            return TaskWeight.MEDIUM;
        }
        
    }

}
