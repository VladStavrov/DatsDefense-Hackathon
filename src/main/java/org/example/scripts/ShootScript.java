package org.example.scripts;

import org.example.models.mapInfo.Base;
import org.example.models.mapInfo.EnemyBlock;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.mapInfo.Zombie;
import org.example.models.play.Attack;
import org.example.models.play.Target;

import java.util.*;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class ShootScript {

    private static final Logger logger = Logger.getLogger(ShootScript.class.getName());

    public static List<Attack> shoot(InfoResponse infoResponse) {
        List<Attack> attacks = new ArrayList<>();
        logger.info("Начало процесса атаки");

        // Найти центральный блок базы (head)
        Base centerBaseBlock = findCenterBaseBlock(infoResponse.getBase());

        if (centerBaseBlock == null) {
            logger.warning("Центральный блок базы не найден, атака невозможна");
            return attacks;
        }

        int centerX = centerBaseBlock.getX();
        int centerY = centerBaseBlock.getY();

        // Карта зомби по их координатам
        Map<String, List<Zombie>> zombiesByLocation = mapZombiesByLocation(Arrays.asList(infoResponse.getZombies()));

        // Сортировка зомби по расстоянию от центра
        List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation = sortZombiesByDistance(centerX, centerY, zombiesByLocation);

        List<Base> remainingBaseBlocks = new ArrayList<>(Arrays.asList(infoResponse.getBase()));
        List<EnemyBlock> highPriorityEnemyBlocks = findHighPriorityEnemyBlocks(Arrays.asList(infoResponse.getEnemyBlocks()));
        List<EnemyBlock> remainingEnemyBlocks = new ArrayList<>(Arrays.asList(infoResponse.getEnemyBlocks()));

        int highPriorityAttacks = 0;
        int totalEnemyBlockAttacks = 0;

        // Выполнение атак по EnemyBlock с атакой 40
        if (!highPriorityEnemyBlocks.isEmpty()) {
            highPriorityAttacks = executeHighPriorityEnemyBlockAttacks(attacks, remainingBaseBlocks, highPriorityEnemyBlocks);
            remainingEnemyBlocks.removeAll(highPriorityEnemyBlocks);
        }

        // Выполнение атак по зомби
        List<Zombie> remainingZombies = new ArrayList<>(Arrays.asList(infoResponse.getZombies()));
        executeZombieAttacks(attacks, remainingBaseBlocks, sortedZombiesByLocation, remainingZombies);

        // Обновить массив зомби после атак
        List<Map.Entry<String, List<Zombie>>> updatedZombiesByLocation = new ArrayList<>(mapZombiesByLocation(remainingZombies).entrySet());

        // Выполнение атак по обычным EnemyBlock и оставшимся зомби
        if (!remainingEnemyBlocks.isEmpty()) {
            totalEnemyBlockAttacks = executeEnemyBlockAttacks(attacks, remainingBaseBlocks, remainingEnemyBlocks, updatedZombiesByLocation);
        }

        logAttackSummary(attacks, remainingZombies, infoResponse, highPriorityAttacks, totalEnemyBlockAttacks);

        logger.info("Процесс атаки завершен");
        return attacks;
    }

    private static Base findCenterBaseBlock(Base[] baseBlocks) {
        for (Base base : baseBlocks) {
            if (base.isHead()) {
                return base;
            }
        }
        logger.warning("Центральный блок базы не найден");
        return null;
    }

    private static Map<String, List<Zombie>> mapZombiesByLocation(List<Zombie> zombies) {
        return zombies.stream().collect(Collectors.groupingBy(zombie -> zombie.getX() + "," + zombie.getY()));
    }

    private static List<Map.Entry<String, List<Zombie>>> sortZombiesByDistance(int centerX, int centerY, Map<String, List<Zombie>> zombiesByLocation) {
        return zombiesByLocation.entrySet().stream().sorted(Comparator.comparingDouble(entry -> calculateDistance(centerX, centerY, entry.getValue().get(0).getX(), entry.getValue().get(0).getY()))).collect(Collectors.toList());
    }

    private static void executeZombieAttacks(List<Attack> attacks, List<Base> baseBlocks, List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation, List<Zombie> remainingZombies) {
        Iterator<Base> baseIterator = baseBlocks.iterator();

        while (baseIterator.hasNext()) {
            Base baseBlock = baseIterator.next();
            int baseX = baseBlock.getX();
            int baseY = baseBlock.getY();
            int attackRadius = baseBlock.getRange();
            int attackPower = baseBlock.getAttack();

            boolean attacked = false;

            for (Map.Entry<String, List<Zombie>> entry : sortedZombiesByLocation) {
                List<Zombie> zombiesInLocation = entry.getValue();
                int targetX = zombiesInLocation.get(0).getX();
                int targetY = zombiesInLocation.get(0).getY();
                double distance = calculateDistance(baseX, baseY, targetX, targetY);

                if (distance <= attackRadius) {
                    shootZombies(attacks, baseBlock, attackPower, zombiesInLocation, remainingZombies);
                    attacked = true;
                    baseIterator.remove();  // Удалить блок базы после атаки
                    break;
                }
            }

            if (attacked) break;
        }
    }

    private static int executeHighPriorityEnemyBlockAttacks(List<Attack> attacks, List<Base> baseBlocks, List<EnemyBlock> enemyBlocks) {
        int highPriorityAttacks = 0;

        Iterator<Base> baseIterator = baseBlocks.iterator();

        while (baseIterator.hasNext()) {
            Base baseBlock = baseIterator.next();
            int baseX = baseBlock.getX();
            int baseY = baseBlock.getY();
            int attackRadius = baseBlock.getRange();

            boolean attacked = false;

            for (EnemyBlock enemyBlock : enemyBlocks) {
                double distance = calculateDistance(baseX, baseY, enemyBlock.getX(), enemyBlock.getY());
                if (distance <= attackRadius) {
                    shootEnemyBlock(attacks, baseBlock, enemyBlock);
                    highPriorityAttacks++;
                    attacked = true;
                    baseIterator.remove();  // Удалить блок базы после атаки
                    break;
                }
            }

            if (attacked) break;
        }

        return highPriorityAttacks;
    }

    private static int executeEnemyBlockAttacks(List<Attack> attacks, List<Base> baseBlocks, List<EnemyBlock> enemyBlocks, List<Map.Entry<String, List<Zombie>>> updatedZombiesByLocation) {
        int totalEnemyBlockAttacks = 0;

        for (Base baseBlock : baseBlocks) {
            int baseX = baseBlock.getX();
            int baseY = baseBlock.getY();
            int attackRadius = baseBlock.getRange();
            int attackPower = baseBlock.getAttack();

            boolean attacked = false;

            // Атакуем обычные EnemyBlock
            for (EnemyBlock enemyBlock : enemyBlocks) {
                double distance = calculateDistance(baseX, baseY, enemyBlock.getX(), enemyBlock.getY());
                if (distance <= attackRadius) {
                    shootEnemyBlock(attacks, baseBlock, enemyBlock);
                    totalEnemyBlockAttacks++;
                    attacked = true;
                    break;
                }
            }

            if (attacked) continue;

            // Если нет EnemyBlock, атакуем зомби
            for (Map.Entry<String, List<Zombie>> entry : updatedZombiesByLocation) {
                List<Zombie> zombiesInLocation = entry.getValue();
                int targetX = zombiesInLocation.get(0).getX();
                int targetY = zombiesInLocation.get(0).getY();
                double distance = calculateDistance(baseX, baseY, targetX, targetY);

                if (distance <= attackRadius) {
                    shootZombies(attacks, baseBlock, attackPower, zombiesInLocation, new ArrayList<>(zombiesInLocation));
                    attacked = true;
                    break;
                }
            }
        }

        return totalEnemyBlockAttacks;
    }

    private static List<EnemyBlock> findHighPriorityEnemyBlocks(List<EnemyBlock> enemyBlocks) {
        return enemyBlocks.stream().filter(enemyBlock -> enemyBlock.getAttack() == 40).collect(Collectors.toList());
    }

    private static void shootZombies(List<Attack> attacks, Base baseBlock, int attackPower, List<Zombie> zombiesInLocation, List<Zombie> remainingZombies) {
        int targetX = zombiesInLocation.get(0).getX();
        int targetY = zombiesInLocation.get(0).getY();
        Attack attack = new Attack();
        attack.setBlockId(baseBlock.getId());
        Target target = new Target();
        target.setX(targetX);
        target.setY(targetY);
        attack.setTarget(target);
        attacks.add(attack);

        for (Zombie zombie : zombiesInLocation) {
            zombie.setHealth(zombie.getHealth() - attackPower);
            if (zombie.getHealth() <= 0) {
                remainingZombies.remove(zombie);
            }
        }
    }

    private static void shootEnemyBlock(List<Attack> attacks, Base baseBlock, EnemyBlock enemyBlock) {
        Attack attack = new Attack();
        attack.setBlockId(baseBlock.getId());
        Target target = new Target();
        target.setX(enemyBlock.getX());
        target.setY(enemyBlock.getY());
        attack.setTarget(target);
        attacks.add(attack);
        enemyBlock.setHealth(enemyBlock.getHealth() - baseBlock.getAttack());
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }

    private static void logAttackSummary(List<Attack> attacks, List<Zombie> remainingZombies, InfoResponse infoResponse, int highPriorityAttacks, int totalEnemyBlockAttacks) {
        int totalZombies = infoResponse.getZombies().length;
        int zombiesKilled = totalZombies - remainingZombies.size();
        int zombiesWounded = remainingZombies.size();
        int totalAttacks = attacks.size();
        int normalEnemyBlockAttacks = totalEnemyBlockAttacks - highPriorityAttacks;

        logger.info(String.format("Итог атаки: всего атак: %d, зомби убито: %d, зомби ранено: %d, атаковано EnemyBlock: %d (приоритетных: %d, обычных: %d)",
                totalAttacks, zombiesKilled, zombiesWounded, totalEnemyBlockAttacks, highPriorityAttacks, normalEnemyBlockAttacks));
    }
}
