package org.lenden.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lenden.model.MenuItems;
import org.lenden.model.Tenants;
import static org.lenden.LoginController.getTenant;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;




public class DaoImpl
{
    Dao dao = new Dao();

    public String tenantId = getTenant() ;


    public boolean login(Tenants tenantInfo)
    {

        Statement stmt;

        try {
            Connection c = dao.getConnection();
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM public.tenants where username = '"+tenantInfo.getUsername()+"' AND password='"+tenantInfo.getPassword()+"';");
            rs.next();
            String name = rs.getString("username").trim();
            String pass = rs.getString("password").trim();  // .trim() is used to remove "\n" or " " at the end of string

            if(pass.equals(tenantInfo.getPassword()) && name.equals(tenantInfo.getUsername()))
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

    public ObservableList<MenuItems> getCategoryItems(String category)
    {
        ObservableList<MenuItems> menuItemList = FXCollections.observableArrayList();
        PreparedStatement stmt;


        try {
            Connection c = dao.getConnection();

            stmt  = c.prepareStatement("SELECT * FROM "+tenantId+".menu WHERE category = ?");
            stmt.setString(1,category);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                MenuItems temp = new MenuItems();
                temp.setFoodItem(rs.getString("foodItemName"));
                temp.setPrice(rs.getString("price"));
                temp.setAvailability(rs.getBoolean("availability"));
                menuItemList.add(temp);
            }

            if(menuItemList.size()!=0)
            {
                rs.close();
                stmt.close();
                c.close();
                return menuItemList;
            }

            rs.close();
            stmt.close();
            c.close();
        }
        catch(Exception e)
        {
            e.getMessage();
        }

        return null;
    }


}
