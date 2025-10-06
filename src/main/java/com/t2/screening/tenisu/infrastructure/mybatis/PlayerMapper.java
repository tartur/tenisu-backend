package com.t2.screening.tenisu.infrastructure.mybatis;

import com.t2.screening.tenisu.domain.model.Player;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * This mapper is backed by xml mapper file
 */
@Mapper
public interface PlayerMapper {
    Player findById(@Param("id") long id);

    List<Player> findAll();

    /**
     * @param p player to insert, will have the id set via useGeneratedKeys
     * @return affected rows; id set via useGeneratedKeys
     */
    int insertPlayer(Player p);

    int insertPlayerData(Player p);
}