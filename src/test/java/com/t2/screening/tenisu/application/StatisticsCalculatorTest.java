package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.application.service.StatisticsCalculator;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class StatisticsCalculatorTest {
    static final Offset<Double> OFFSET = Offset.offset(0.01);
    StatisticsCalculator statisticsCalculator = new StatisticsCalculator();

    @Test
    void calculateMedian() {
        assertThat(statisticsCalculator.calculateMedian(List.of(1.0, 3.0, 2.5))).isEqualTo(2.5, OFFSET);
        assertThat(statisticsCalculator.calculateMedian(List.of(1.0, 3.0, 2.0, 5.0))).isEqualTo(2.5, OFFSET);
        assertThat(statisticsCalculator.calculateMedian(List.of(1.0))).isEqualTo(1.0);
        assertThat(statisticsCalculator.calculateMedian(List.of(1.0, 2.0))).isEqualTo(1.5);
        assertThat(statisticsCalculator.calculateMedian(Collections.emptyList())).isNaN();
    }

    @Test
    void when_calculateMedian_input_order_should_not_change() {
        List<Double> input = List.of(1.0, 3.0, 2.5);

        assertThat(statisticsCalculator.calculateMedian(input)).isEqualTo(2.5, OFFSET);
        assertThat(input).containsExactly(1.0, 3.0, 2.5);
    }

    @Test
    void calculateAverage() {
        assertThat(statisticsCalculator.calculateAverage(List.of(1.0, 3.0, 2.0))).isEqualTo(2.0, OFFSET);
        assertThat(statisticsCalculator.calculateAverage(List.of(1.0, 3.0, 2.0, 5.0))).isEqualTo(2.75, OFFSET);
        assertThat(statisticsCalculator.calculateAverage(List.of(1.0))).isEqualTo(1.0);
        assertThat(statisticsCalculator.calculateAverage(List.of(1.0, 2.0))).isEqualTo(1.5);
        assertThat(statisticsCalculator.calculateAverage(Collections.emptyList())).isNaN();
    }

    @Test
    void when_calculateAverage_input_order_should_not_change() {
        List<Double> input = List.of(1.0, 3.0, 2.0);

        assertThat(statisticsCalculator.calculateAverage(input)).isEqualTo(2.0, OFFSET);
        assertThat(input).containsExactly(1.0, 3.0, 2.0);
    }
}