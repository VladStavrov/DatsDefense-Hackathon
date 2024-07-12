package org.example.models.play;

import lombok.Data;

import java.util.List;

@Data
public class CommandResponse {
    private Command acceptedCommands;
    private List<String> errors;

}
