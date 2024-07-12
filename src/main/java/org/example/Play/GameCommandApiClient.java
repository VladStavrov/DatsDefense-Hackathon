package org.example.Play;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.example.exeptions.ApiErrorResponse;
import org.example.exeptions.ApiException;
import org.example.models.play.CommandResponse;
import org.example.models.play.PlayRequest;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GameCommandApiClient {
    private static final String BASE_URL = "https://games-test.datsteam.dev/play/zombidef/units";
    private static final String API_KEY = "6684220d3195e6684220d31962";
    private static final String AUTH_HEADER = "X-Auth-Token";
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    public GameCommandApiClient() {
        this.httpClient = HttpClient.newHttpClient();
        this.objectMapper = new ObjectMapper();
    }
    public CommandResponse sendCommand(PlayRequest playRequest) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(playRequest);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL))
                .header(AUTH_HEADER, API_KEY)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        int statusCode = response.statusCode();



        if (statusCode == 200) {
            return objectMapper.readValue(response.body(), CommandResponse.class);
        } else if (statusCode == 400 || statusCode == 401 || statusCode == 403 || statusCode == 404 || statusCode == 429) {
            ApiErrorResponse errorResponse = objectMapper.readValue(response.body(), ApiErrorResponse.class);
            throw new ApiException(statusCode, errorResponse);
        } else {
            throw new RuntimeException("Unexpected response status: " + statusCode);
        }
    }

}
