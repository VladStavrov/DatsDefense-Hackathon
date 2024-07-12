package org.example.exeptions;

import lombok.Data;

@Data
public class ApiErrorResponse {
    public int errCode;
    public String error;
}