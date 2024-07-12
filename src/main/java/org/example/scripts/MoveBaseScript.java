package org.example.scripts;

import org.example.models.mapInfo.Base;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.play.MoveBase;
import org.example.models.worldInfo.WorldDataResponse;

import java.util.HashSet;
import java.util.Set;

public class MoveBaseScript {
    public static MoveBase moveBase(InfoResponse infoResponse, WorldDataResponse worldDataResponse){
        MoveBase moveBase = new MoveBase();
        //
        return moveBase;
    }
    //TODO не сильно тестил
    public static int[] findSafestCoordinates(Base[] bases) {
        Set<String> zone = new HashSet<>();
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        int maxX = Integer.MIN_VALUE;
        int maxY = Integer.MIN_VALUE;

        for (Base base : bases) {
            zone.add(base.x + "," + base.y);
            minX = Math.min(minX, base.x);
            minY = Math.min(minY, base.y);
            maxX = Math.max(maxX, base.x);
            maxY = Math.max(maxY, base.y);
        }

        int[] safestCoordinates = new int[2];
        int maxDistance = -1;

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                if (!zone.contains(x + "," + y)) continue;

                int distance = Integer.MAX_VALUE;
                for (Base base : bases) {
                    distance = Math.min(distance, Math.abs(base.x - x) + Math.abs(base.y - y));
                }

                if (distance > maxDistance) {
                    maxDistance = distance;
                    safestCoordinates[0] = x;
                    safestCoordinates[1] = y;
                }
            }
        }

        return safestCoordinates;
    }
}
