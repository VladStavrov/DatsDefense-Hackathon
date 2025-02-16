package org.example.scripts;

import org.example.models.mapInfo.*;
import org.example.models.play.Attack;
import org.example.models.play.Target;

import java.util.*;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class ShootScript {

    private static final Logger logger = Logger.getLogger(ShootScript.class.getName());

    public record AttackResponse(List<Attack> attacks, InfoResponse updatedInfoResponse) {
    }

    public static AttackResponse shoot(InfoResponse infoResponse) {
        List<Attack> attacks = new ArrayList<>();
        logger.info("Начало процесса атаки");

        // Проверка на пустоту массивов зомби и блоков врагов
        if (infoResponse.getZombies() == null && infoResponse.getEnemyBlocks() == null) {
            logger.info("Зомби и блоки врагов отсутствуют, атака невозможна");
            return new AttackResponse(attacks, infoResponse);
        }

        // Найти центральный блок базы (head)
        Base centerBaseBlock = findCenterBaseBlock(infoResponse.getBase());

        if (centerBaseBlock == null) {
            logger.warning("Центральный блок базы не найден, атака невозможна");
            return new AttackResponse(attacks, infoResponse);
        }

        int centerX = centerBaseBlock.getX();
        int centerY = centerBaseBlock.getY();

        // Обработка зомби, если они существуют
        Map<String, List<Zombie>> zombiesByLocation = Optional.ofNullable(infoResponse.getZombies())
                .map(Arrays::asList)
                .map(ShootScript::mapZombiesByLocation)
                .orElseGet(HashMap::new);

        // Сортировка зомби по расстоянию от центра и уровню угрозы
        List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation = zombiesByLocation.entrySet().stream()
                .sorted(Comparator.<Map.Entry<String, List<Zombie>>>comparingDouble(entry -> calculateDistance(centerX, centerY, entry.getValue().get(0).getX(), entry.getValue().get(0).getY()))
                        .thenComparing(entry -> getZombieThreatLevel(entry.getValue().get(0))))
                .collect(Collectors.toList());

        List<Base> remainingBaseBlocks = new ArrayList<>(Arrays.asList(infoResponse.getBase()));
        List<EnemyBlock> highPriorityEnemyBlocks = Optional.ofNullable(infoResponse.getEnemyBlocks())
                .map(Arrays::asList)
                .map(ShootScript::findHighPriorityEnemyBlocks)
                .orElseGet(ArrayList::new);

        List<EnemyBlock> remainingEnemyBlocks = Optional.ofNullable(infoResponse.getEnemyBlocks())
                .map(Arrays::asList)
                .map(ArrayList::new)
                .orElseGet(ArrayList::new);

        int highPriorityAttacks = 0;
        int totalEnemyBlockAttacks = 0;

        // Выполнение атак по EnemyBlock с атакой 40
        if (!highPriorityEnemyBlocks.isEmpty()) {
            highPriorityAttacks = executeHighPriorityEnemyBlockAttacks(attacks, remainingBaseBlocks, highPriorityEnemyBlocks);
            remainingEnemyBlocks.removeAll(highPriorityEnemyBlocks);
        }

        // Выполнение атак по обычным EnemyBlock
        if (!remainingEnemyBlocks.isEmpty()) {
            totalEnemyBlockAttacks = executeEnemyBlockAttacks(attacks, remainingBaseBlocks, remainingEnemyBlocks, sortedZombiesByLocation);
        }

        // Выполнение атак по зомби
        if (!sortedZombiesByLocation.isEmpty()) {
            List<Zombie> remainingZombies = new ArrayList<>(Arrays.asList(infoResponse.getZombies()));
            executeZombieAttacks(attacks, remainingBaseBlocks, sortedZombiesByLocation, remainingZombies);

            // Обновить массив зомби после атак
            infoResponse.setZombies(remainingZombies.toArray(new Zombie[0]));
        }

        logAttackSummary(attacks, Arrays.asList(infoResponse.getZombies()), infoResponse, highPriorityAttacks, totalEnemyBlockAttacks);

        // Обновление InfoResponse
        infoResponse.setEnemyBlocks(remainingEnemyBlocks.toArray(new EnemyBlock[0]));

        logger.info("Процесс атаки завершен");
        return new AttackResponse(attacks, infoResponse);
    }

    private static Base findCenterBaseBlock(Base[] baseBlocks) {
        return Arrays.stream(baseBlocks)
                .filter(Base::isHead)
                .findFirst()
                .orElse(null);
    }

    private static Map<String, List<Zombie>> mapZombiesByLocation(List<Zombie> zombies) {
        return zombies.stream()
                .collect(Collectors.groupingBy(zombie -> zombie.getX() + "," + zombie.getY()));
    }

    private static void executeZombieAttacks(List<Attack> attacks, List<Base> baseBlocks, List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation, List<Zombie> remainingZombies) {
        List<Base> baseBlocksToRemove = new ArrayList<>();

        for (Base baseBlock : baseBlocks) {
            int baseX = baseBlock.getX();
            int baseY = baseBlock.getY();
            int attackRadius = baseBlock.getRange();
            int attackPower = baseBlock.getAttack();

            for (Map.Entry<String, List<Zombie>> entry : sortedZombiesByLocation) {
                List<Zombie> zombiesInLocation = entry.getValue();
                if(!zombiesInLocation.isEmpty()){
                    int targetX = zombiesInLocation.get(0).getX();
                    int targetY = zombiesInLocation.get(0).getY();
                    double distance = calculateDistance(baseX, baseY, targetX, targetY);

                    if (distance <= attackRadius) {
                        shootZombies(attacks, baseBlock, attackPower, zombiesInLocation, remainingZombies);
                        baseBlocksToRemove.add(baseBlock);  // Добавить блок базы для удаления после атаки
                        break;
                    }
                }

            }
        }

        baseBlocks.removeAll(baseBlocksToRemove);  // Удалить блоки базы после атаки
    }

    private static int executeHighPriorityEnemyBlockAttacks(List<Attack> attacks, List<Base> baseBlocks, List<EnemyBlock> enemyBlocks) {
        int highPriorityAttacks = 0;
        List<Base> baseBlocksToRemove = new ArrayList<>();

        for (Base baseBlock : baseBlocks) {
            int baseX = baseBlock.getX();
            int baseY = baseBlock.getY();
            int attackRadius = baseBlock.getRange();

            for (EnemyBlock enemyBlock : enemyBlocks) {
                double distance = calculateDistance(baseX, baseY, enemyBlock.getX(), enemyBlock.getY());
                if (distance <= attackRadius) {
                    shootEnemyBlock(attacks, baseBlock, enemyBlock);
                    highPriorityAttacks++;
                    baseBlocksToRemove.add(baseBlock);  // Добавить блок базы для удаления после атаки
                    break;
                }
            }
        }

        baseBlocks.removeAll(baseBlocksToRemove);  // Удалить блоки базы после атаки
        return highPriorityAttacks;
    }

    private static int executeEnemyBlockAttacks(List<Attack> attacks, List<Base> baseBlocks, List<EnemyBlock> enemyBlocks, List<Map.Entry<String, List<Zombie>>> sortedZombiesByLocation) {
        int totalEnemyBlockAttacks = 0;
        List<Base> baseBlocksToRemove = new ArrayList<>();

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
                    baseBlocksToRemove.add(baseBlock);  // Добавить блок базы для удаления после атаки
                    attacked = true;
                    break;
                }
            }

            if (attacked) continue;
            if(!sortedZombiesByLocation.isEmpty()){
                for (Map.Entry<String, List<Zombie>> entry : sortedZombiesByLocation) {

                    List<Zombie> zombiesInLocation = entry.getValue();
                    if(!zombiesInLocation.isEmpty()){
                        int targetX = zombiesInLocation.get(0).getX();
                        int targetY = zombiesInLocation.get(0).getY();
                        double distance = calculateDistance(baseX, baseY, targetX, targetY);

                        if (distance <= attackRadius) {
                            shootZombies(attacks, baseBlock, attackPower, zombiesInLocation, new ArrayList<>(zombiesInLocation));
                            baseBlocksToRemove.add(baseBlock);  // Добавить блок базы для удаления после атаки
                            break;
                        }
                    }

                }
            }
            // Если нет EnemyBlock, атакуем зомби

        }

        baseBlocks.removeAll(baseBlocksToRemove);  // Удалить блоки базы после атаки
        return totalEnemyBlockAttacks;
    }

    private static List<EnemyBlock> findHighPriorityEnemyBlocks(List<EnemyBlock> enemyBlocks) {
        return enemyBlocks.stream()
                .filter(enemyBlock -> enemyBlock.getAttack() == 40)
                .collect(Collectors.toList());
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

        Iterator<Zombie> iterator = zombiesInLocation.iterator();
        while (iterator.hasNext()) {
            Zombie zombie = iterator.next();
            if (zombie.getHealth() > 0) {  // Проверка на живого зомби
                zombie.setHealth(zombie.getHealth() - attackPower);
                if (zombie.getHealth() <= 0) {
                    remainingZombies.remove(zombie);
                    iterator.remove();  // Удаляем зомби из текущей локации
                }
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

    private static int getZombieThreatLevel(Zombie zombie) {
        return switch (zombie.getType()) {
            case "normal" -> 1;
            case "fast" -> 2;  // Учитываем повышенную скорость
            case "bomber" -> 5;  // Учитываем радиус атаки
            case "liner" -> 15;  // Учитываем множественные атаки
            case "juggernaut" -> 10;  // Учитываем постоянное движение и неуничтожаемость
            case "chaos_knight" -> 4;  // Учитываем случайное движение и множественные атаки
            default -> 1;
        };
    }

    private static void logAttackSummary(List<Attack> attacks, List<Zombie> remainingZombies, InfoResponse infoResponse, int highPriorityAttacks, int totalEnemyBlockAttacks) {
        int totalZombies = infoResponse.getZombies().length;
        int zombiesKilled = totalZombies - remainingZombies.size();
        int zombiesWounded = remainingZombies.size();
        int totalAttacks = attacks.size();
        int normalEnemyBlockAttacks = totalEnemyBlockAttacks - highPriorityAttacks;

        logger.info(String.format("Итог атаки: всего атак: %d, зомби убито: %d, зомби ранено: %d, атаковано EnemyBlock: %d (приоритетных: %d, обычных: %d)", totalAttacks, zombiesKilled, zombiesWounded, totalEnemyBlockAttacks, highPriorityAttacks, normalEnemyBlockAttacks));
    }
}
