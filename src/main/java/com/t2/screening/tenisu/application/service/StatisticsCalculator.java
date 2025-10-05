package com.t2.screening.tenisu.application.service;

import java.util.Arrays;
import java.util.List;

/**
 * Generic statistics calculator
 */
public class StatisticsCalculator {

    public double calculateMedian(List<Double> values) {
        if (values == null || values.isEmpty()) {
            return Double.NaN;
        }
        int n = values.size();
        Double[] valuesArray = values.toArray(new Double[n]);
        Arrays.sort(valuesArray);
        if ((n % 2) == 0) { // odd length
            return (valuesArray[n / 2 - 1] + valuesArray[n / 2]) / 2.0;
        } else {
            return valuesArray[(n / 2)];
        }
    }

    public double calculateAverage(List<Double> values) {
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(Double.NaN);
    }
}
