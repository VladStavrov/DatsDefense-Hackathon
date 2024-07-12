package org.example;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiClient {
    //TODO Вставить BASE_URL из документации
    private static final String BASE_URL = "https://games-test.datsteam.dev/"; //Тестовый
//    private static final String BASE_URL = "https://games.datsteam.dev/"; // Основной

    private static final String AUTH_TOKEN = "6684220d3195e6684220d31962";
    private final HttpClient httpClient;

    public ApiClient() {
        this.httpClient = HttpClient.newBuilder().build();
    }

    private String buildUrlWithParams(String endpoint) {
        return BASE_URL + endpoint;
    }

    private HttpRequest.Builder createRequestBuilder(String url) {
        return HttpRequest.newBuilder().uri(URI.create(url)).header("Content-Type", "application/json").header("X-Auth-Token", AUTH_TOKEN);
    }

    private String sendRequest(HttpRequest request) throws Exception {
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Request failed with status code: " + response.statusCode() + " and body: " + response.body());
        }
    }

    public String doGet(String endpoint) throws Exception {
        String url = buildUrlWithParams(endpoint);
        HttpRequest request = createRequestBuilder(url).GET().build();
        return sendRequest(request);
    }

    public String doPost(String endpoint, String jsonBody) throws Exception {
        String url = buildUrlWithParams(endpoint);
        HttpRequest request = createRequestBuilder(url).POST(HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        return sendRequest(request);
    }

    public String doPut(String endpoint) throws Exception {
        String url = buildUrlWithParams(endpoint);
        HttpRequest request = createRequestBuilder(url).PUT(HttpRequest.BodyPublishers.noBody()).build();
        return sendRequest(request);
    }

    public String doPatch(String endpoint, String jsonBody) throws Exception {
        String url = buildUrlWithParams(endpoint);
        HttpRequest request = createRequestBuilder(url).method("PATCH", HttpRequest.BodyPublishers.ofString(jsonBody)).build();
        return sendRequest(request);
    }

    public String doDelete(String endpoint) throws Exception {
        String url = buildUrlWithParams(endpoint);
        HttpRequest request = createRequestBuilder(url).DELETE().build();
        return sendRequest(request);
    }
}
