package org.lenden.dao;

import java.sql.*;

public class Dao
{
    public Connection getConnection()
    {
        Connection conn;
        try {
            String url = "jdbc:postgresql://localhost:5432/postgres";
            String user = "postgres";
            String password = "root";
            conn = DriverManager.getConnection(url, user, password);
            return conn;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

}
