package org.lenden.dao;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


public class daoImpl
{
    org.lenden.dao.dao dao = new dao();

    public boolean login(String username, String password)
    {
        Connection c = dao.getConnection();
        Statement stmt;

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM users;");
            rs.next();
            String name = rs.getString("username");
            String pass = rs.getString("password");

            if(pass.equals(password) && name.equals(username))
            {
                return true;
            }

            rs.close();
            stmt.close();
            c.close();
        }
        catch(Exception e)
        {
            e.getMessage();
        }

        return false;
    }
}
