package org.lenden.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lenden.model.MenuItems;
import org.lenden.model.Tenants;
import static org.lenden.LoginController.getTenant;

import java.sql.*;


public class DaoImpl
{
    Dao dao = new Dao();

    public String tenantId = getTenant();


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

            stmt  = c.prepareStatement("SELECT * FROM "+tenantId+".menu WHERE fooditemcategory = ?");
            stmt.setString(1,category);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                MenuItems temp = new MenuItems();
                temp.setFoodItemName(rs.getString("fooditemname"));
                temp.setFoodItemPrice(rs.getInt("fooditemprice"));
                if(rs.getBoolean("fooditemavailability") == true)
                    temp.setFoodItemAvailability("Available");
                else
                    temp.setFoodItemAvailability("NOT Available");

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
        catch(SQLException e)
        {
            e.getMessage();
        }

        return null;
    }

    public double getTax(String tax) throws SQLException
    {
        try {
            Connection c = dao.getConnection();

            PreparedStatement stmt;
            stmt = c.prepareStatement("SELECT * FROM " + tenantId + ".taxes");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            if (tax.equals("cgst")) {
                double cgst = rs.getDouble("cgst");

                rs.close();
                c.close();
                stmt.close();
                return cgst;
            } else if (tax.equals("sgst")) {
                double sgst = rs.getDouble("sgst");

                rs.close();
                c.close();
                stmt.close();
                return sgst;
            } else if (tax.equals("vat")) {
                double vat = rs.getDouble("vat");

                rs.close();
                c.close();
                stmt.close();
                return vat;
            }
            else if (tax.equals("servicecharge"))
            {
                double servicecharge = rs.getDouble("servicecharge");

                rs.close();
                c.close();
                stmt.close();
                return servicecharge;
            }

        }
        catch(SQLException e)
        {

            e.getMessage();
        }
        return 0;
    }


}
