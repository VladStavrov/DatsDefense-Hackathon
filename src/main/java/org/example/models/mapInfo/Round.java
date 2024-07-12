package org.example.models.mapInfo;

import lombok.Data;

@Data
public class Round {
    public int duration;
    public String endAt;
    public String name;
    public int repeat;
    public String startAt;
    public String status;
}
