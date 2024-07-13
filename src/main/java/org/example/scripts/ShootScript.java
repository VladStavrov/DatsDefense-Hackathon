package org.example.scripts;

import org.example.models.mapInfo.Base;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.mapInfo.Zombie;
import org.example.models.play.Attack;
import org.example.models.play.Target;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ShootScript {

    public static List<Attack> shoot(InfoResponse infoResponse) {
        List<Attack> attacks = new ArrayList<>();

        // Логирование начала процесса
        System.out.println("Начало процесса атаки");

        // Найти центральный блок базы (head)
        Base centerBaseBlock = null;
        for (Base baseBlock : infoResponse.getBase()) {
            if (baseBlock.isHead) {
                centerBaseBlock = baseBlock;
                break;
            }
        }

        if (centerBaseBlock == null) {
            System.out.println("Центральный блок базы не найден");
            return attacks;
        }

        int centerX = centerBaseBlock.getX();
        int centerY = centerBaseBlock.getY();

        for (Base baseBlock : infoResponse.getBase()) {
            // Определение радиуса атаки
            int attackRadius = baseBlock.range;
            int attackPower = baseBlock.attack;

            System.out.println("Блок базы ID: " + baseBlock.id + " имеет радиус атаки: " + attackRadius + " и силу атаки: " + attackPower);

            // Карта зомби по их координатам
            Map<String, List<Zombie>> zombiesByLocation = new HashMap<>();
            for (Zombie zombie : infoResponse.getZombies()) {
                double distance = calculateDistance(centerX, centerY, zombie.getX(), zombie.getY());

                if (distance <= attackRadius) {
                    String key = zombie.getX() + "," + zombie.getY();
                    zombiesByLocation.computeIfAbsent(key, k -> new ArrayList<>()).add(zombie);
                    System.out.println("Зомби ID: " + zombie.id + " в радиусе атаки. Расстояние: " + distance);
                }
            }

            // Обработка атак для каждой уникальной клетки с зомби
            for (Map.Entry<String, List<Zombie>> entry : zombiesByLocation.entrySet()) {
                List<Zombie> zombiesInLocation = entry.getValue();

                // Сортировка зомби по здоровью
                zombiesInLocation.sort(Comparator.comparingInt(z -> z.health));

                while (!zombiesInLocation.isEmpty()) {
                    boolean allZombiesKilled = true;

                    for (Zombie zombie : zombiesInLocation) {
                        if (zombie.health > 0) {
                            allZombiesKilled = false;
                            break;
                        }
                    }

                    if (allZombiesKilled) {
                        break;
                    }

                    shootZombies(attacks, baseBlock, attackPower, zombiesInLocation);
                }
            }
        }

        System.out.println("Процесс атаки завершен");

        return attacks;
    }

    private static void shootZombies(List<Attack> attacks, Base baseBlock, int attackPower, List<Zombie> zombiesInLocation) {
        Attack attack = new Attack();
        attack.setBlockId(baseBlock.id);
        Target target = new Target();
        target.setX(zombiesInLocation.get(0).getX());
        target.setY(zombiesInLocation.get(0).getY());
        attack.setTarget(target);
        attacks.add(attack);

        for (Zombie zombie : zombiesInLocation) {
            zombie.health -= attackPower;
            if (zombie.health <= 0) {
                System.out.println("Зомби ID: " + zombie.id + " убит");
            } else {
                System.out.println("Зомби ID: " + zombie.id + " ранен, оставшееся здоровье: " + zombie.health);
            }
        }
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }
}