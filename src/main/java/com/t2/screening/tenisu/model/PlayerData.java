package com.t2.screening.tenisu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Player statistics
 */
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class PlayerData {
    private int rank;
    private int points;
    /**
     * in cm
     */
    private int height;
    /**
     * in grams
     */
    private int weight;
    private int age;
    private int[] last;
}
