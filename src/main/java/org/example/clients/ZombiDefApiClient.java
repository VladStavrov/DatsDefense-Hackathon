package org.example.clients;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.exeptions.ApiErrorResponse;
import org.example.exeptions.ApiException;
import org.example.models.mapInfo.InfoResponse;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.io.IOException;

public class ZombiDefApiClient {

    private static final String BASE_URL = "https://games-test.datsteam.dev/play/zombidef/units";
    private static final String API_KEY = "6684220d3195e6684220d31962";
    private static final String AUTH_HEADER = "X-Auth-Token";

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;

    public ZombiDefApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }

    public static InfoResponse getApiResponse() {
        try {
            ZombiDefApiClient client = new ZombiDefApiClient();
            return client.fetchUnits();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public InfoResponse fetchUnits() throws IOException, InterruptedException, ApiException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(BASE_URL)).header(AUTH_HEADER, API_KEY).header("Content-Type", "application/json").GET().build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();
        String responseBody = response.body();

        if (statusCode == 200) {
            return objectMapper.readValue(responseBody, InfoResponse.class);
        } else if (statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 404 || statusCode == 429) {
            ApiErrorResponse errorResponse = objectMapper.readValue(responseBody, ApiErrorResponse.class);
            throw new ApiException(statusCode, errorResponse);
        } else {
            throw new RuntimeException("Unexpected response status: " + statusCode);
        }
    }

    public static void main(String[] args) {
        InfoResponse response = getApiResponse();
        if (response != null) {
            System.out.println(response);
        } else {
            System.out.println("Failed to fetch units.");
        }
    }
}

