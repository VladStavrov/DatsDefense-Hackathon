package org.example.models;

import lombok.Data;

import java.util.List;

@Data
public class RoundsResponse {
    private String gameName;
    private String now;
    private List<Round> rounds;
}

