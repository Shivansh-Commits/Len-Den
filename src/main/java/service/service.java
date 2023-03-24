package service;

import java.sql.*;

public class service
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

    public void login(String username, String password)
    {
        Connection c = getConnection();
        Statement stmt;

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");
            rs.next();
            String name = rs.getString("username");
            String pass = rs.getString("password");

            if(pass.equals(password) && name.equals(username))
            {
                System.out.println("LOGIN SUCCESS");
            }

            rs.close();
            stmt.close();
            c.close();
        }
        catch(Exception e)
        {
            e.getMessage();
        }
    }

}
