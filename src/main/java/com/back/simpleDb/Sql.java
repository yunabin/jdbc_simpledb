package com.back.simpleDb;

import java.sql.*;
import java.util.*;

public class Sql {
    private final Connection conn;
    private final boolean devMode;
    private final StringBuilder query = new StringBuilder();
    private final List<Object> params = new ArrayList<>();

    public Sql(Connection conn, boolean devMode) {
        this.conn = conn;
        this.devMode = devMode;
    }

    public Sql append(String sql, Object... args) {
        if (!query.isEmpty()) query.append(" ");
        query.append(sql);
        Collections.addAll(params, args);
        return this; // 체이닝 핵심!
    }

    private PreparedStatement buildPs() throws SQLException {
        String rawSql = query.toString();
        if (devMode) {
            System.out.println("== rawSql ==");
            System.out.println(rawSql);
            System.out.println("params: " + params);
        }
        PreparedStatement ps = conn.prepareStatement(
                rawSql, Statement.RETURN_GENERATED_KEYS
        );
        for (int i = 0; i < params.size(); i++) {
            ps.setObject(i + 1, params.get(i));
        }
        return ps;
    }

    public void run() {
        try (PreparedStatement ps = buildPs()) {
            ps.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public long insert() {
        try (PreparedStatement ps = buildPs()) {
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public int update() {
        try (PreparedStatement ps = buildPs()) {
            return ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    public int delete() {
        return update();
    }
}