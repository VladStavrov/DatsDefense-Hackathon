package org.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.*;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;

public class MainCommands {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final ApiClient apiClient = new ApiClient();

    public void play(String url) throws Exception {

        PlayRequest playRequest = new PlayRequest();
        playRequest.setAttack(Collections.singletonList(new Attack() {{
            setBlockId("f47ac10b-58cc-0372-8562-0e02b2c3d479");
            setTarget(new Target() {{
                setX(1);
                setY(1);
            }});
        }}));
        playRequest.setBuild(Collections.singletonList(new Build() {{
            setX(1);
            setY(1);
        }}));
        playRequest.setMoveBase(new MoveBase() {{
            setX(1);
            setY(1);
        }});

        String json = objectMapper.writeValueAsString(playRequest);
        System.out.println("playRequest json: "+ playRequest);
        apiClient.doPost("/play/zombidef/command",null,json);
    }



        public void zombieDef(){
            ApiClient apiClient = new ApiClient();
            try{
                String response = apiClient.doGet("/rounds/zombidef", new HashMap<>());
                parseAndPrintResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }


        }

        public void fetchWorldData() {
            ApiClient apiClient = new ApiClient();
            try {
                String response = apiClient.doGet("/play/zombidef/world", new HashMap<>());
                parseAndPrintWorldResponse(response);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    private void parseAndPrintResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            ZombieDefResponse zombieDefResponse = objectMapper.readValue(response, ZombieDefResponse.class);
            System.out.println(zombieDefResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void parseAndPrintWorldResponse(String response) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            WorldDataResponse worldDataResponse = objectMapper.readValue(response, WorldDataResponse.class);
            System.out.println(worldDataResponse.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
