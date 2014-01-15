package nl.alexeyu.photomate.api.shutterstock;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.PhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.RemotePhoto;
import nl.alexeyu.photomate.service.PrioritizedTask;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShutterPhotoStockApi implements PhotoApi<RemotePhoto>, PhotoStockApi {
    
    private static final Logger logger = LoggerFactory.getLogger("ShutterStockPhotoAPI");

    private static final String BASE_URI = "http://api.shutterstock.com/images/";

    private static final String QUERY_TEMPLATE = "%ssearch.json?searchterm=%s&results_per_page=%s&search_group=photos";

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
    public List<RemotePhoto> search(String keywords) {
        String requestUri = String.format(QUERY_TEMPLATE, BASE_URI, encode(keywords), resultsPerPage);
        ShutterSearchResult searchResult = doRequest(requestUri, new JsonResponseHandler<>(ShutterSearchResult.class));
        List<RemotePhoto> photos = new ArrayList<>();
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
    public void provideThumbnail(RemotePhoto photo) {
        executor.execute(new ThumbnailReader(photo));
    }

    @Override
    public void provideMetadata(RemotePhoto photo) {
        executor.execute(new MetadataReader(photo));
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

    private class ThumbnailReader implements Runnable, PrioritizedTask {
        
        private final RemotePhoto photo;
        
        private final ImageResponseHandler responseHandler;
        
        public ThumbnailReader(RemotePhoto photo) {
            this.photo = photo;
            this.responseHandler = new ImageResponseHandler();
        }
        
        @Override
        public void run() {
            try {
                HttpGet httpget = new HttpGet(photo.getThumbnailUrl());
                ImageIcon thumbnail = client.execute(httpget, responseHandler);
                photo.setThumbnail(thumbnail);
            } catch (IOException ex) {
                throw new IllegalStateException(ex);
            }
        }

        @Override
        public TaskPriority getPriority() {
            return TaskPriority.MEDIUM;
        }
        
    }

    private class MetadataReader implements Runnable, PrioritizedTask {
        
        private final RemotePhoto photo;
        
        private final JsonResponseHandler<ShutterPhotoDetails> responseHandler;
        
        public MetadataReader(RemotePhoto photo) {
            this.photo = photo;
            this.responseHandler = new JsonResponseHandler<>(ShutterPhotoDetails.class);
        }
        
        @Override
        public void run() {
            try {
                HttpGet httpget = new HttpGet(photo.getUrl() + ".json");
                ShutterPhotoDetails details = client.execute(httpget, responseHandler);
                photo.setMetaData(details);
            } catch (IOException ex) {
                logger.error("Could not read metadata of " + photo, ex);
            }
        }

        @Override
        public TaskPriority getPriority() {
            return TaskPriority.MEDIUM;
        }
        
    }

    private class SyncRestServiceCaller<T> implements Callable<T>, PrioritizedTask {
        
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
        public TaskPriority getPriority() {
            return TaskPriority.MEDIUM;
        }
        
    }

}
