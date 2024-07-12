package org.example.models.mapInfo;

import lombok.Data;

import java.util.List;

@Data
public class ZombieDefResponse {
    public String gameName;
    public String now;
    public List<Round> rounds;
}
