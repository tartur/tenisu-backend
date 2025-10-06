package com.t2.screening.tenisu.infrastructure.mybatis;

import com.t2.screening.tenisu.domain.model.Country;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CountryMapper {
    @Select("SELECT code, picture_url as picture FROM countries ORDER BY code")
    List<Country> findAll();

    @Select("SELECT code, picture_url as picture FROM countries WHERE code = #{code}")
    Country findByCode(String code);
}