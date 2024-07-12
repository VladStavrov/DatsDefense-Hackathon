package org.example.models;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
public class PlayRequest {
    private List<Attack> attack;
    private List<Build> build;
    private MoveBase moveBase;
}
