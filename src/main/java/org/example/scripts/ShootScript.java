package org.example.scripts;

import org.example.models.mapInfo.Base;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.mapInfo.Zombie;
import org.example.models.play.Attack;
import org.example.models.play.Target;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class ShootScript {

    public static List<Attack> shoot(InfoResponse infoResponse) {
        List<Attack> attacks = new ArrayList<>();

        // Логирование начала процесса
        System.out.println("Начало процесса атаки");

        for (Base baseBlock : infoResponse.getBase()) {
            List<Zombie> targetZombies = new ArrayList<>();

            // Определение радиуса атаки
            int attackRadius = baseBlock.range;
            int attackPower = baseBlock.attack;

            System.out.println("Блок базы ID: " + baseBlock.id + " имеет радиус атаки: " + attackRadius + " и силу атаки: " + attackPower);

            for (Zombie zombie : infoResponse.getZombies()) {
                double distance = calculateDistance(baseBlock.getX(), baseBlock.getY(), zombie.getX(), zombie.getY());

                if (distance <= attackRadius) {
                    targetZombies.add(zombie);
                    System.out.println("Зомби ID: " + zombie.id + " в радиусе атаки. Расстояние: " + distance);
                }
            }

            targetZombies.sort(Comparator.comparingDouble(z -> calculateDistance(baseBlock.getX(), baseBlock.getY(), z.getX(), z.getY())));

            PriorityQueue<Zombie> zombieQueue = new PriorityQueue<>(Comparator.comparingInt(z -> z.health));

            zombieQueue.addAll(targetZombies);

            while (!zombieQueue.isEmpty()) {
                Zombie zombie = zombieQueue.poll();
                if (zombie.health <= attackPower) {
                    // Зомби будет убит
                    Attack attack = new Attack();
                    attack.setBlockId(baseBlock.id);
                    Target target = new Target();
                    target.setX(zombie.x);
                    target.setY(zombie.y);
                    attack.setTarget(target);
                    attacks.add(attack);
                    zombie.health -= attackPower;
                    System.out.println("Атака: Блок базы ID: " + baseBlock.id + " убивает зомби ID: " + zombie.id + " с силой: " + attackPower);
                } else {
                    // Зомби будет ранен, но не убит
                    Attack attack = new Attack();
                    attack.setBlockId(baseBlock.id);
                    Target target = new Target();
                    target.setX(zombie.x);
                    target.setY(zombie.y);
                    attack.setTarget(target);
                    attacks.add(attack);
                    zombie.health -= attackPower;
                    zombieQueue.add(zombie);
                    System.out.println("Атака: Блок базы ID: " + baseBlock.id + " ранит зомби ID: " + zombie.id + " с силой: " + attackPower);
                }
            }
        }

        System.out.println("Процесс атаки завершен");

        return attacks;
    }

    private static double calculateDistance(int x1, int y1, int x2, int y2) {
        return Math.sqrt(Math.pow(Math.abs(x1 - x2), 2) + Math.pow(Math.abs(y1 - y2), 2));
    }
}