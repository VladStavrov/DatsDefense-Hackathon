package org.example.models.mapInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
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
