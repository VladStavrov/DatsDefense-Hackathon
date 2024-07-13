package org.example.models.play;

import lombok.Data;

import java.util.List;

@Data
public class CommandResponse {
    private PlayRequest playRequest;
    private List<String> errors;
    private PlayRequest acceptedCommands;

}
