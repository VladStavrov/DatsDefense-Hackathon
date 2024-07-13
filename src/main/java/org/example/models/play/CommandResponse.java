package org.example.models.play;

import lombok.Data;
import lombok.ToString;

import java.util.List;

@Data
@ToString(includeFieldNames = true)
public class CommandResponse {
    private PlayRequest playRequest;
    private List<String> errors;
    private PlayRequest acceptedCommands;
}
