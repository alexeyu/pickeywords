package nl.alexeyu.photomate.api;

import java.io.IOException;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.model.StockPhotoDescription;
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
    public Icon getImage(StockPhotoDescription photo) {
        return doRequest(photo.getUrl(), new ImageResponseHandler());
    }

    @Override
    public List<String> getKeywords(StockPhotoDescription photo) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<StockPhotoDescription> search(String keyword) {
        ShutterSearchResult searchResult = doRequest(
                BASE_URI + "search.json?searchterm=" + keyword,
                new JsonResponseHandler());
        return searchResult.getPhotoDescriptions();
    }

    private <T> T doRequest(String url, ResponseHandler<T> responseHandler) {
        try {
            HttpGet httpget = new HttpGet(url);
            return client.execute(httpget, responseHandler);
        } catch (IOException ex) {
            throw new IllegalStateException(ex);
        }
    }

    private static class JsonResponseHandler implements ResponseHandler<ShutterSearchResult> {

        private ObjectMapper objectMapper = new ObjectMapper();

        @Override
        public ShutterSearchResult handleResponse(HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                String result = IOUtils.toString(entity.getContent());
                EntityUtils.consume(entity);
                return objectMapper.readValue(result, ShutterSearchResult.class);
            } else {
                throw new IllegalStateException(response.getStatusLine().getReasonPhrase());
            }
        }

    }

    private static class ImageResponseHandler implements ResponseHandler<Icon> {

        @Override
        public Icon handleResponse(HttpResponse response) throws IOException {
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
