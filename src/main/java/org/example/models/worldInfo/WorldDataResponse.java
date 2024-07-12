package org.example.models.worldInfo;

import lombok.Data;

import java.util.List;

@Data
public class WorldDataResponse {
    private String realmName;
    private List<ZPot> zpots;
}
