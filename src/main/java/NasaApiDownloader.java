import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class NasaApiDownloader {
    private static final String NASA_API_KEY = "Kx00eaMc1QLISJqfeMihg6DjcvXdnpAeRSQRvGUa";

    public static void main(String[] args) {
        try (CloseableHttpClient httpClient = createHttpClient()){
            HttpGet request = new HttpGet("https://api.nasa.gov/planetary/apod?api_key=" + NASA_API_KEY);
            CloseableHttpResponse response = httpClient.execute(request);

            NasaApiResponse nasaApiResponse = parseJsonResponse(response, NasaApiResponse.class);

            downloadImage(httpClient, nasaApiResponse.getUrl());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }

    private static CloseableHttpClient createHttpClient() {
        return HttpClientBuilder.create()
                .setDefaultRequestConfig(RequestConfig.custom()
                        .setConnectTimeout(5000)
                        .setSocketTimeout(30000)
                        .setRedirectsEnabled(false)
                        .build())
                .build();
    }

    private static <T> T parseJsonResponse(CloseableHttpResponse response, Class<T> responseClass) throws IOException {
        String jsonResponse = EntityUtils.toString(response.getEntity());
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(jsonResponse, responseClass);
    }

    private static void downloadImage(CloseableHttpClient httpClient, String imageUrl) throws IOException {
        HttpGet imageRequest = new HttpGet(imageUrl);
        CloseableHttpResponse imageResponse = httpClient.execute(imageRequest);

        String filename = imageUrl.substring(imageUrl.lastIndexOf("/") + 1);
        File file = new File(filename);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(EntityUtils.toByteArray(imageResponse.getEntity()));
        }
    }
}