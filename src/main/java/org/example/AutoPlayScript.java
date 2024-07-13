package org.example;

import java.util.List;

import org.example.models.mapInfo.InfoResponse;
import org.example.models.play.*;
import org.example.models.worldInfo.WorldDataResponse;

import static org.example.MainCommands.*;
import static org.example.scripts.BuildScript.build;
import static org.example.scripts.ShootScript.shoot;

public class AutoPlayScript {

    public static void main(String[] args) throws InterruptedException {
        while (true) {
            try {
                // Каждую секунду пробрасываем роут №3
                InfoResponse infoResponse;
                try {
                    infoResponse = MainCommands.getApiResponse();
                    if (infoResponse == null) throw new Exception("Failed to get game info.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    Thread.sleep(1000);
                    continue;
                }

                // Каждую секунду пробрасываем роут №1
                WorldDataResponse worldDataResponse;
                try {
                    worldDataResponse = getWorldDataResponse();
                    if (worldDataResponse == null) throw new Exception("Failed to get world data.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    Thread.sleep(1000);
                    continue;
                }

                PlayRequest playRequest = new PlayRequest();
                try {
                    List<Attack> attack = shoot(infoResponse);
                    List<Build> builds = build(infoResponse, worldDataResponse);
                    //MoveBase moveBase = moveBase(infoResponse, worldDataResponse);

                    playRequest.setAttack(attack);
                    playRequest.setBuild(builds);
                    //playRequest.setMoveBase(moveBase);
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    getPlay();
                    Thread.sleep(1000);
                    continue;
                }

                CommandResponse playResponse;
                try {
                    playResponse = MainCommands.playResponse(playRequest);
                    if (playResponse == null) throw new Exception("Failed to send play response.");
                    System.out.println(playResponse);
                    System.out.println("Commands sent and Info updated.");
                } catch (Exception e) {
                    System.err.println(e.getMessage());
                    Thread.sleep(1000);
                }
                System.out.println(infoResponse.getBase().length + " - Размер базы; " + infoResponse.getPlayer().getZombieKills() + " - убито зомби; " + infoResponse.getPlayer().getGold() + " - золото");
                System.out.println("==================================");
                Thread.sleep(2000);

            } catch (Exception e) {
                e.printStackTrace();
                Thread.sleep(1000);
            }
        }
    }
}