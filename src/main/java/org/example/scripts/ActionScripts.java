package org.example.scripts;



import org.example.models.mapInfo.InfoResponse;
import org.example.models.play.Attack;
import org.example.models.play.Build;
import org.example.models.play.MoveBase;
import org.example.models.play.PlayRequest;
import org.example.models.worldInfo.WorldDataResponse;

import java.util.List;

import static org.example.MainCommands.*;
import static org.example.scripts.BuildScript.build;
import static org.example.scripts.MoveBaseScript.moveBase;
import static org.example.scripts.ShootScript.shoot;

public class ActionScripts {

    public static void main(String[] args) {

        InfoResponse infoResponse = getApiResponse();
        WorldDataResponse worldDataResponse = getWorldDataResponse();

        PlayRequest playRequest = new PlayRequest();

        List<Attack> attack = shoot(infoResponse,worldDataResponse);
        List<Build> builds = build(infoResponse,worldDataResponse);
        MoveBase moveBase = moveBase(infoResponse,worldDataResponse);

        playRequest.setAttack(attack);
        playRequest.setBuild(builds);
        playRequest.setMoveBase(moveBase);

        System.out.println(playResponse(playRequest));
    }
}
