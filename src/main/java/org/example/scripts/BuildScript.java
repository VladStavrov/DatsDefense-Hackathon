package org.example.scripts;

import org.example.models.mapInfo.Base;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.play.Build;
import org.example.models.worldInfo.WorldDataResponse;

import java.util.*;

public class BuildScript {

    public static List<Build> build(InfoResponse infoResponse, WorldDataResponse worldDataResponse) {
        List<Build> build;
        build = findNextBuildLocation(infoResponse.getBase(), infoResponse.getPlayer().getGold());
        return build;
    }

    public static List<Build> findNextBuildLocation(Base[] bases, int money) {
        int startX = -1;
        int startY = -1;
        List<Build> build = new ArrayList<>();
        for (Base base : bases) {
            if (base.isHead()) {
                startX = base.getX();
                startY = base.getY();
            }
        }

        if (startX == -1 || startY == -1) {
            throw new IllegalArgumentException("No head base found");
        }
        int radius = 0;

        while (money > 0) {
            radius++;
            int x = startX - radius;
            int y = startY - radius;

            int maxX = startX + radius;

            int maxY = startY + radius;
            for (; x < maxX; x++) {
                for (; y < maxY; y++) {
                    double distance = calculateDistance(startX, startY, x, y);

                    if (distance <= radius) {
                        if (checkCoordinates(x, y, bases)) {
                            System.out.println("Point: (" + x + ", " + y + ")");
                            build.add(createBase(x, y));
                            money--;
                            if (money <= 0) {
                                return build;
                            }
                        }
                    }
                }
            }

        }

        return build;
    }

    public static boolean checkCoordinates(int x, int y, Base[] bases) {
        if (x < 0 || y < 0) {
            return false;
        }
        for (Base base : bases) {
            if (base.x == x && base.y == y) {
                return false;
            }
        }

        return true;
    }

    private static Build createBase(int x, int y) {
        Build base = new Build();
        base.setX(x);
        base.setY(y);
        return base;
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}
