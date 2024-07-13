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

        Map<Base, Double> dangerMap = new HashMap<>();

        // Оценка угрозы от EnemyBlock и зомби для каждой клетки базы
        for (Base baseCell : baseCells) {
            double dangerLevel = 0;

            for (EnemyBlock enemyBlock : enemyBlocks) {
                double distance = calculateDistance(baseCell.getX(), baseCell.getY(), enemyBlock.getX(), enemyBlock.getY());
                dangerLevel += 1 / distance; // Чем ближе враг, тем выше угроза
            }

            for (Zombie zombie : zombies) {
                double distance = calculateDistance(baseCell.getX(), baseCell.getY(), zombie.getX(), zombie.getY());
                dangerLevel += 1 / distance; // Чем ближе зомби, тем выше угроза
            }

            dangerMap.put(baseCell, dangerLevel);
        }

        // Поиск клеток с наименьшим уровнем угрозы
        double minDangerLevel = Collections.min(dangerMap.values());
        double maxDangerLevel = Collections.max(dangerMap.values());
        List<Base> safestCells = new ArrayList<>();
        for (Map.Entry<Base, Double> entry : dangerMap.entrySet()) {
            if (entry.getValue() == minDangerLevel) {
                safestCells.add(entry.getKey());
            }
        }

        Base currentBaseCell = Arrays.stream(baseCells).filter(base -> base.getX() == currentX && base.getY() == currentY).findFirst().orElseThrow(() -> new IllegalArgumentException("Current position is not part of the base"));

        double currentDangerLevel = dangerMap.get(currentBaseCell);

        // Логирование информации
        logger.info(String.format("Максимальный уровень опасности: %.2f", maxDangerLevel));
        logger.info(String.format("Минимальный уровень опасности: %.2f", minDangerLevel));
        logger.info(String.format("Начальная клетка: (%d, %d) с уровнем опасности: %.2f", currentX, currentY, currentDangerLevel));

        // Проверка разницы между текущей опасностью и минимальной опасностью
        if (currentDangerLevel - minDangerLevel < 1) {
            logger.info("Разница между текущей и минимальной опасностью < 1. Остаемся на текущих координатах.");
            return currentBaseCell;
        }

        // Найти самую близкую безопасную клетку к текущей позиции
        Base safestBaseCell = Collections.min(safestCells, Comparator.comparingDouble(cell -> calculateDistance(currentX, currentY, cell.getX(), cell.getY())));

        // Логирование информации о безопасной клетке
        logger.info(String.format("Перемещение в клетку: (%d, %d) с уровнем опасности: %.2f", safestBaseCell.getX(), safestBaseCell.getY(), dangerMap.get(safestBaseCell)));

        return safestBaseCell;
    }

    // Метод для вычисления расстояния между двумя точками
    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    // Метод для перемещения базы в самую безопасную клетку
    public static MoveBase moveBaseToSafestCell(InfoResponse infoResponse, int currentX, int currentY) {
        Base safestBaseCell = findSafestBaseCell(infoResponse, currentX, currentY);
        MoveBase moveBase = new MoveBase();
        moveBase.setX(safestBaseCell.getX());
        moveBase.setY(safestBaseCell.getY());
        return moveBase;
    }
}
