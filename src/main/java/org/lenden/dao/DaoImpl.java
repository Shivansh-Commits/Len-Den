package org.lenden.dao;

import org.lenden.model.Tenants;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;


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
            String pass = rs.getString("password");
            pass=pass.trim(); //to remove "\n" at the end of string

            if(pass.equals(tenant.getPassword()) && name.equals(tenant.getUsername()))
            {
                rs.close();
                stmt.close();
                c.close();
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
