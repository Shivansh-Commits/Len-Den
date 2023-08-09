package org.lenden.dao;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lenden.model.Bill;
import org.lenden.model.BillItems;
import org.lenden.model.MenuItems;
import org.lenden.model.Tenants;
import static org.lenden.LoginController.getTenant;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class DaoImpl
{
    //Dao dao = new Dao(); //Connection manager class w/o connection pool

    public String tenantId = getTenant();

    public boolean login(Tenants tenantInfo) throws SQLException
    {
        Statement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM public.tenants where username = '"+tenantInfo.getUsername()+"' AND password='"+tenantInfo.getPassword()+"';");
            if(rs.next())
            {
                String name = rs.getString("username").trim();
                String pass = rs.getString("password").trim();  // .trim() is used to remove "\n" or " " at the end of string

                if(pass.equals(tenantInfo.getPassword()) && name.equals(tenantInfo.getUsername()))
                {
                    rs.close();
                    stmt.close();
                    return true;
                }
                else
                {
                    rs.close();
                    stmt.close();
                    return false;
                }
            }
            else
            {
                rs.close();
                stmt.close();
                return false;
            }
        }
        catch(SQLException e)
        {
            //e.printStackTrace();
            throw new SQLException(e.getMessage());
        }
    }

    public ObservableList<MenuItems> getCategoryItems(String category)
    {
        ObservableList<MenuItems> menuItemList = FXCollections.observableArrayList();
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("SELECT * FROM "+tenantId+".menu WHERE fooditemcategory = ?");
            stmt.setString(1,category);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                MenuItems temp = new MenuItems();
                temp.setFoodItemName(rs.getString("fooditemname"));
                temp.setFoodItemPrice(rs.getInt("fooditemprice"));
                temp.setFoodItemCategory(category);
                if(rs.getBoolean("fooditemavailability"))
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
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public double getTax(String tax) throws SQLException
    {
        try(Connection c = ConnectionManager.getConnection())
        {
            PreparedStatement stmt;
            stmt = c.prepareStatement("SELECT * FROM " + tenantId + ".taxes");
            ResultSet rs = stmt.executeQuery();
            rs.next();
            switch (tax) {
                case "cgst":
                    double cgst = rs.getDouble("cgst");

                    rs.close();
                    c.close();
                    stmt.close();
                    return cgst;
                case "sgst":
                    double sgst = rs.getDouble("sgst");

                    rs.close();
                    c.close();
                    stmt.close();
                    return sgst;
                case "vat":
                    double vat = rs.getDouble("vat");

                    rs.close();
                    c.close();
                    stmt.close();
                    return vat;
                case "servicecharge":
                    double servicecharge = rs.getDouble("servicecharge");

                    rs.close();
                    c.close();
                    stmt.close();
                    return servicecharge;
            }

        }
        catch(SQLException e)
        {

            e.printStackTrace();
        }
        return 0;
    }

    public int getNextBillNumber()
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
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

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("INSERT INTO "+tenantId+".bills (outletname,outletaddress,gstnumber,servicecharge,sgst,cgst,discount,subtotal,total,grandtotal,billdate,billnumber,tablenumber) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?);");
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
            stmt.setString(13, bill.getTableNumber());

            int rowsAffected = stmt.executeUpdate();

            //Add bill details (food items) to "billdetails" table
            addBillDetails(bill);

            stmt.close();

            return rowsAffected;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
        return -1;

    }

    public void addBillDetails(Bill bill) throws SQLException
    {
        PreparedStatement stmt;


        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("INSERT INTO " + tenantId + ".billdetails (fooditemname,fooditemquantity,fooditemprice,billnumber,tablenumber) VALUES (?,?,?,?,?)");

            for (BillItems item : bill.getBillItems()) {
                stmt.setString(1, item.getFoodItemName());
                stmt.setInt(2, item.getFoodItemQuantity());
                stmt.setInt(3, item.getFoodItemPrice());
                stmt.setInt(4, bill.getBillnumber());
                stmt.setString(5,bill.getTableNumber());

                stmt.executeUpdate();
            }

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public String getOutletDetails(String detail)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
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
                return rs.getString("gstnumber");
            }
            if(detail.equals("phone"))
            {
                return rs.getString("phone");
            }

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void saveOpenTableDetails(HashMap<String,ObservableList<BillItems>> openTables)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("INSERT INTO " + tenantId + ".opentabledetails (fooditemname,fooditemquantity,fooditemprice,tablenumber) VALUES (?,?,?,?) ON CONFLICT (fooditemname, tablenumber) DO UPDATE SET fooditemquantity = excluded.fooditemquantity, fooditemprice = excluded.fooditemprice");

            for (Map.Entry<String, ObservableList<BillItems>> entry : openTables.entrySet())
            {
                String tablenumber = entry.getKey();
                stmt.setString(4,tablenumber);

                ObservableList<BillItems> billdetails = entry.getValue();

                for(BillItems item: billdetails)
                {
                    stmt.setString(1,item.getFoodItemName());
                    stmt.setInt(2,item.getFoodItemQuantity());
                    stmt.setInt(3,item.getFoodItemPrice());

                    stmt.executeUpdate();
                }
            }
            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public HashMap<String,ObservableList<BillItems>> fetchOpenTableDetails()
    {
        HashMap<String,ObservableList<BillItems>> openTableDetails = new HashMap<>();

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("SELECT * FROM " + tenantId + ".opentabledetails",ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                String tableNumber = rs.getString("tablenumber");
                if(!openTableDetails.containsKey(tableNumber))
                {
                    ObservableList<BillItems> billitems = FXCollections.observableArrayList();
                    openTableDetails.put(tableNumber,billitems);
                }
            }

            for (Map.Entry<String, ObservableList<BillItems>> opentables : openTableDetails.entrySet())
            {
                String tableNumber = opentables.getKey();
                rs.beforeFirst();

                ObservableList<BillItems> billitems = FXCollections.observableArrayList();
                while(rs.next())
                {
                    if(rs.getString("tablenumber").equals(tableNumber))
                    {
                        BillItems billItem = new BillItems(rs.getString("fooditemname"),rs.getInt("fooditemprice"),rs.getInt("fooditemquantity"));
                        billitems.add(billItem);
                    }
                }
                openTableDetails.put(tableNumber,billitems);
            }

            stmt.close();

            return openTableDetails;

        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return new HashMap<>();
    }

    public void deleteOpenTableDetails(String tableNumber,String foodItemName)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("DELETE FROM "+tenantId+".opentabledetails WHERE fooditemname = ? AND tablenumber = ? ");
            stmt.setString(1,foodItemName);
            stmt.setString(2,tableNumber);

            int affectedRows = stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }
    }

    public ObservableList<String> getCategories()
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("SELECT DISTINCT fooditemcategory FROM "+tenantId+".menu");
            ResultSet rs = stmt.executeQuery();
            ObservableList<String> categories = FXCollections.observableArrayList();
            while(rs.next())
            {
                categories.add(rs.getString("fooditemcategory"));
            }

            stmt.close();

            return categories;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void closeTable(String openTable) throws SQLException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("DELETE FROM "+tenantId+".opentabledetails WHERE tablenumber = ?");
            stmt.setString(1,openTable);

            stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public HashMap<String, Integer> fetchAreaAndTables()
    {
        HashMap<String, Integer> areaAndTables = new HashMap<>();

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("SELECT tables,area FROM "+tenantId+".tableandarea");
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                areaAndTables.put(  rs.getString("area")  , rs.getInt("tables") );
            }
            stmt.close();

            return areaAndTables;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public boolean addItemToMenu(MenuItems item)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("INSERT INTO "+ tenantId +".menu (fooditemname,fooditemcategory,fooditemprice,fooditemavailability) VALUES (?,?,?,?) ");
            stmt.setString(1,item.getFoodItemName());
            stmt.setString(2,item.getFoodItemCategory());
            stmt.setInt(3,item.getFoodItemPrice());

            String availability = item.getFoodItemAvailability();
            stmt.setBoolean(4, availability.equals("Available"));

            if(stmt.executeUpdate()==1)
            {
                stmt.close();
                c.close();
                return true;
            }
            else
            {
                stmt.close();
                c.close();
                return false;
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }

    }

    public boolean deleteMenuItem(MenuItems item)
    {

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("DELETE FROM "+ tenantId +".menu WHERE fooditemname = ? ");
            stmt.setString(1,item.getFoodItemName());

            int rowsDeleted = stmt.executeUpdate();

            return (rowsDeleted > 0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateMenuItem(MenuItems item)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {

            stmt  = c.prepareStatement("UPDATE "+tenantId+".menu SET fooditemprice = ?, fooditemavailability = ?, fooditemcategory = ? WHERE fooditemname = ?");

            stmt.setInt(1,item.getFoodItemPrice());
            String availability = item.getFoodItemAvailability();
            stmt.setBoolean(2, availability.equals("Available"));
            stmt.setString(3,item.getFoodItemCategory());
            stmt.setString(4,item.getFoodItemName());


            if(stmt.executeUpdate()==1)
            {
                stmt.close();
                c.close();
                return true;
            }
            else
            {
                stmt.close();
                c.close();
                return false;
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateAreaAndTables(HashMap<String,Integer> areaAndTables)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {

            for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
            {
                String area = entry.getKey();
                int tables = entry.getValue();

                stmt  = c.prepareStatement("UPDATE "+tenantId+".tableandarea SET tables = ? WHERE area = ?");

                stmt.setInt(1, tables);
                stmt.setString(2,area);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated == 0) {
                    // Area does not exist, insert a new row
                    stmt = c.prepareStatement("INSERT INTO "+tenantId+".tableandarea (area, tables) VALUES (?, ?)");
                    stmt.setString(1, area);
                    stmt.setInt(2, tables);
                    if(stmt.executeUpdate()!=1)
                        return false;
                }
            }
            return true;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteArea(String area)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement("DELETE FROM "+ tenantId +".tableandarea WHERE area = ? ");
            stmt.setString(1,area);

            return (stmt.executeUpdate() > 0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<String> fetchReservedTables()
    {
        Statement stmt;
        ArrayList<String> reservedTables = new ArrayList<>();

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM "+ tenantId +".reservedtables");

            while(rs.next())
            {
                reservedTables.add(rs.getString("tablename"));
            }

            rs.close();
            c.close();
            stmt.close();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return reservedTables;
    }

    public boolean saveReservedTableDetails(String reservedTableNumber)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("INSERT INTO " + tenantId + ".reservedtables (tablename) VALUES (?)");
            stmt.setString(1,reservedTableNumber);

            if(stmt.executeUpdate()>0)
            {
                stmt.close();
                return true;
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

    public boolean deleteReservedTable(String reservedTableNumber)
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement("DELETE FROM " + tenantId + ".reservedtables where tablename = ?");
            stmt.setString(1,reservedTableNumber);

            if(stmt.executeUpdate()>0)
            {
                stmt.close();
                return true;
            }
            else
            {
                stmt.close();
                return false;
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        return false;
    }

}
