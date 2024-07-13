package org.example.scripts;

import org.example.models.mapInfo.*;
import org.example.models.play.Build;
import org.example.models.worldInfo.WorldDataResponse;
import org.example.models.worldInfo.ZPot;

import java.util.*;

public class BuildScript {
    public static void main(String[] args) {
        build(null,null);
    }
    public static List<Build> build(InfoResponse infoResponse, WorldDataResponse worldDataResponse){
        List<Build> build;

        build = findNextBuildLocation(infoResponse,infoResponse.getPlayer().getGold(),worldDataResponse);
       // build = findNextBuildLocation(createRandomBases(15,5,12),35);
        return build;
    }


    /*public static List<Build> findNextBuildLocation(InfoResponse infoResponse,int money,WorldDataResponse worldDataResponse) {
        //printGrid(bases);
        Base[] bases = infoResponse.getBase();
        int startX = -1;
        int startY = -1;
        List<Build> build = new ArrayList<>();
        for (Base base : bases) {
            if (base.isHead() ) {
                startX = base.getX();
                startY = base.getY();
                System.out.println("Head: "+startX+" : "+startY);
                break;
            }
        }
        if (startX == -1 || startY == -1) {
            throw new IllegalArgumentException("No head base found");
        }
        int radius;
        int distanceEnemy = 5, distanceSpotsZombie = 0, distanceZombie = 2;
        while(money > 0 && distanceEnemy!=1){

            radius = 0;
            distanceEnemy--;
            distanceSpotsZombie--;
            if(distanceSpotsZombie<=0){
                distanceSpotsZombie=0;
            }
            distanceZombie--;
            if(distanceZombie<=0){
                distanceZombie=0;
            }

            while(money > 0 && radius<=200){
                radius++;
                int x,y;
                int maxX = startX + radius;
                int maxY = startY + radius;
                for (x=startX-radius;x<=maxX; x++) {
                    for (y=startY-radius;y<=maxY; y++) {
                        double distance = calculateDistance(startX,startY,x,y);
                        if (distance <= radius) {
                            if(checkCoordinates(x,y,infoResponse,worldDataResponse,build,distanceEnemy,distanceSpotsZombie,distanceZombie)){
                                System.out.println("Point: (" + x + ", " + y + ")");
                                build.add(createBase(x,y));
                                money--;
                                if (money<=0){
                                    return build;
                                }
                            }
                        }
                    }
                }
            }
        }
        return build;
    }*/
    /*public static List<Build> findNextBuildLocation(InfoResponse infoResponse, int money, WorldDataResponse worldDataResponse) {
        Base[] bases = infoResponse.getBase();
        int startX = -1;
        int startY = -1;
        List<Build> build = new ArrayList<>();
        int firstPartMoney = money/2;
        int secondPart = money - firstPartMoney;

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
        int distanceEnemy = 5, distanceSpotsZombie = 4, distanceZombie = 2;

        // PriorityQueue для мест размещения с учетом их безопасности и стратегической ценности
        PriorityQueue<Build> priorityQueue = new PriorityQueue<>(new BuildDistanceComparator(startX, startY));

        while (firstPartMoney > 0 && distanceEnemy != 1) {
            radius = 0;
            distanceEnemy--;
            distanceSpotsZombie--;
            if (distanceSpotsZombie <= 0) {
                distanceSpotsZombie = 0;
            }
            distanceZombie--;
            if (distanceZombie <= 0) {
                distanceZombie = 0;
            }

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
                                System.out.println("Point: (" + x + ", " + y + ")");
                                priorityQueue.add(createBase(x, y));
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
*/
    /*public static List<Build> findNextBuildLocation(InfoResponse infoResponse, int money, WorldDataResponse worldDataResponse) {
        Base[] bases = infoResponse.getBase();
        int startX = -1;
        int startY = -1;
        List<Build> build = new ArrayList<>();
        int firstPartMoney = money / 2;
        int secondPartMoney = money - firstPartMoney;

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
        int distanceEnemy = 5, distanceSpotsZombie = 4, distanceZombie = 2;

        // PriorityQueue для мест размещения с учетом их безопасности и стратегической ценности
        PriorityQueue<Build> priorityQueue = new PriorityQueue<>(new BuildDistanceComparator(startX, startY));

        // Первая часть денег: строительство от центра
        while (firstPartMoney > 0 && distanceEnemy != 1) {
            radius = 0;
            distanceEnemy--;
            distanceSpotsZombie--;
            if (distanceSpotsZombie <= 0) {
                distanceSpotsZombie = 0;
            }
            distanceZombie--;
            if (distanceZombie <= 0) {
                distanceZombie = 0;
            }

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
                                System.out.println("Point: (" + x + ", " + y + ")");
                                priorityQueue.add(createBase(x, y));
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

        // Найти самую дальнюю точку от центра
        Base furthestBase = findFurthestBase(startX, startY, infoResponse);

        // Вторая часть денег: строительство от самой дальней точки
        if (furthestBase != null) {
            startX = furthestBase.getX();
            startY = furthestBase.getY();
            distanceEnemy = 5;
            distanceSpotsZombie = 4;
            distanceZombie = 2;

            priorityQueue = new PriorityQueue<>(new BuildDistanceComparator(startX, startY));

            while (secondPartMoney > 0 && distanceEnemy != 1) {
                radius = 0;
                distanceEnemy--;
                distanceSpotsZombie--;
                if (distanceSpotsZombie <= 0) {
                    distanceSpotsZombie = 0;
                }
                distanceZombie--;
                if (distanceZombie <= 0) {
                    distanceZombie = 0;
                }

                while (secondPartMoney > 0 && radius <= 200) {
                    radius++;
                    int x, y;
                    int maxX = startX + radius;
                    int maxY = startY + radius;

                    for (x = startX - radius; x <= maxX; x++) {
                        for (y = startY - radius; y <= maxY; y++) {
                            double distance = calculateDistance(startX, startY, x, y);
                            if (distance <= radius) {
                                if (checkCoordinates(x, y, infoResponse, worldDataResponse, build, distanceEnemy, distanceSpotsZombie, distanceZombie)) {
                                    System.out.println("Point: (" + x + ", " + y + ")");
                                    priorityQueue.add(createBase(x, y));
                                }
                            }
                        }
                    }

                    while (!priorityQueue.isEmpty() && secondPartMoney > 0) {
                        Build nextBuild = priorityQueue.poll();
                        build.add(nextBuild);
                        secondPartMoney--;
                    }
                }
            }
        }

        return build;
    }*/
    public static List<Build> findNextBuildLocation(InfoResponse infoResponse, int money, WorldDataResponse worldDataResponse) {
        Base[] bases = infoResponse.getBase();
        int startX = -1;
        int startY = -1;
        List<Build> build = new ArrayList<>();
        int firstPartMoney = money / 2;
        int secondPartMoney = money - firstPartMoney;

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

        // Первая часть денег: строительство от центра
        buildFromLocation(startX, startY, firstPartMoney, infoResponse, worldDataResponse, build);

        // Найти самую дальнюю точку от центра
        Base furthestBase = findFurthestBase(startX, startY, infoResponse);

        // Вторая часть денег: строительство от самой дальней точки
        if (furthestBase != null) {
            buildFromLocation(furthestBase.getX(), furthestBase.getY(), secondPartMoney, infoResponse, worldDataResponse, build);
        }

        return build;
    }

    private static void buildFromLocation(int startX, int startY, int money, InfoResponse infoResponse, WorldDataResponse worldDataResponse, List<Build> build) {
        int radius;
        int distanceEnemy = 5;
        int distanceSpotsZombie = 4;
        int distanceZombie = 2;

        PriorityQueue<Build> priorityQueue = new PriorityQueue<>(new BuildDistanceComparator(startX, startY));

        while (money > 0 && distanceEnemy != 1) {
            radius = 0;
            distanceEnemy--;
            distanceSpotsZombie--;
            if (distanceSpotsZombie <= 0) {
                distanceSpotsZombie = 0;
            }
            distanceZombie--;
            if (distanceZombie <= 0) {
                distanceZombie = 0;
            }

            while (money > 0 && radius <= 200) {
                radius++;
                int x, y;
                int maxX = startX + radius;
                int maxY = startY + radius;

                for (x = startX - radius; x <= maxX; x++) {
                    for (y = startY - radius; y <= maxY; y++) {
                        double distance = calculateDistance(startX, startY, x, y);
                        if (distance <= radius) {
                            if (checkCoordinates(x, y, infoResponse, worldDataResponse, build, distanceEnemy, distanceSpotsZombie, distanceZombie)) {
                                System.out.println("Point: (" + x + ", " + y + ")");
                                priorityQueue.add(createBase(x, y));
                            }
                        }
                    }
                }

                while (!priorityQueue.isEmpty() && money > 0) {
                    Build nextBuild = priorityQueue.poll();
                    build.add(nextBuild);
                    money--;
                }
            }
        }
    }



    public static Base findFurthestBase(int startX, int startY, InfoResponse infoResponse) {
        Base[] bases = infoResponse.getBase();
        Base furthestBase = null;
        double maxDistance = -1;

        for (Base base : bases) {
            double distance = calculateDistance(startX, startY, base.getX(), base.getY());
            if (distance > maxDistance) {
                maxDistance = distance;
                furthestBase = base;
            }
        }

        return furthestBase;
    }
    public static boolean checkCoordinates(int x, int y, InfoResponse infoResponse,WorldDataResponse worldDataResponse,
                                           List<Build> currentBuild, int distanceEnemy, int distanceSpotsZombie,
                                           int distanceZombie) {
        if (x < 0 || y < 0) {
            return false;
        }
        double distance = 1000;
        double newDistance = 1000;
        if (!currentBuild.isEmpty()){
            for (Build build:
                 currentBuild) {
                if(build.getX() == x && build.getY() == y){
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
        if(distance > 1){
            return false;
        }

        //Zombie
        if (infoResponse != null && infoResponse.getZombies() != null) {
            for (Zombie zombie : infoResponse.getZombies()) {
                if (zombie.x == x && zombie.y == y) {
                    return false;
                }
                if (Math.sqrt(Math.pow(zombie.x - x, 2) + Math.pow(zombie.y - y, 2))<=distanceZombie) {
                    return false;
                }
            }
        }

        //Enemy
        if (infoResponse != null && infoResponse.getEnemyBlocks() != null) {
            for (EnemyBlock enemyBlock : infoResponse.getEnemyBlocks()) {
                int enemyX = enemyBlock.getX();
                int enemyY = enemyBlock.getY();
                if (Math.sqrt(Math.pow(enemyX - x, 2) + Math.pow(enemyY - y, 2))<=distanceEnemy) {
                    return false;
                }
            }
        }

        //Spots
        if (worldDataResponse != null && worldDataResponse.getZpots() != null) {
            for (ZPot zpot : worldDataResponse.getZpots()) {
                int zpotX = zpot.getX();
                int zpotY = zpot.getY();
                if (Math.sqrt(Math.pow(zpotX - x, 2) + Math.pow(zpotY - y, 2))<=distanceSpotsZombie) {
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