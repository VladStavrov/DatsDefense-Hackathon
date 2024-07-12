package org.example.exeptions;

import lombok.Getter;

@Getter
public class ApiException extends Exception {
    private final int statusCode;
    private final ApiErrorResponse errorResponse;

    public ApiException(int statusCode, ApiErrorResponse errorResponse) {
        super("API returned status code " + statusCode + " with error: " + errorResponse.error);
        this.statusCode = statusCode;
        this.errorResponse = errorResponse;
    }
}