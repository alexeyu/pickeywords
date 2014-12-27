package nl.alexeyu.photomate.api.shutterstock;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.PhotoApi;
import nl.alexeyu.photomate.api.PhotoFactory;
import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.RemotePhoto;
import nl.alexeyu.photomate.model.PhotoMetaData;
import nl.alexeyu.photomate.util.ConfigReader;

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

import com.google.common.io.ByteStreams;

public class ShutterPhotoStockApi implements PhotoApi<ShutterPhotoDescription, RemotePhoto>, PhotoStockApi {
    
    private final Logger logger = LoggerFactory.getLogger("ShutterPhotoStockApi");

	private static final String QUERY_TEMPLATE = 
	        "http://api.shutterstock.com/images/search.json?searchterm=%s&results_per_page=%s&search_group=photos";

	@Inject
	private ConfigReader configReader;

	private HttpClient client;

	private int resultsPerPage = 10;

	@Inject
	public void init() {
		String name = configReader.getProperty("stock.shutter.api.name").orElse("");
		String apiKey = configReader.getProperty("stock.shutter.api.key").orElse("");
		Optional<String> resultsPerPageProperty = configReader.getProperty("stock.shutter.api.resultsPerPage");
		if (resultsPerPageProperty.isPresent()) {
			resultsPerPage = Integer.valueOf(resultsPerPageProperty.get());
		}
		this.client = createClient(name, apiKey);
	}

	private HttpClient createClient(String name, String apiKey) {
		Credentials credentials = new UsernamePasswordCredentials(name, apiKey);
		CredentialsProvider credProvider = new BasicCredentialsProvider();
		credProvider.setCredentials(AuthScope.ANY, credentials);
		return HttpClientBuilder.create()
				.setDefaultCredentialsProvider(credProvider)
				.setConnectionManager(new PoolingHttpClientConnectionManager())
				.build();
	}

	@Override
	public List<RemotePhoto> search(String keywords) {
        try {
            String requestUri = String.format(QUERY_TEMPLATE,  URLEncoder.encode(keywords, "UTF-8"), resultsPerPage);
            JsonResponseReader<ShutterSearchResult> searchResultReader = new JsonResponseReader<>(ShutterSearchResult.class);
            ShutterSearchResult searchResult = client.execute(new HttpGet(requestUri),  new DefaultResponseHandler<>(searchResultReader));
            return createPhotos(searchResult.getPhotoDescriptions().stream(), new ShutterPhotoFactory());
        } catch (IOException ex) {
            logger.error("Cannot find photos", ex);
            return Collections.emptyList();
        }
	}
	
	@Override
    public Supplier<PhotoMetaData> metaDataSupplier(RemotePhoto photo) {
	    return new HttpResponseSupplier<>(
	            photo.photoUrl() + ".json", 
	            new JsonResponseReader<>(PhotoMetaData.class)); 
    }

    @Override
    public Supplier<List<ImageIcon>> thumbnailsSupplier(RemotePhoto photo) {
        return new HttpResponseSupplier<>(
                photo.thumbnailUrl(), 
                content -> Collections.singletonList(new ImageIcon(content)));
    }

    private static class DefaultResponseHandler<T> implements ResponseHandler<T> {
        
        private final Function<byte[], T> contentReader;
        
        DefaultResponseHandler(Function<byte[], T> contentReader) {
            this.contentReader = contentReader;
        }
        
        @Override
        public T handleResponse(HttpResponse response) throws IOException {
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                HttpEntity entity = response.getEntity();
                byte[] content = ByteStreams.toByteArray(entity.getContent());
                EntityUtils.consume(entity);
                return contentReader.apply(content);
            } else {
                throw new IOException(response.getStatusLine().getReasonPhrase());
            }
        }
    }
    
	private static class JsonResponseReader<T> implements Function<byte[], T> {

		private final Class<T> clazz;
		
		public JsonResponseReader(Class<T> clazz) {
            this.clazz = clazz;
        }

        @Override
		public T apply(byte[] content) {
		    try {
				return new ObjectMapper().readValue(content, clazz);
			} catch (IOException ex) {
			    throw new IllegalStateException(ex);
			}
		}

	}
	
	private class HttpResponseSupplier<T> implements Supplier<T> {
	    
	    private final String url;
	    private final Function<byte[], T> responseReader;

        public HttpResponseSupplier(String url, Function<byte[], T> responseReader) {
            this.url = url;
            this.responseReader = responseReader;
        }

        @Override
        public T get() {
            try {
                return client.execute(
                                new HttpGet(url), 
                                new DefaultResponseHandler<>(responseReader));
            } catch (IOException ex) {
                logger.error("Cannot read url " + url, ex);
                return null;
            }
        }
	    
	}
	
	private static class ShutterPhotoFactory implements PhotoFactory<ShutterPhotoDescription, RemotePhoto> {

	    @Override
	    public RemotePhoto createPhoto(ShutterPhotoDescription source) {
	        return new RemotePhoto(source.getUrl(), source.getThumbailUrl());
	    }

	}

}
