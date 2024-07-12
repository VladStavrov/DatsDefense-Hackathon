package org.example;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import org.example.models.ParticipationResponse;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.play.*;
import org.example.models.worldInfo.WorldDataResponse;

import static org.example.MainCommands.*;
import static org.example.scripts.BuildScript.build;
import static org.example.scripts.MoveBaseScript.moveBase;
import static org.example.scripts.ShootScript.shoot;

public class AutoPlayScript {

    private static final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {
        // Пробрасываем роут №2
        AtomicReference<ParticipationResponse> participationResponse = new AtomicReference<>(getPlay());
        if (participationResponse.get() == null) {
            System.err.println("Failed to participate in the game.");
            return;
        }

        scheduler.scheduleAtFixedRate(() -> {
            try {
                // Проверяем количество миллисекунд до конца текущего хода (роут №3)
                participationResponse.set(getPlay());
                if (participationResponse.get() == null) {
                    System.err.println("Failed to participate in the game.");
                    return;
                }

                long roundEndsInMs = participationResponse.get().getStartsInSec();

                // Если миллисекунд меньше 2000 (2 секунд), пробрасываем роут №2 и сбрасываем информацию
                if (roundEndsInMs < 2000) {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    MainCommands.getPlay();
                } else {

                    // Каждую секунду пробрасываем роут №3
                    InfoResponse infoResponse = MainCommands.getApiResponse();
                    if (infoResponse == null) {
                        System.err.println("Failed to get game info.");
                        return;
                    }

                    // Каждую секунду пробрасываем роут №1

                    WorldDataResponse worldDataResponse = getWorldDataResponse();

                    PlayRequest playRequest = new PlayRequest();

                    List<Attack> attack = shoot(infoResponse, worldDataResponse);
                    List<Build> builds = build(infoResponse, worldDataResponse);
                    MoveBase moveBase = moveBase(infoResponse, worldDataResponse);

                    playRequest.setAttack(attack);
                    playRequest.setBuild(builds);
                    playRequest.setMoveBase(moveBase);

                    System.out.println(playResponse(playRequest));


                    CommandResponse playResponse = MainCommands.playResponse(playRequest);
                    System.out.println("Commands sent and Info updated.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 1, TimeUnit.SECONDS);
    }
}