package org.lenden.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.lenden.model.*;
import static org.lenden.LoginController.getTenant;
import java.sql.*;
import java.util.*;


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
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM public.tenants where username = '%s' AND password='%s';", tenantInfo.getUsername(), tenantInfo.getPassword()));
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
            e.printStackTrace();
            throw e;
        }
    }

    public Map.Entry<String, String> getSubscriptionInfo(Tenants tenant) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("SELECT subscriptionstartdate,subscriptionenddate FROM public.tenants where username = ? AND password = ?;"));
            stmt.setString(1,tenant.getUsername());
            stmt.setString(2,tenant.getPassword());

            ResultSet rs = stmt.executeQuery();

            rs.next();

            String startDate = rs.getString("subscriptionstartdate").trim();
            String endDate = rs.getString("subscriptionenddate").trim();  // .trim() is used to remove "\n" or " " at the end of string

            return new AbstractMap.SimpleEntry<>(startDate, endDate);

        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public ObservableList<MenuItems> getCategoryItems(String category) throws Exception
    {
        ObservableList<MenuItems> menuItemList = FXCollections.observableArrayList();
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.menu WHERE fooditemcategory = ?", tenantId));
            stmt.setString(1,category);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                MenuItems menuItem = new MenuItems();

                //Getting ID
                menuItem.setId(rs.getInt("id"));

                //Getting Name
                menuItem.setFoodItemName(rs.getString("fooditemname"));

                //Getting Price
                menuItem.setFoodItemPrice(rs.getDouble("fooditemprice"));

                //Getting Availability
                menuItem.setFoodItemCategory(category);
                if(rs.getBoolean("fooditemavailability"))
                    menuItem.setFoodItemAvailability("Available");
                else
                    menuItem.setFoodItemAvailability("NOT Available");

                //Getting Stock Quantity
                menuItem.setStockQuantity(rs.getInt("stockquantity"));

                //Getting Variant Data
                String variantDataJson = rs.getString("variants");
                try {
                    if (variantDataJson != null && !variantDataJson.isEmpty()) {
                        ObjectMapper objectMapper = new ObjectMapper();
                        TypeReference<HashMap<String, Double>> typeReference = new TypeReference<HashMap<String, Double>>() {};
                        HashMap<String, Double> variantData = objectMapper.readValue(variantDataJson, typeReference);
                        menuItem.setVariantData(variantData);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }

                menuItemList.add(menuItem);
            }

            if(!menuItemList.isEmpty())
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
            throw e;
        }

        return null;
    }

    public double getTax(String tax) throws SQLException
    {
        try(Connection c = ConnectionManager.getConnection())
        {
            PreparedStatement stmt;
            stmt = c.prepareStatement(String.format("SELECT * FROM %s.billsettings", tenantId));
            ResultSet rs = stmt.executeQuery();
            rs.next();
            switch (tax) {
                case "cgst":
                case "sgst":
                    double sgstOrCgst = rs.getDouble("gst");
                    sgstOrCgst = sgstOrCgst/2;
                    rs.close();
                    c.close();
                    stmt.close();
                    return sgstOrCgst;
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
            throw e;
        }
        return 0;
    }

    public void saveTax(String taxType,double defaultTaxValue) throws SQLException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            if(taxType.equals("gst"))
            {
                stmt = c.prepareStatement(String.format("UPDATE %s.billsettings SET gst = ?", tenantId));
                stmt.setDouble(1,defaultTaxValue);
            }
            else if(taxType.equals("vat"))
            {
                stmt = c.prepareStatement(String.format("UPDATE %s.billsettings SET vat = ?", tenantId));
                stmt.setDouble(1,defaultTaxValue);
            }
            else
            {
                stmt = c.prepareStatement(String.format("UPDATE %s.billsettings SET servicecharge = ?", tenantId));
                stmt.setDouble(1,defaultTaxValue);
            }

            if(stmt.executeUpdate()>0)
            {
                stmt.close();
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public int getNextBillNumber() throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            //GETTING NEXT BILL NUMBER
            stmt  = c.prepareStatement(String.format("Select nextbillnumber from %s.bills where billnumber = 0 ", tenantId)); // A specific row where nextbill number is stored.
            ResultSet rs = stmt.executeQuery();
            rs.next();
            int billnumber = rs.getInt("nextbillnumber");

            //SAVING THE VALUE OF NEXT BILL NUMBER
            int nextbillnumber = billnumber + 1;
            stmt  = c.prepareStatement(String.format("UPDATE %s.bills SET nextbillnumber = %d where billnumber = 0", tenantId, nextbillnumber));
            stmt.executeUpdate();

            return billnumber;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public int addBillToDB(Bill bill) throws SQLException
    {

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("INSERT INTO %s.bills (outletname,outletaddress,gstnumber,servicecharge,sgst,cgst,discount,subtotal,total,grandtotal,billdate,billnumber,tablenumber,modeofpayment,status) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);", tenantId));
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
            stmt.setString(14, bill.getModeOfpayment());
            stmt.setString(15, bill.getStatus());

            int rowsAffected = stmt.executeUpdate();

            //Add bill details (food items) to "billdetails" table
            addBillDetails(bill);

            stmt.close();

            return rowsAffected;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public Bill fetchBill(int billNumber) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.bills WHERE billnumber = ?", tenantId));
            stmt.setInt(1, billNumber);
            ResultSet rs = stmt.executeQuery();

            Bill bill = new Bill();
            while(rs.next())
            {
                bill.setBillnumber(rs.getInt("billnumber"));
                bill.setGrandTotal(rs.getDouble("grandtotal"));
                bill.setDiscount(rs.getDouble("discount"));
            }

            stmt.close();

            return bill;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public ObservableList<Bill> fetchAllBills() throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.bills", tenantId));
            ResultSet rs = stmt.executeQuery();

            ObservableList<Bill> bills = FXCollections.observableArrayList();

            while(rs.next())
            {
                Bill bill = new Bill();

                bill.setBillnumber(rs.getInt("billnumber"));
                bill.setTotal(rs.getDouble("total"));
                bill.setGrandTotal(rs.getDouble("grandtotal"));
                bill.setDiscount(rs.getDouble("discount"));
                bill.setModeOfpayment(rs.getString("modeofpayment"));
                bill.setStatus(rs.getString("status"));
                bill.setDate(rs.getString("billdate"));

                bills.add(bill);
            }

            stmt.close();

            return bills;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void changeBillStatus(int billNumber,String status) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("UPDATE %s.bills SET status =  ? WHERE billnumber = ?;", tenantId));
            stmt.setString(1,status);
            stmt.setInt(2, billNumber);


            int rowsAffected = stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void addBillDetails(Bill bill) throws SQLException
    {
        PreparedStatement stmt;


        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.billdetails (fooditemname,fooditemquantity,fooditemprice,billnumber,tablenumber) VALUES (?,?,?,?,?)", tenantId));

            for (BillItems item : bill.getBillItems()) {
                stmt.setString(1, item.getFoodItemName());
                stmt.setInt(2, item.getFoodItemQuantity());
                stmt.setDouble(3, item.getFoodItemPrice());
                stmt.setInt(4, bill.getBillnumber());
                stmt.setString(5,bill.getTableNumber());

                stmt.executeUpdate();
            }

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public String getOutletDetails(String detail) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("Select * from %s.outletdetails ", tenantId));
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
            throw e;
        }

        return null;
    }

    public void updateOutletDetails(String outletName, String outletAddress, String  outletContact , String outletGstNum) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("UPDATE %s.outletdetails SET outletname = ?,outletaddress = ?, phone = ?, gstnumber = ?", tenantId));
            stmt.setString(1,outletName);
            stmt.setString(2,outletAddress);
            stmt.setString(3,outletContact);
            stmt.setString(4,outletGstNum);

            stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void saveOpenTableDetails(HashMap<String,ObservableList<BillItems>> openTables) throws SQLException, JsonProcessingException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.opentabledetails (fooditemname,fooditemquantity,fooditemprice,tablenumber,variant) VALUES (?,?,?,?,?) ON CONFLICT (fooditemname, tablenumber) DO UPDATE SET fooditemquantity = excluded.fooditemquantity, fooditemprice = excluded.fooditemprice", tenantId));

            for (Map.Entry<String, ObservableList<BillItems>> entry : openTables.entrySet())
            {
                String tablenumber = entry.getKey();
                stmt.setString(4,tablenumber);

                ObservableList<BillItems> billdetails = entry.getValue();

                for(BillItems item: billdetails)
                {
                    stmt.setString(1,item.getFoodItemName());
                    stmt.setInt(2,item.getFoodItemQuantity());
                    stmt.setDouble(3,item.getFoodItemPrice());

                    if(item.getVariant() == null)
                    {
                        stmt.setString(5, "");
                    }
                    else
                    {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String variantDataJson = objectMapper.writeValueAsString(item.getVariant());
                            stmt.setString(5, variantDataJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }
                    }

                    stmt.executeUpdate();
                }
            }
            stmt.close();
        }
        catch(SQLException | JsonProcessingException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public HashMap<String,ObservableList<BillItems>> fetchOpenTableDetails() throws SQLException, JsonProcessingException {
        HashMap<String,ObservableList<BillItems>> openTableDetails = new HashMap<>();

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("SELECT * FROM %s.opentabledetails", tenantId),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

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
                        //Processing Variant Data
                        String variantDataJson = rs.getString("variant");
                        HashMap<String, Double> variantData = new HashMap<>();
                        try {
                            if (variantDataJson != null && !variantDataJson.isEmpty()) {
                                ObjectMapper objectMapper = new ObjectMapper();
                                TypeReference<HashMap<String, Double>> typeReference = new TypeReference<HashMap<String, Double>>() {};
                                variantData = objectMapper.readValue(variantDataJson, typeReference);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }

                        BillItems billItem = new BillItems(rs.getString("fooditemname"),rs.getDouble("fooditemprice"),rs.getInt("fooditemquantity"),variantData);
                        billitems.add(billItem);
                    }
                }
                openTableDetails.put(tableNumber,billitems);
            }

            stmt.close();

            return openTableDetails;

        }
        catch(SQLException | JsonProcessingException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public int fetchOpenAndReservedTableCount() throws SQLException
    {
        PreparedStatement stmt;
        int openTables = 0;

        try(Connection c = ConnectionManager.getConnection())
        {
            //Checking for Open Tables
            stmt  = c.prepareStatement(String.format("SELECT COUNT(*) FROM %s.opentabledetails", tenantId));
            ResultSet rs = stmt.executeQuery();

            if (rs.next())
            {
                openTables = rs.getInt(1);
            }

            //Checking for Resserved Tables
            stmt  = c.prepareStatement(String.format("SELECT COUNT(*) FROM %s.reservedtables", tenantId));
             rs = stmt.executeQuery();

            if (rs.next())
            {
                openTables += rs.getInt(1);
            }

            stmt.close();

            return openTables;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void deleteOpenTableDetails(String tableNumber,String foodItemName) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("DELETE FROM %s.opentabledetails WHERE fooditemname = ? AND tablenumber = ? ", tenantId));
            stmt.setString(1,foodItemName);
            stmt.setString(2,tableNumber);

            int affectedRows = stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public ObservableList<String> getCategories() throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT DISTINCT fooditemcategory FROM %s.menu", tenantId));
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
            throw e;
        }

    }

    public void closeTable(String openTable) throws SQLException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("DELETE FROM %s.opentabledetails WHERE tablenumber = ?", tenantId));
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

    public LinkedHashMap<String, Integer> fetchAreaAndTables() throws SQLException
    {
        LinkedHashMap<String, Integer> areaAndTables = new LinkedHashMap<>();

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT tables,area FROM %s.tableandarea ORDER BY area ASC", tenantId));
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                String area = rs.getString("area");
                areaAndTables.put(  rs.getString("area")  , rs.getInt("tables") );
            }
            stmt.close();

            return areaAndTables;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean addItemToMenu(MenuItems item) throws SQLException, JsonProcessingException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("INSERT INTO %s.menu (fooditemname,fooditemcategory,fooditemprice,fooditemavailability,stockquantity,variants) VALUES (?,?,?,?,?,?) ", tenantId));
            stmt.setString(1,item.getFoodItemName());
            stmt.setString(2,item.getFoodItemCategory());
            stmt.setDouble(3,item.getFoodItemPrice());

            String availability = item.getFoodItemAvailability();
            stmt.setBoolean(4, availability.equals("Available"));
            stmt.setInt(5,item.getStockQuantity());
            if(item.getVariantData().size()==0)
            {
                stmt.setString(6, "");
            }
            else
            {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String variantDataJson = objectMapper.writeValueAsString(item.getVariantData());
                    stmt.setString(6, variantDataJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }



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
            throw e;
        }


    }

    public boolean deleteMenuItem(Integer id) throws SQLException
    {

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("DELETE FROM %s.menu WHERE id = ? ", tenantId));
            stmt.setInt(1,id);

            int rowsDeleted = stmt.executeUpdate();

            return (rowsDeleted > 0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateMenuItem(MenuItems item) throws SQLException, JsonProcessingException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {

            stmt  = c.prepareStatement(String.format("UPDATE %s.menu SET fooditemname = ? ,fooditemprice = ?, fooditemavailability = ?, fooditemcategory = ? , stockquantity = ?, variants=?  WHERE id = ?", tenantId));

            //Setting Name
            stmt.setString(1,item.getFoodItemName());

            //Setting Item Price
            stmt.setDouble(2,item.getFoodItemPrice());

            //Setting Availability
            String availability = item.getFoodItemAvailability();
            stmt.setBoolean(3, availability.equals("Available"));

            //Setting Category Data
            stmt.setString(4,item.getFoodItemCategory());

            //Setting Stock Data
            stmt.setInt(5,item.getStockQuantity());

            //Setting Variant Data
            if(item.getVariantData().size()==0)
            {
                stmt.setString(6, "");
            }
            else
            {
                try {
                    ObjectMapper objectMapper = new ObjectMapper();
                    String variantDataJson = objectMapper.writeValueAsString(item.getVariantData());
                    stmt.setString(6, variantDataJson);
                } catch (Exception e) {
                    e.printStackTrace();
                    throw e;
                }
            }

            //Setting ID
            stmt.setInt(7,item.getId());



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
            throw e;
        }
    }

    public boolean updateAreaAndTables(HashMap<String,Integer> areaAndTables) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {

            for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
            {
                String area = entry.getKey();
                int tables = entry.getValue();

                stmt  = c.prepareStatement(String.format("UPDATE %s.tableandarea SET tables = ? WHERE area = ?", tenantId));

                stmt.setInt(1, tables);
                stmt.setString(2,area);

                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated == 0) {
                    // Area does not exist, insert a new row
                    stmt = c.prepareStatement(String.format("INSERT INTO %s.tableandarea (area, tables) VALUES (?, ?)", tenantId));
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
            throw e;
        }
    }

    public boolean deleteArea(String area) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("DELETE FROM %s.tableandarea WHERE area = ? ", tenantId));
            stmt.setString(1,area);

            return (stmt.executeUpdate() > 0);
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public ArrayList<String> fetchReservedTables() throws SQLException
    {
        Statement stmt;
        ArrayList<String> reservedTables = new ArrayList<>();

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s.reservedtables", tenantId));

            while(rs.next())
            {
                reservedTables.add(rs.getString("tablename"));
            }

            rs.close();
            c.close();
            stmt.close();

            return reservedTables;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean saveReservedTableDetails(String reservedTableNumber) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.reservedtables (tablename) VALUES (?)", tenantId));
            stmt.setString(1,reservedTableNumber);

            if(stmt.executeUpdate()>0)
            {
                stmt.close();
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean deleteReservedTable(String reservedTableNumber) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("DELETE FROM %s.reservedtables where tablename = ?", tenantId));
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
            throw e;
        }
    }

    public ArrayList<String> fetchModeOfPayment() throws SQLException {

        String[] modeOfPayments = new String[0];
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT modeofpayment FROM %s.billsettings", tenantId));
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                 modeOfPayments = rs.getString("modeofpayment").split(",");
            }

            ArrayList<String> paymentModesList = new ArrayList<>(Arrays.asList(modeOfPayments));

            stmt.close();

            return paymentModesList;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void saveTakeAwayOrderDetails(int orderNumber, ObservableList<BillItems> takeAwayOrders,String status) throws SQLException, JsonProcessingException {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.takeawayordersdetails (fooditemname,fooditemquantity,fooditemprice,ordernumber,status,variant) VALUES (?,?,?,?,?,?)", tenantId));

                int ordernumber = orderNumber;
                stmt.setInt(4,ordernumber);
                stmt.setString(5,status);

                for(BillItems item: takeAwayOrders)
                {
                    stmt.setString(1,item.getFoodItemName());
                    stmt.setInt(2,item.getFoodItemQuantity());
                    stmt.setDouble(3,item.getFoodItemPrice());

                    if(item.getVariant()==null)
                    {
                        stmt.setString(6, "");
                    }
                    else
                    {
                        try {
                            ObjectMapper objectMapper = new ObjectMapper();
                            String variantDataJson = objectMapper.writeValueAsString(item.getVariant());
                            stmt.setString(6, variantDataJson);
                        } catch (Exception e) {
                            e.printStackTrace();
                            throw e;
                        }
                    }

                    stmt.executeUpdate();
                }

            stmt.close();
        }
        catch(SQLException | JsonProcessingException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public HashMap<String,ObservableList<BillItems>> fetchOpenTakeAwayOrders() throws SQLException, JsonProcessingException {
        HashMap<String,ObservableList<BillItems>> pendingOrdersDetails = new HashMap<>();

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("SELECT * FROM %s.takeawayordersdetails", tenantId),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                String ordernumber = rs.getString("ordernumber");
                if(!pendingOrdersDetails.containsKey(ordernumber))
                {
                    ObservableList<BillItems> billitems = FXCollections.observableArrayList();
                    pendingOrdersDetails.put(ordernumber,billitems);
                }
            }

            for (Map.Entry<String, ObservableList<BillItems>> openTakeAwayOrder : pendingOrdersDetails.entrySet())
            {
                String ordernumber = openTakeAwayOrder.getKey();
                rs.beforeFirst();

                ObservableList<BillItems> billitems = FXCollections.observableArrayList();
                while(rs.next())
                {
                    if(rs.getString("ordernumber").equals(ordernumber))
                    {
                            //Processing Variant Data
                            String variantDataJson = rs.getString("variant");
                            HashMap<String, Double> variantData = new HashMap<>();
                            try {
                                if (variantDataJson != null && !variantDataJson.isEmpty()) {
                                    ObjectMapper objectMapper = new ObjectMapper();
                                    TypeReference<HashMap<String, Double>> typeReference = new TypeReference<HashMap<String, Double>>() {};
                                    variantData = objectMapper.readValue(variantDataJson, typeReference);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                throw e;
                            }

                        BillItems billItem = new BillItems(rs.getString("fooditemname"),rs.getDouble("fooditemprice"),rs.getInt("fooditemquantity"),variantData);
                        billitems.add(billItem);
                    }
                }
                pendingOrdersDetails.put(ordernumber,billitems);
            }

            stmt.close();

            return pendingOrdersDetails;

        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public void closeTakeAwayOrder(int orderNumber) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("DELETE FROM %s.takeawayordersdetails WHERE ordernumber = ? ", tenantId));
            stmt.setInt(1,orderNumber);

            int affectedRows = stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }
    }

    public Double fetchDefaultDiscount() throws SQLException
    {

        Double defaultdiscount=0.0;
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT defaultdiscount FROM %s.billsettings", tenantId));
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                defaultdiscount = Double.parseDouble(rs.getString("defaultdiscount"));
            }

            stmt.close();

            return defaultdiscount;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void updateDefaultDiscount(Double defaultDiscount) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("UPDATE %s.billsettings SET defaultdiscount = ?", tenantId));
            stmt.setDouble(1,defaultDiscount);

            stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public Double fetchMaxDiscount() throws SQLException
    {

        Double maxdiscount=0.0;
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT maxdiscount FROM %s.billsettings", tenantId));
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                maxdiscount = Double.parseDouble(rs.getString("maxdiscount"));
            }

            stmt.close();

            return maxdiscount;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void updateMaxDiscount(Double maxDiscount) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("UPDATE %s.billsettings SET maxdiscount = ?", tenantId));
            stmt.setDouble(1,maxDiscount);

            stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public String fetchDefaultModeOfPayment() throws SQLException
    {

        String defaultModeOfPayment = "";
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT defaultmodeofpayment FROM %s.billsettings", tenantId));
            ResultSet rs = stmt.executeQuery();
            while(rs.next())
            {
                defaultModeOfPayment = rs.getString("defaultmodeofpayment");
            }

            stmt.close();

            return defaultModeOfPayment;
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }

    public void updateDefaultModeOfPayment(String defaultmodeofpayment) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("UPDATE %s.billsettings SET defaultmodeofpayment = ?", tenantId));
            stmt.setString(1,defaultmodeofpayment);

            stmt.executeUpdate();

            stmt.close();
        }
        catch(SQLException e)
        {
            e.printStackTrace();
            throw e;
        }

    }
}
