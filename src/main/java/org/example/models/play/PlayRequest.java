package org.example.models.play;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Data
@NoArgsConstructor
@ToString(includeFieldNames = true)
public class PlayRequest {
    private List<Attack> attack;
    private List<Build> build;
    private MoveBase moveBase;
}
