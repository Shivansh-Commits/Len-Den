package org.lenden.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lenden.model.Bill;
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

    public int getNextBillNumber()
    {
        PreparedStatement stmt;
        Connection c = dao.getConnection();

        try {

            //GETTING NEXT BILL NUMBER
            stmt  = c.prepareStatement("Select nextbillnumber from "+tenantId+".bills where billnumber = 0 "); // A specific row where nextbill number is stored.
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int billnumber = rs.getInt("nextbillnumber");

            //SAVING THE VALUE OF NEXT BILL NUMBER
             int nextbillnumber = billnumber + 1;
            stmt  = c.prepareStatement("UPDATE "+tenantId+".bills SET nextbillnumber = "+nextbillnumber+" where billnumber = 0");
            stmt.executeUpdate();

            return billnumber;
        }
        catch(SQLException e)
        {
            e.printStackTrace();

        }

        return 0;
    }

    public int addBillToDB(Bill bill)
    {

        PreparedStatement stmt;
        Connection c = dao.getConnection();

        try {
            stmt  = c.prepareStatement("INSERT INTO "+tenantId+".bills (outletname,outletaddress,gstnumber,servicecharge,sgst,cgst,discount,subtotal,total,grandtotal,billdate,billnumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?);");
            stmt.setString(1, bill.getOutletName());
            stmt.setString(2, bill.getOutletAddress());
            stmt.setString(3, bill.getGstNumber());
            stmt.setDouble(4, bill.getServiceCharge());
            stmt.setDouble(5, bill.getSgst());
            stmt.setDouble(6, bill.getCgst());
            stmt.setDouble(7, bill.getDiscount());
            stmt.setDouble(8, bill.getSubTotal());
            stmt.setDouble(9, bill.getTotal());
            stmt.setDouble(10, bill.getGrandTotal());
            stmt.setString(11, bill.getDate());
            stmt.setDouble(12, bill.getBillnumber());

            int rowsAffected = stmt.executeUpdate();

            stmt.close();
            c.close();

            return rowsAffected;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return -1;

    }

    public String getOutletDetails(String detail)
    {
        PreparedStatement stmt;
        Connection c = dao.getConnection();

        try {

            stmt  = c.prepareStatement("Select * from "+tenantId+".outletdetails ");
            ResultSet rs = stmt.executeQuery();
            rs.next();

            if(detail.equals("name") )
            {
                return rs.getString("outletname");
            }
            if(detail.equals("address") )
            {
                return rs.getString("outletaddress");
            }
            if(detail.equals("gstnumber") )
            {
                return rs.getString("outletaddress");
            }

            stmt.close();
            c.close();
        }
        catch(SQLException e)
        {
            e.getMessage();
        }

        return null;
    }


}
