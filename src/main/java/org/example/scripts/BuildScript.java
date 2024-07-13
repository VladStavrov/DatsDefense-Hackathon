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


    public static List<Build> findNextBuildLocation(InfoResponse infoResponse,int money,WorldDataResponse worldDataResponse) {
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
        int distanceEnemy = 5, distanceSpotsZombie = 4, distanceZombie = 2;
        while(money > 0 && distanceZombie!=0){
            radius = 0;
            distanceEnemy--;
            distanceSpotsZombie--;
            distanceZombie--;
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


      //  printGrid(bases);
        return build;
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


    //
    // Test
    //
    public static Base[] addBaseToArray(Base[] bases, Base newBase) {
        Base[] newBases = new Base[bases.length + 1];
        System.arraycopy(bases, 0, newBases, 0, bases.length);
        newBases[bases.length] = newBase;
        return newBases;
    }

    public static void printGrid(Base[] bases) {
        int width = getMaxX(bases)+1;
        int height = getMaxY(bases)+1;
        char[][] grid = new char[height][width];


        for (int i = 0; i < height; i++) {
            Arrays.fill(grid[i], '.');
        }


        for (Base base : bases) {
            if (base.x < width && base.y < height) {
                if (base.isHead==true){
                    grid[base.y][base.x] = '&';
                } else if (base.getRange()==77777) {
                    grid[base.y][base.x] = '0';
                }
                else{
                    grid[base.y][base.x] = '*';
                }

            }
        }

        // Вывод сетки на консоль
        for (int i = 0; i < width; i++){
            System.out.print(i + " ");
        }
        System.out.println();
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println("  "+ i);

        }
    }

    public static int getMaxX(Base[] bases) {
        int maxX = Integer.MIN_VALUE;
        for (Base base : bases) {
            if (base.x > maxX) {
                maxX = base.x;
            }
        }
        return maxX;
    }

    public static int getMaxY(Base[] bases) {
        int maxY = Integer.MIN_VALUE;
        for (Base base : bases) {
            if (base.y > maxY) {
                maxY = base.y;
            }
        }
        return maxY;
    }
    public static Base[] createRandomBases(int numberOfBases, int maxX, int maxY) {
        Base[] bases = new Base[numberOfBases];
        Random rand = new Random();
        int headIndex = rand.nextInt(numberOfBases);

        for (int i = 0; i < numberOfBases; i++) {
            int attack = rand.nextInt(100);
            int health = rand.nextInt(100);
            String id = "id" + i;
            boolean isHead = (i == headIndex);
            LastAttack lastAttack = new LastAttack();
            int range = rand.nextInt(10);
            int x = rand.nextInt(maxX);
            int y = rand.nextInt(maxY);
            bases[i] = new Base(attack, health, id, isHead, lastAttack, range, x, y);
        }

        return bases;
    }
}