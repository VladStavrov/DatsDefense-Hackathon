package org.example.models.mapInfo;

import lombok.Data;

@Data
public class EnemyBlock {
    public int attack;
    public int health;
    public boolean isHead;
    public LastAttack lastAttack;
    public String name;
    public int x;
    public int y;
}
