package org.example.models;

import lombok.Data;

@Data
public class Round {
    private int duration;
    private String endAt;
    private String name;
    private int repeat;
    private String startAt;
    private String status;
}
