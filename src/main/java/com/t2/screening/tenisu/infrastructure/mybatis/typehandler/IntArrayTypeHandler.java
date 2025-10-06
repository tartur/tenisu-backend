package com.t2.screening.tenisu.infrastructure.mybatis.typehandler;


import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;

import java.sql.*;
import java.util.Arrays;
import java.util.stream.IntStream;

public class IntArrayTypeHandler extends BaseTypeHandler<int[]> {

    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, int[] parameter, JdbcType jdbcType) throws SQLException {
        Connection conn = ps.getConnection();
        Array array = conn.createArrayOf("smallint", IntStream.of(parameter).boxed().toArray());
        ps.setArray(i, array);
    }

    @Override
    public int[] getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return map(rs.getArray(columnName));
    }

    @Override
    public int[] getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return map(rs.getArray(columnIndex));
    }

    @Override
    public int[] getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return map(cs.getArray(columnIndex));
    }

    private int[] map(Array array) throws SQLException {
        if (array == null) return null;
        Object arr = array.getArray();
        Object[] obj = (Object[]) arr;
        return Arrays.stream(obj).mapToInt(o -> ((Number) o).intValue()).toArray();
    }
}