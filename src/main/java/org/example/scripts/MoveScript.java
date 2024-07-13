package org.example.scripts;

import org.example.models.mapInfo.*;
import org.example.models.play.MoveBase;

import java.util.*;
import java.util.logging.Logger;

public class MoveScript {

    private static final Logger logger = Logger.getLogger(MoveScript.class.getName());

    // Метод для нахождения самой безопасной клетки базы
    public static Base findSafestBaseCell(InfoResponse infoResponse, int currentX, int currentY) {
        Base[] baseCells = infoResponse.getBase();
        List<EnemyBlock> enemyBlocks = Optional.ofNullable(infoResponse.getEnemyBlocks()).map(Arrays::asList).orElse(Collections.emptyList());
        List<Zombie> zombies = Optional.ofNullable(infoResponse.getZombies()).map(Arrays::asList).orElse(Collections.emptyList());

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
                Set<Coordinate> attackCells = getZombieAttackCells(zombie);
                if (attackCells.contains(new Coordinate(baseCell.getX(), baseCell.getY()))) {
                    dangerLevel += getZombieThreatLevel(zombie);
                }
            }
            dangerMap.put(baseCell, dangerLevel);
        }

        // Поиск клеток с наименьшим уровнем угрозы
        int minDangerLevel = Collections.min(dangerMap.values());
        int maxDangerLevel = Collections.max(dangerMap.values());
        List<Base> safestCells = new ArrayList<>();
        for (Map.Entry<Base, Integer> entry : dangerMap.entrySet()) {
            if (entry.getValue() == minDangerLevel) {
                safestCells.add(entry.getKey());
            }
        }

        Base currentBaseCell = Arrays.stream(baseCells).filter(base -> base.getX() == currentX && base.getY() == currentY).findFirst().orElseThrow(() -> new IllegalArgumentException("Current position is not part of the base"));

        // Логирование информации
        logger.info(String.format("Максимальный уровень опасности: %d", maxDangerLevel));
        logger.info(String.format("Минимальный уровень опасности: %d", minDangerLevel));
        logger.info(String.format("Начальная клетка: (%d, %d) с уровнем опасности: %d", currentX, currentY, dangerMap.get(currentBaseCell)));

        // Найти самую близкую безопасную клетку к текущей позиции
        Base safestBaseCell = Collections.min(safestCells, Comparator.comparingDouble(cell -> calculateDistance(currentX, currentY, cell.getX(), cell.getY())));

        // Логирование информации о безопасной клетке
        logger.info(String.format("Перемещение в клетку: (%d, %d) с уровнем опасности: %d", safestBaseCell.getX(), safestBaseCell.getY(), dangerMap.get(safestBaseCell)));

        return safestBaseCell;
    }

    // Метод для вычисления зоны атаки зомби
    private static Set<Coordinate> getZombieAttackCells(Zombie zombie) {
        Set<Coordinate> attackCells = new HashSet<>();
        int currentX = zombie.getX();
        int currentY = zombie.getY();
        int speed = zombie.getSpeed();
        String direction = zombie.getDirection();

        // Вычисление новой позиции зомби после движения
        int targetX = currentX;
        int targetY = currentY;
        switch (direction) {
            case "up" -> targetY -= speed;
            case "down" -> targetY += speed;
            case "left" -> targetX -= speed;
            case "right" -> targetX += speed;
        }

        switch (zombie.getType()) {
            case "normal", "fast" -> attackCells.add(new Coordinate(targetX, targetY));
            case "bomber" -> {
                for (int dx = -1; dx <= 1; dx++) {
                    for (int dy = -1; dy <= 1; dy++) {
                        attackCells.add(new Coordinate(targetX + dx, targetY + dy));
                    }
                }
            }
            case "liner" -> {
                int stepX = direction.equals("left") ? -1 : direction.equals("right") ? 1 : 0;
                int stepY = direction.equals("up") ? -1 : direction.equals("down") ? 1 : 0;
                for (int i = 0; i <= speed; i++) {
                    attackCells.add(new Coordinate(currentX + i * stepX, currentY + i * stepY));
                }
            }
            case "juggernaut" -> {
                for (int i = 1; i <= speed; i++) {
                    attackCells.add(new Coordinate(currentX + i * (direction.equals("right") ? 1 : direction.equals("left") ? -1 : 0), currentY + i * (direction.equals("down") ? 1 : direction.equals("up") ? -1 : 0)));
                }
            }
            case "chaos_knight" -> {
                int[][] knightMoves = {{2, 1}, {2, -1}, {-2, 1}, {-2, -1}, {1, 2}, {1, -2}, {-1, 2}, {-1, -2}};
                for (int[] move : knightMoves) {
                    attackCells.add(new Coordinate(currentX + move[0], currentY + move[1]));
                }
            }
        }

        return attackCells;
    }

    // Метод для вычисления расстояния между двумя точками
    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    // Метод для определения уровня угрозы от зомби, основываясь на его типе
    private static int getZombieThreatLevel(Zombie zombie) {
        return switch (zombie.getType()) {
            case "fast" -> 2;
            case "bomber" -> 5;
            case "liner" -> 3;
            case "juggernaut" -> 10;
            case "chaos_knight" -> 4;
            default -> 1;
        };
    }

    // Метод для перемещения базы в самую безопасную клетку
    public static MoveBase moveBaseToSafestCell(InfoResponse infoResponse, int currentX, int currentY) {
        Base safestBaseCell = findSafestBaseCell(infoResponse, currentX, currentY);
        MoveBase moveBase = new MoveBase();
        moveBase.setX(safestBaseCell.getX());
        moveBase.setY(safestBaseCell.getY());
        return moveBase;
    }

    // Класс для представления координат
    private static class Coordinate {
        int x, y;

        Coordinate(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Coordinate that = (Coordinate) o;
            return x == that.x && y == that.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
