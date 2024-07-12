package org.example;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.models.*;

import java.util.Collections;

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
}
