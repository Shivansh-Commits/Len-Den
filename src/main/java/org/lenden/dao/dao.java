package org.lenden.dao;

import java.sql.*;

public class dao
{
    public Connection getConnection()
    {
        Connection conn;
        try {
            String url = "jdbc:postgresql://localhost:5432/lenden";
            String user = "postgres";
            String password = "admin";
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
