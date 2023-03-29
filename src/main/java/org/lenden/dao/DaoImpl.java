package org.lenden.dao;

import org.lenden.model.Tenants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;


public class DaoImpl
{
    Dao dao = new Dao();

    public boolean login(Tenants tenant)
    {
        Connection c = dao.getConnection();
        Statement stmt;

        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM tenants;");
            rs.next();
            String name = rs.getString("username");
            String passStr = rs.getString("password");
            char[] pass = passStr.toCharArray();

            if(Arrays.equals(pass,tenant.getPassword()) && name.equals(tenant.getUsername()))
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
