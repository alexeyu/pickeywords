package nl.alexeyu.photomate.api.shutterstock;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.swing.ImageIcon;

import nl.alexeyu.photomate.api.PhotoApi;
import nl.alexeyu.photomate.api.PhotoStockApi;
import nl.alexeyu.photomate.api.RemotePhoto;
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

import com.google.common.io.ByteStreams;
import com.google.common.io.CharStreams;

public class ShutterPhotoStockApi implements PhotoApi<RemotePhoto>,
		PhotoStockApi {

	private static final String BASE_URI = "http://api.shutterstock.com/images/";

	private static final String QUERY_TEMPLATE = "%ssearch.json?searchterm=%s&results_per_page=%s&search_group=photos";

	@Inject
	private ConfigReader configReader;

	private HttpClient client;

	private int resultsPerPage;

	@Inject
	public void init() {
		String name = configReader.getProperty("stock.shutter.api.name", "");
		String apiKey = configReader.getProperty("stock.shutter.api.key", "");
		resultsPerPage = Integer.valueOf(configReader.getProperty(
				"stock.shutter.api.resultsPerPage", "10"));
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
		String requestUri = String.format(QUERY_TEMPLATE, BASE_URI,
				encode(keywords), resultsPerPage);
		ShutterSearchResult searchResult = doRequest(requestUri,
				new JsonResponseHandler<>(ShutterSearchResult.class));
		return searchResult.getPhotoDescriptions().stream()
				.map(descr -> createRemotePhoto(descr))
				.collect(Collectors.toList());
	}

	private RemotePhoto createRemotePhoto(ShutterPhotoDescription descr) {
		RemotePhoto photo = new RemotePhoto(descr.getUrl(),
				descr.getThumbailUrl());
		init(photo);
		return photo;
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
		CompletableFuture.supplyAsync(new ThumbnailReader(photo)).thenAccept(
				t -> photo.addThumbnail(t));
	}

	@Override
	public void provideMetadata(RemotePhoto photo) {
		CompletableFuture.supplyAsync(new MetadataReader(photo)).thenAccept(
				m -> photo.setMetaData(m));
	}

	private <T> T doRequest(String url, ResponseHandler<T> responseHandler) {
		try {
			return CompletableFuture.supplyAsync(
					new SyncRestServiceCaller<>(url, responseHandler)).get();
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
				String result = CharStreams.toString(new InputStreamReader(
						entity.getContent()));
				EntityUtils.consume(entity);
				return objectMapper.readValue(result, clazz);
			} else {
				throw new IllegalStateException(response.getStatusLine()
						.getReasonPhrase());
			}
		}

	}

	private static class ImageResponseHandler implements
			ResponseHandler<ImageIcon> {

		@Override
		public ImageIcon handleResponse(HttpResponse response)
				throws IOException {
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				HttpEntity entity = response.getEntity();
				byte[] content = ByteStreams.toByteArray(entity.getContent());
				EntityUtils.consume(entity);
				return new ImageIcon(content);
			} else {
				throw new IllegalStateException(response.getStatusLine()
						.getReasonPhrase());
			}
		}

	}

	private class ThumbnailReader implements Supplier<ImageIcon> {

		private final RemotePhoto photo;

		private final ImageResponseHandler responseHandler;

		public ThumbnailReader(RemotePhoto photo) {
			this.photo = photo;
			this.responseHandler = new ImageResponseHandler();
		}

		@Override
		public ImageIcon get() {
			try {
				HttpGet httpget = new HttpGet(photo.getThumbnailUrl());
				return client.execute(httpget, responseHandler);
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}

	}

	private class MetadataReader implements Supplier<ShutterPhotoDetails> {

		private final RemotePhoto photo;

		private final JsonResponseHandler<ShutterPhotoDetails> responseHandler;

		public MetadataReader(RemotePhoto photo) {
			this.photo = photo;
			this.responseHandler = new JsonResponseHandler<>(
					ShutterPhotoDetails.class);
		}

		@Override
		public ShutterPhotoDetails get() {
			try {
				HttpGet httpget = new HttpGet(photo.getUrl() + ".json");
				return client.execute(httpget, responseHandler);
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}

	}

	private class SyncRestServiceCaller<T> implements Supplier<T> {

		private final String url;

		private final ResponseHandler<T> responseHandler;

		public SyncRestServiceCaller(String url,
				ResponseHandler<T> responseHandler) {
			this.url = url;
			this.responseHandler = responseHandler;
		}

		@Override
		public T get() {
			try {
				HttpGet httpget = new HttpGet(url);
				return client.execute(httpget, responseHandler);
			} catch (IOException ex) {
				throw new IllegalStateException(ex);
			}
		}

	}

}
