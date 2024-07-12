package org.example.models.mapInfo;

import lombok.Data;

@Data
public class Base {
    public int attack;
    public int health;
    public String id;
    public boolean isHead;
    public LastAttack lastAttack;
    public int range;
    public int x;
    public int y;
}
