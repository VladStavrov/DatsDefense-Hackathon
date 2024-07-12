package org.example;

import org.example.clients.GameCommandApiClient;
import org.example.clients.ParticipationApiClient;
import org.example.clients.RoundsApiClient;
import org.example.clients.WorldDataApiClient;
import org.example.clients.ZombiDefApiClient;
import org.example.models.ParticipationResponse;
import org.example.models.RoundsResponse;
import org.example.models.mapInfo.InfoResponse;
import org.example.models.play.CommandResponse;
import org.example.models.play.PlayRequest;
import org.example.models.worldInfo.WorldDataResponse;

public class MainCommands {

    //№1 - commands to build and attack, should be sent only once per turn
    public static CommandResponse playResponse(PlayRequest playRequest) {
        try {
            GameCommandApiClient client = new GameCommandApiClient();
            return client.sendCommand(playRequest);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //№2 - you MUST send this request in lobby time to participate in the game (once per round)
    public static ParticipationResponse getPlay() {
        ParticipationApiClient participationApiClient = new ParticipationApiClient();
        try {
            return participationApiClient.participate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    // №3 - world parts around player that are changing during the game (zombies, players, current player, etc...)
    public static InfoResponse getApiResponse() {
        try {
            ZombiDefApiClient client = new ZombiDefApiClient();
            return client.fetchUnits();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // №4 - world parts around player that are not changing during the game (zombie zpots)
    public static WorldDataResponse getWorldDataResponse() {
        try {
            WorldDataApiClient client = new WorldDataApiClient();
            return client.fetchWorldData();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // №5 - game rounds
    public static RoundsResponse zombieDef() {
        try {
            RoundsApiClient client = new RoundsApiClient();
            return client.fetchRounds();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
