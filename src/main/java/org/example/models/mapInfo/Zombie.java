package org.example.models.mapInfo;

import lombok.Data;

@Data
public class Zombie {
    public int attack;
    public String direction;
    public int health;
    public String id;
    public int speed;
    public String type;
    public int waitTurns;
    public int x;
    public int y;
}
