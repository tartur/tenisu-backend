package com.t2.screening.tenisu.application;

import com.t2.screening.tenisu.application.service.CalculateBMIService;
import com.t2.screening.tenisu.domain.model.Player;
import com.t2.screening.tenisu.domain.model.PlayerData;
import org.assertj.core.data.Offset;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CalculateBMIServiceTest {

    static final Offset<Double> OFFSET = Offset.offset(0.01);

    @Test
    void calculate() {
        Player player = Player.builder().data(PlayerData.builder().weight(80000).height(180).build()).build();

        assertThat(new CalculateBMIService().calculate(player)).isEqualTo(24.7, OFFSET);
    }

}