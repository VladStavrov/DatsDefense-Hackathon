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
        Base centerBaseBlock = findCenterBaseBlock((infoResponse.getBase()));

        assert centerBaseBlock != null;
        int centerX = centerBaseBlock.getX();
        int centerY = centerBaseBlock.getY();

        // Карта зомби по их координатам
        Map<String, List<Zombie>> zombiesByLocation = mapZombiesByLocation(Arrays.asList(infoResponse.getZombies()));

        // Сортировка зомби по расстоянию от центра
        List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation = sortZombiesByDistance(centerX, centerY, zombiesByLocation);

        for (Base baseBlock : infoResponse.getBase()) {
            processBaseBlock(attacks, baseBlock, sortedZombiesByLocation, Arrays.asList(infoResponse.getEnemyBlocks()));
        }

        logger.info("Процесс атаки завершен");
        return attacks;
    }

    private static Base findCenterBaseBlock(Base[] baseBlocks) {
        for (Base base : baseBlocks) {
            if (base.isHead()) {
                System.out.println("Head: " + base.x + " : " + base.y);
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

    private static void processBaseBlock(List<Attack> attacks, Base baseBlock, List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation, List<EnemyBlock> enemyBlocks) {
        int baseX = baseBlock.getX();
        int baseY = baseBlock.getY();
        int attackRadius = baseBlock.getRange();
        int attackPower = baseBlock.getAttack();

        logger.info("Блок базы ID: " + baseBlock.getId() + " имеет радиус атаки: " + attackRadius + " и силу атаки: " + attackPower);

        boolean attacked = false;

        for (Map.Entry<String, List<Zombie>> entry : sortedZombiesByLocation) {
            List<Zombie> zombiesInLocation = entry.getValue();
            int targetX = zombiesInLocation.get(0).getX();
            int targetY = zombiesInLocation.get(0).getY();
            double distance = calculateDistance(baseX, baseY, targetX, targetY);

            if (distance <= attackRadius) {
                shootZombies(attacks, baseBlock, attackPower, zombiesInLocation);
                attacked = true;
                break;
            } else {
                logger.info("Цель с координатами {" + targetX + ", " + targetY + "} слишком далеко от блока базы с ID: " + baseBlock.getId() + " (расстояние = " + distance + ")");
            }
        }

        if (!attacked) {
            logger.info("Блок базы ID: " + baseBlock.getId() + " не имеет зомби в радиусе атаки, пытается атаковать EnemyBlock.");
            for (EnemyBlock enemyBlock : enemyBlocks) {
                double distance = calculateDistance(baseX, baseY, enemyBlock.getX(), enemyBlock.getY());
                if (distance <= attackRadius) {
                    shootEnemyBlock(attacks, baseBlock, enemyBlock);
                    attacked = true;
                    break;
                } else {
                    logger.info("EnemyBlock с координатами {" + enemyBlock.getX() + ", " + enemyBlock.getY() + "} слишком далеко от блока базы с ID: " + baseBlock.getId() + " (расстояние = " + distance + ")");
                }
            }
        }

        if (!attacked) {
            logger.info("Блок базы ID: " + baseBlock.getId() + " не имеет целей в радиусе атаки.");
        }
    }

    private static void shootZombies(List<Attack> attacks, Base baseBlock, int attackPower, List<Zombie> zombiesInLocation) {
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
                logger.info("Зомби ID: " + zombie.getId() + " убит");
            } else {
                logger.info("Зомби ID: " + zombie.getId() + " ранен, оставшееся здоровье: " + zombie.getHealth());
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
        if (enemyBlock.getHealth() <= 0) {
            logger.info("EnemyBlock с координатами {" + enemyBlock.getX() + ", " + enemyBlock.getY() + "} уничтожен");
        } else {
            logger.info("EnemyBlock с координатами {" + enemyBlock.getX() + ", " + enemyBlock.getY() + "} ранен, оставшееся здоровье: " + enemyBlock.getHealth());
        }
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow((x1 - x2), 2) + Math.pow((y1 - y2), 2));
    }
}
