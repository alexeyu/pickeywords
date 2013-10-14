package nl.alexeyu.photomate.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.Photo;
import nl.alexeyu.photomate.util.ConfigReader;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.map.ObjectMapper;

public class ShutterPhotoStockApi implements PhotoStockApi {

    private static final String BASE_URI = "http://api.shutterstock.com/images/";
    
    private static final ResponseHandler<ShutterSearchResult> PHOTO_SEARCH_HANDLER 
        = new JsonResponseHandler<>(ShutterSearchResult.class);

    private static final ResponseHandler<ShutterPhotoDetails> PHOTO_DETAILS_HANDLER 
        = new JsonResponseHandler<>(ShutterPhotoDetails.class);

    private String name;
    private String apiKey;

    @Inject
    private ConfigReader configReader;

    private DefaultHttpClient client;

    @Inject
    public void init() {
        this.name = configReader.getProperty("stock.shutter.api.name", "");
        this.apiKey = configReader.getProperty("stock.shutter.api.key", "");
        client = new DefaultHttpClient();
        Credentials credentials = new UsernamePasswordCredentials(name, apiKey);
        client.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);
    }

    @Override
    public ImageIcon getImage(String photoUrl) {
        return doRequest(photoUrl, new ImageResponseHandler());
    }

    @Override
    public List<String> getKeywords(String photoUrl) {
        ShutterPhotoDetails photoDetails = doRequest(photoUrl + ".json", PHOTO_DETAILS_HANDLER);
        return photoDetails.getKeywords();
    }

    @Override
    public List<Photo> search(String keyword) {
        String requestUri = String.format("%ssearch.json?searchterm=%s&results_per_page=10", BASE_URI, keyword);
        ShutterSearchResult searchResult = doRequest(requestUri, PHOTO_SEARCH_HANDLER);
        List<Photo> photos = new ArrayList<>();
        for (ShutterPhotoDescription photoDescr : searchResult.getPhotoDescriptions()) {
            photos.add(new RemotePhoto(photoDescr.getUrl(), photoDescr.getThumbailUrl(), this));
        }
        return photos;
    }

    private <T> T doRequest(String url, ResponseHandler<T> responseHandler) {
        try {
            HttpGet httpget = new HttpGet(url);
            return client.execute(httpget, responseHandler);
        } catch (IOException ex) {
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

}
