package org.example.scripts;

import org.example.models.mapInfo.*;
import org.example.models.play.MoveBase;

import java.util.*;

public class MoveScript {

    // Метод для нахождения самой безопасной клетки базы
    public static Base findSafestBaseCell(InfoResponse infoResponse) {
        Base[] baseCells = infoResponse.getBase();
        List<EnemyBlock> enemyBlocks = infoResponse.getEnemyBlocks() != null ? Arrays.asList(infoResponse.getEnemyBlocks()) : Collections.emptyList();
        List<Zombie> zombies = infoResponse.getZombies() != null ? Arrays.asList(infoResponse.getZombies()) : Collections.emptyList();

        Map<Base, Integer> dangerMap = new HashMap<>();

        // Оценка угрозы от EnemyBlock для каждой клетки базы
        for (Base baseCell : baseCells) {
            int dangerLevel = 0;
            for (EnemyBlock enemyBlock : enemyBlocks) {
                double distance = calculateDistance(baseCell.getX(), baseCell.getY(), enemyBlock.getX(), enemyBlock.getY());
                if (distance <= baseCell.getRange()) {
                    dangerLevel++;
                }
            }
            dangerMap.put(baseCell, dangerLevel);
        }

        // Оценка угрозы от зомби для каждой клетки базы
        for (Base baseCell : baseCells) {
            int dangerLevel = dangerMap.getOrDefault(baseCell, 0);
            for (Zombie zombie : zombies) {
                if (canZombieReachBaseCell(zombie, baseCell)) {
                    dangerLevel++;
                }
            }
            dangerMap.put(baseCell, dangerLevel);
        }

        // Поиск клетки с наименьшим уровнем угрозы
        return Collections.min(dangerMap.entrySet(), Map.Entry.comparingByValue()).getKey();
    }

    // Метод для проверки, может ли зомби достичь клетки базы
    private static boolean canZombieReachBaseCell(Zombie zombie, Base baseCell) {
        int targetX = baseCell.getX();
        int targetY = baseCell.getY();
        int currentX = zombie.getX();
        int currentY = zombie.getY();
        int speed = zombie.getSpeed();
        String direction = zombie.getDirection();

        return switch (direction) {
            case "up" -> currentX == targetX && currentY - speed <= targetY;
            case "down" -> currentX == targetX && currentY + speed >= targetY;
            case "left" -> currentY == targetY && currentX - speed <= targetX;
            case "right" -> currentY == targetY && currentX + speed >= targetX;
            default -> false;
        };
    }

    // Метод для вычисления расстояния между двумя точками
    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    // Метод для перемещения базы в самую безопасную клетку
    public static MoveBase moveBaseToSafestCell(InfoResponse infoResponse) {
        Base safestBaseCell = findSafestBaseCell(infoResponse);
        MoveBase moveBase = new MoveBase();
        moveBase.setX(safestBaseCell.getX());
        moveBase.setY(safestBaseCell.getY());
        return moveBase;
    }
}
