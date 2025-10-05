package com.t2.screening.tenisu.application.service;

import com.t2.screening.tenisu.domain.model.Player;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * BMI (Body Mass Index) calculation service
 */
public class CalculateBMIService {

    private static final int IMC_SCALE = 1;

    public double calculate(Player player) {
        double weightInKg = player.getData().getWeight() * 0.001;
        double heightInMeter = player.getData().getHeight() * 0.01;
        return BigDecimal.valueOf(weightInKg / (heightInMeter * heightInMeter))
                .setScale(IMC_SCALE, RoundingMode.HALF_EVEN)
                .doubleValue();
    }
}
