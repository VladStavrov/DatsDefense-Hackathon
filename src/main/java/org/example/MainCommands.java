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

        private void parseAndPrintWorldResponse(String response) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(response);

                String realmName = jsonNode.get("realmName").asText();
                JsonNode zpots = jsonNode.get("zpots");

                System.out.println("Realm Name: " + realmName);
                System.out.println("Zpots: ");
                for (JsonNode zpot : zpots) {
                    System.out.println("  Zpot: " + zpot.toString());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


        private void parseAndPrintResponse(String response) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode root = mapper.readTree(response);
                String game = root.get("game").asText();
                String now = root.get("now").asText();
                JsonNode rounds = root.get("rounds");


                System.out.println("Game "+game);
                System.out.println("Time "+ now);
                System.out.println("Rounds "+rounds);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }


}
