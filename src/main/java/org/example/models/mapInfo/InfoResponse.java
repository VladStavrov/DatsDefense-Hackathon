package org.example.models.mapInfo;

import lombok.Data;

@Data
public class InfoResponse {
    public Base[] base;
    public EnemyBlock[] enemyBlocks;
    public Player player;
    public String realmName;
    public int turn;
    public long turnEndsInMs;
    public Zombie[] zombies;
}
