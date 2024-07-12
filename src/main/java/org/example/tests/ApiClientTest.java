package org.example.tests;

import org.example.ApiClient;

import java.util.HashMap;
import java.util.Map;

public class ApiClientTest {

    public static void main(String[] args) {
        // Создаем экземпляр ApiClient
        ApiClient apiClient = new ApiClient();

        try {
            // Пример GET-запроса
            String getResponse = apiClient.doGet("/example-endpoint");
            System.out.println("GET Response: " + getResponse);

            // Пример POST-запроса
            String jsonBody = "{\"key\":\"value\"}";
            String postResponse = apiClient.doPost("/example-endpoint", jsonBody);
            System.out.println("POST Response: " + postResponse);

            // Пример PUT-запроса
            String putResponse = apiClient.doPut("/example-endpoint");
            System.out.println("PUT Response: " + putResponse);

            // Пример PATCH-запроса
            String patchResponse = apiClient.doPatch("/example-endpoint", jsonBody);
            System.out.println("PATCH Response: " + patchResponse);

            // Пример DELETE-запроса
            String deleteResponse = apiClient.doDelete("/example-endpoint");
            System.out.println("DELETE Response: " + deleteResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
