package org.example.models.mapInfo;

import lombok.Data;

@Data
public class Player {
    public int enemyBlockKills;
    public String gameEndedAt;
    public int gold;
    public String name;
    public int points;
    public int zombieKills;
}
