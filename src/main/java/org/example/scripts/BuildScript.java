package org.example.scripts;

import org.example.models.mapInfo.*;
import org.example.models.play.Build;
import org.example.models.worldInfo.WorldDataResponse;
import org.example.models.worldInfo.ZPot;

import java.util.*;

public class BuildScript {
    public static List<Build> build(InfoResponse infoResponse, WorldDataResponse worldDataResponse) {
        List<Build> build;

        build = findNextBuildLocation(infoResponse, infoResponse.getPlayer().getGold(), worldDataResponse);
        return build;
    }

    public static List<Build> findNextBuildLocation(InfoResponse infoResponse, int money, WorldDataResponse worldDataResponse) {
        Base[] bases = infoResponse.getBase();
        int startX = -1;
        int startY = -1;
        List<Build> build = new ArrayList<>();
        int firstPartMoney = money / 2;

        // Найти базу с центром управления
        for (Base base : bases) {
            if (base.isHead()) {
                startX = base.getX();
                startY = base.getY();
                System.out.println("Head: " + startX + " : " + startY);
                break;
            }
        }

        if (startX == -1 || startY == -1) {
            throw new IllegalArgumentException("No head base found");
        }

        int radius;
        int distanceEnemy = 5, distanceSpotsZombie = 1, distanceZombie = 1;

        // PriorityQueue для мест размещения с учетом их безопасности и стратегической ценности
        PriorityQueue<Build> priorityQueue = new PriorityQueue<>(new BuildDistanceComparator(startX, startY));

        while (firstPartMoney > 0 && distanceEnemy != 1) {
            radius = 0;
            distanceEnemy--;


            while (firstPartMoney > 0 && radius <= 200) {
                radius++;
                int x, y;
                int maxX = startX + radius;
                int maxY = startY + radius;

                for (x = startX - radius; x <= maxX; x++) {
                    for (y = startY - radius; y <= maxY; y++) {
                        double distance = calculateDistance(startX, startY, x, y);
                        if (distance <= radius) {
                            if (checkCoordinates(x, y, infoResponse, worldDataResponse, build, distanceEnemy, distanceSpotsZombie, distanceZombie)) {
                                // Пропускаем координаты, если обе четные
                                if (!shouldSkipCoordinates(x, y)) {
                                    System.out.println("Point: (" + x + ", " + y + ")");
                                    priorityQueue.add(createBase(x, y));
                                }
                            }
                        }
                    }
                }

                while (!priorityQueue.isEmpty() && firstPartMoney > 0) {
                    Build nextBuild = priorityQueue.poll();
                    build.add(nextBuild);
                    firstPartMoney--;
                }
            }
        }

        return build;
    }

    // Метод для фильтрации координат
    public static boolean shouldSkipCoordinates(int x, int y) {
        return false;
        //return (x % 2 == 0) && (y % 2 == 0);
    }

    public static boolean checkCoordinates(int x, int y, InfoResponse infoResponse, WorldDataResponse worldDataResponse, List<Build> currentBuild, int distanceEnemy, int distanceSpotsZombie, int distanceZombie) {
        if (x < 0 || y < 0) {
            return false;
        }
        double distance = 1000;
        double newDistance;
        if (!currentBuild.isEmpty()) {
            for (Build build : currentBuild) {
                if (build.getX() == x && build.getY() == y) {
                    return false;
                }
            }
        }
        for (Base base : infoResponse.getBase()) {
            if (base.x == x && base.y == y) {
                return false;
            }

            newDistance = Math.sqrt(Math.pow(base.x - x, 2) + Math.pow(base.y - y, 2));
            if (newDistance < distance) {
                distance = newDistance;
            }
        }
        if (distance > 1) {
            return false;
        }

        // Zombie
        if (infoResponse.getZombies() != null) {
            for (Zombie zombie : infoResponse.getZombies()) {
                if (zombie.x == x && zombie.y == y) {
                    return false;
                }
                if (Math.sqrt(Math.pow(zombie.x - x, 2) + Math.pow(zombie.y - y, 2)) <= distanceZombie) {
                    return false;
                }
            }
        }

        // Enemy
        if (infoResponse.getEnemyBlocks() != null) {
            for (EnemyBlock enemyBlock : infoResponse.getEnemyBlocks()) {
                int enemyX = enemyBlock.getX();
                int enemyY = enemyBlock.getY();
                if (Math.sqrt(Math.pow(enemyX - x, 2) + Math.pow(enemyY - y, 2)) <= distanceEnemy) {
                    return false;
                }
            }
        }

        // Spots
        if (worldDataResponse != null && worldDataResponse.getZpots() != null) {
            for (ZPot zpot : worldDataResponse.getZpots()) {
                int zpotX = zpot.getX();
                int zpotY = zpot.getY();
                if (Math.sqrt(Math.pow(zpotX - x, 2) + Math.pow(zpotY - y, 2)) <= distanceSpotsZombie) {
                    return false;
                }

                if (zpotX == x && zpotY == y) {
                    return false;
                }
                if (zpotX == x - 1 && zpotY == y) {
                    return false;
                }
                if (zpotX == x + 1 && zpotY == y) {
                    return false;
                }
                if (zpotX == x && zpotY == y - 1) {
                    return false;
                }
                if (zpotX == x && zpotY == y + 1) {
                    return false;
                }
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
