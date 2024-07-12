package org.example.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public  class Attack {
    private String blockId;
    private Target target;

}