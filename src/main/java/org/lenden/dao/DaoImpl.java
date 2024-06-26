package org.lenden.dao;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.lenden.model.*;

import static java.util.Collections.min;
import static org.lenden.LoginController.getTenant;
import java.sql.*;
import java.time.LocalDate;
import java.util.*;


public class DaoImpl
{

    public Tenants tenant = getTenant();
    public String tenantId = tenant.getUsername();

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
            e.getMessage();
            throw e;
        }
    }

    public Map.Entry<String, String> fetchSubscriptionInfo(Tenants tenant) throws SQLException
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
            e.getMessage();
            throw e;
        }
    }

    public double fetchTax(String tax) throws SQLException
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

            e.getMessage();
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
            e.getMessage();
            throw e;
        }
    }

    public int fetchNextBillNumber() throws SQLException
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
            throw e;
        }
    }

    public String fetchOutletDetails(String detail) throws SQLException
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
            e.getMessage();
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
            e.getMessage();
            throw e;
        }

    }

    public void saveOpenTableDetails(HashMap<String,ObservableList<BillItems>> openTables) throws SQLException, JsonProcessingException
    {
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
                            e.getMessage();
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
            e.getMessage();
            throw e;
        }
    }

    public HashMap<String,ObservableList<BillItems>> fetchOpenTableDetails() throws SQLException, JsonProcessingException
    {
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
                            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
            throw e;
        }
    }

    public ObservableList<String> fetchCategories() throws SQLException
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
            e.getMessage();
            throw e;
        }

    }

    public ObservableList<Menu> fetchCategoryItems(String category) throws Exception
    {
        ObservableList<Menu> menuItemList = FXCollections.observableArrayList();
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.menu WHERE fooditemcategory = ?", tenantId));
            stmt.setString(1,category);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Menu menuItem = new Menu();

                //Getting ID
                menuItem.setId(rs.getInt("id"));

                //Getting Category
                menuItem.setFoodItemCategory("fooditemcategory");

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
                menuItem.setStockQuantity(rs.getString("stockquantity"));

                //Getting Variant Data
                if(rs.getString("isvariantexists").equals("NO"))
                {
                    menuItem.setVariantData(null);
                }
                else
                {
                    try {
                        menuItem.setVariantData(fetchVariants(menuItem.getId()));
                    } catch (Exception e) {
                        e.getMessage();
                        throw e;
                    }
                }

                //Getting isInventoryTracked
                menuItem.setIsInventoryTracked(rs.getString("isInventoryTracked"));

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
            e.getMessage();
            throw e;
        }

        return null;
    }

    public ObservableList<Menu> fetchAllCategoryItems() throws Exception
    {
        ObservableList<Menu> menuItemList = FXCollections.observableArrayList();
        try
        {
            ObservableList<String> categories = FXCollections.observableArrayList();
            categories = fetchCategories();

            for(String category : categories)
            {
                menuItemList.addAll(fetchCategoryItems(category));
            }

            return menuItemList;

        }
        catch (Exception ex)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+ex.getMessage(), ButtonType.OK);
            alert.setHeaderText("Failed");
            alert.setTitle("Error!");
            alert.showAndWait();
        }

        return null;
    }

    public boolean addItemToMenu(Menu item) throws SQLException, JsonProcessingException
    {
        PreparedStatement stmt;
        boolean isVariantExistFlag = false;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("INSERT INTO %s.menu (fooditemname,fooditemcategory,fooditemprice,fooditemavailability,stockquantity,isvariantexists,isinventorytracked) VALUES (?,?,?,?,?,?,?) RETURNING id", tenantId));

            stmt.setString(1,item.getFoodItemName());

            stmt.setString(2,item.getFoodItemCategory());

            stmt.setDouble(3,item.getFoodItemPrice());

            String availability = item.getFoodItemAvailability();

            stmt.setBoolean(4, availability.equals("Available"));

            stmt.setString(5,item.getStockQuantity());

            if(item.getVariantData() == null || item.getVariantData().size() == 0)
            {
                stmt.setString(6,"NO");
            }
            else
            {
                stmt.setString(6,"YES");
                isVariantExistFlag = true;
            }

            stmt.setString(7,"OFF");

            boolean isInserted = stmt.execute();

            if(isInserted) {

                //IF Variants Exist
                if(isVariantExistFlag) {

                    // Get the generated ID from the ResultSet
                    ResultSet generatedKeys = stmt.getResultSet();
                    if (generatedKeys.next()) {

                        int menuItemId = generatedKeys.getInt("id");
                        if (addVariant(menuItemId, item.getVariantData()) || isInserted) {

                            stmt.close();
                            c.close();
                            return true;
                        }
                        else {

                            stmt.close();
                            c.close();
                            return false;
                        }
                    }
                    else {

                        throw new SQLException("Failed to retrieve generated ID for newly added Menu Item");
                    }
                }
                else {

                    if(isInserted) {

                        return true;
                    }
                }
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
            e.getMessage();
            throw e;
        }
    return false;

    }

    public ObservableList<Variant> fetchVariants(int menuItemId) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.variants WHERE menuitemid = ?", tenantId));
            stmt.setInt(1,menuItemId);
            ResultSet rs = stmt.executeQuery();

            ObservableList<Variant> variants = FXCollections.observableArrayList();
            while(rs.next())
            {
                Variant variant = new Variant();

                variant.setId(rs.getInt("id"));
                variant.setMenuItemId(rs.getInt("menuitemid"));
                variant.setVariantName(rs.getString("variantname"));
                variant.setVariantPrice(rs.getDouble("variantprice"));
                variant.setStockQuantity(rs.getString("stockquantity"));

                variants.add(variant);
            }

            stmt.close();

            return variants;
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }
    }

    public boolean addVariant(int menuItemId,ObservableList<Variant> variants) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.variants (variantname,variantprice,menuitemid,stockquantity) VALUES (?,?,?,?)", tenantId));

            for(Variant variant : variants)
            {
                stmt.setString(1, variant.getVariantName());
                stmt.setDouble(2, variant.getVariantPrice());
                stmt.setInt(3, menuItemId);
                stmt.setString(4, variant.getStockQuantity());

                if(stmt.execute() == false)
                {
                    return false;
                }
            }

            stmt.close();
            return true;

        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }
    }

    public boolean updateVariants(int menuItemId, ObservableList<Variant> variants) throws SQLException {

        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection())
        {

            stmt = c.prepareStatement(String.format("DELETE FROM %s.variants WHERE menuitemid = ?",tenantId));
            // Delete existing variants for the specified menuItemId
            stmt.setInt(1, menuItemId);
            stmt.executeUpdate();


            stmt = c.prepareStatement(String.format("INSERT INTO %s.variants (menuitemid, variantname, variantprice,stockquantity) VALUES (?, ?, ? ,?)",tenantId));
            // Insert updated variants
            for (Variant variant : variants) {

                stmt.setInt(1, menuItemId);
                stmt.setString(2, variant.getVariantName());
                stmt.setDouble(3, variant.getVariantPrice());
                stmt.setString(4, variant.getStockQuantity());
                if(stmt.executeUpdate()<0)
                    {
                        return false;
                    }
            }
            return true;
        }
        catch (SQLException e) {

            e.getMessage();
            throw e;
        }
    }


    public boolean deleteMenuItem(Integer id) throws SQLException
    {
        int rowsDeleted = 0;
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("DELETE FROM %s.menu WHERE id = ? ", tenantId));
            stmt.setInt(1,id);

            rowsDeleted = rowsDeleted +  stmt.executeUpdate();

            stmt  = c.prepareStatement(String.format("DELETE FROM %s.recipe WHERE menuitemid = ? ", tenantId));
            stmt.setInt(1,id);

            rowsDeleted = rowsDeleted +  stmt.executeUpdate();

            stmt  = c.prepareStatement(String.format("DELETE FROM %s.variants WHERE menuitemid = ? ", tenantId));
            stmt.setInt(1,id);

            rowsDeleted = rowsDeleted +  stmt.executeUpdate();


            return (rowsDeleted > 0);
        }
        catch(Exception e)
        {
            e.getMessage();
            throw e;
        }
    }

    public boolean updateMenuItem(Menu item) throws SQLException, JsonProcessingException
    {
        try (Connection c = ConnectionManager.getConnection()) {
            String tableName = tenantId + ".menu";
            StringBuilder queryBuilder = new StringBuilder("UPDATE ").append(tableName).append(" SET ");

            Map<String, Object> fieldsToUpdate = new HashMap<>();

            // Add non-null fields to the fieldsToUpdate map
            if (item.getStockQuantity() != null) fieldsToUpdate.put("stockquantity", item.getStockQuantity());
            if (item.getFoodItemCategory() != null) fieldsToUpdate.put("fooditemcategory", item.getFoodItemCategory());
            if (item.getFoodItemName() != null) fieldsToUpdate.put("fooditemname", item.getFoodItemName());
            if (item.getFoodItemPrice() != null) fieldsToUpdate.put("fooditemprice", item.getFoodItemPrice());
            if (item.getIsInventoryTracked() != null) fieldsToUpdate.put("isinventorytracked", item.getIsInventoryTracked());

            // Handle fooditemavailability specially
            if (item.getFoodItemAvailability() != null) {
                // Use a ternary operator to convert the availability string to a boolean
                boolean isAvailable = item.getFoodItemAvailability().equals("Available");
                fieldsToUpdate.put("fooditemavailability", isAvailable);
            }

            // Append field updates to the query
            int index = 0;
            for (Map.Entry<String, Object> entry : fieldsToUpdate.entrySet()) {
                if (index > 0) {
                    queryBuilder.append(", ");
                }
                queryBuilder.append(entry.getKey()).append(" = ?");
                index++;
            }

            // Append WHERE clause
            queryBuilder.append(" WHERE id = ?");

            // Create and execute the prepared statement
            PreparedStatement stmt = c.prepareStatement(queryBuilder.toString());

            // Set parameter values for fieldsToUpdate
            int parameterIndex = 1;
            for (Object value : fieldsToUpdate.values()) {
                if (value instanceof Boolean) {
                    stmt.setBoolean(parameterIndex++, (Boolean) value);
                } else if (value instanceof Double) {
                    stmt.setDouble(parameterIndex++, (Double) value);
                } else {
                    stmt.setString(parameterIndex++, value.toString());
                }
            }

            // Set parameter value for id
            stmt.setInt(parameterIndex, item.getId());

            // Execute the update
            int rowsUpdated = stmt.executeUpdate();
            stmt.close();
            c.close();


            // Update variant properties if variant data is not null
            if (item.getVariantData() != null && !item.getVariantData().isEmpty()) {
                boolean variantsUpdated = updateVariants(item.getId(), item.getVariantData());
                if (!variantsUpdated) {
                    // Handle failure to update variants
                    return false;
                }
            }

            return rowsUpdated == 1;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    public boolean updateAreaAndTables(HashMap<String,Integer> areaAndTables) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {

            //Deleting previous areas and tables data
            stmt  = c.prepareStatement(String.format("DELETE FROM %s.tableandarea", tenantId));
            stmt.executeUpdate();

            for(Map.Entry<String, Integer> entry : areaAndTables.entrySet())
            {
                String area = entry.getKey();
                int tables = entry.getValue();

                // Area does not exist, insert a new row
                stmt = c.prepareStatement(String.format("INSERT INTO %s.tableandarea (area, tables) VALUES (?, ?)", tenantId));
                stmt.setString(1, area);
                stmt.setInt(2, tables);


                if(stmt.executeUpdate()!=1)
                    return false;
            }
            return true;
        }
        catch(Exception e)
        {
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
                c.close();
                return true;
            }
            return false;
        }
        catch(Exception e)
        {
            e.getMessage();
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
                c.close();
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
            e.getMessage();
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
            e.getMessage();
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
                            e.getMessage();
                            throw e;
                        }
                    }

                    stmt.executeUpdate();
                }

            stmt.close();
        }
        catch(SQLException | JsonProcessingException e)
        {
            e.getMessage();
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
                                e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
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
            e.getMessage();
            throw e;
        }

    }

    public ObservableList<Inventory> fetchInventoryItems() throws Exception
    {
        ObservableList<Inventory> inventoryItems = FXCollections.observableArrayList();
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.inventory", tenantId));
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                Inventory inventoryItem = new Inventory();

                //Getting ID
                inventoryItem.setId(rs.getInt("id"));

                //Getting Name
                inventoryItem.setInventoryItemName(rs.getString("name"));

                //Getting Price
                inventoryItem.setInventoryItemRate(rs.getDouble("rate"));

                //Getting unit
                inventoryItem.setInventoryItemUnit(rs.getString("unit"));

                //Getting Stock Quantity
                inventoryItem.setInventoryItemQuantity(rs.getDouble("quantity"));


                inventoryItems.add(inventoryItem);
            }

            if(!inventoryItems.isEmpty())
            {
                rs.close();
                stmt.close();
                c.close();
                return inventoryItems;
            }

            rs.close();
            stmt.close();
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }

        return null;
    }

    public Inventory fetchSpecificInventoryItem(int id) throws Exception
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.inventory WHERE id = ?", tenantId));
            stmt.setInt(1,id);
            ResultSet rs = stmt.executeQuery();

            Inventory inventoryItem = new Inventory();

            while(rs.next())
            {
                //Getting ID
                inventoryItem.setId(rs.getInt("id"));

                //Getting Name
                inventoryItem.setInventoryItemName(rs.getString("name"));

                //Getting Price
                inventoryItem.setInventoryItemRate(rs.getDouble("rate"));

                //Getting unit
                inventoryItem.setInventoryItemUnit(rs.getString("unit"));

                //Getting Stock Quantity
                inventoryItem.setInventoryItemQuantity(rs.getDouble("quantity"));

            }

            if(inventoryItem != null)
            {
                rs.close();
                stmt.close();
                c.close();
                return inventoryItem;
            }

            rs.close();
            stmt.close();
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }

        return null;
    }

    public boolean deleteInventoryItem(int id) throws SQLException
    {

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("DELETE FROM %s.inventory WHERE id = ? ", tenantId));
            stmt.setInt(1,id);

            int rowsDeleted = stmt.executeUpdate();

            return (rowsDeleted > 0);
        }
        catch(Exception e)
        {
            e.getMessage();
            throw e;
        }
    }

    public boolean updateInventoryItem(Inventory inventoryItem) throws SQLException
    {
        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection()) {
            stmt = c.prepareStatement(String.format("UPDATE %s.inventory SET name=?, rate=?, unit=?, quantity=? WHERE id=?", tenantId));
            stmt.setString(1, inventoryItem.getInventoryItemName());
            stmt.setDouble(2, inventoryItem.getInventoryItemRate());
            stmt.setString(3, inventoryItem.getInventoryItemUnit());
            stmt.setDouble(4, inventoryItem.getInventoryItemQuantity());
            stmt.setLong(5, inventoryItem.getId());

            int rowsUpdated = stmt.executeUpdate();

            return (rowsUpdated > 0);
        } catch (SQLException e) {
            e.getMessage();
            throw e;
        }
    }

    public boolean addNewInventoryItem(Inventory newItem) throws SQLException
    {
        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection()) {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.inventory (name, rate, unit, quantity) VALUES (?, ?, ?, ?)", tenantId));
            stmt.setString(1, newItem.getInventoryItemName());
            stmt.setDouble(2, newItem.getInventoryItemRate());
            stmt.setString(3, newItem.getInventoryItemUnit());
            stmt.setDouble(4, newItem.getInventoryItemQuantity());

            int rowsInserted = stmt.executeUpdate();

            return (rowsInserted > 0);
        } catch (SQLException e) {
            e.getMessage();
            throw e;
        }
    }

    public boolean addInventoryItemPurchase(InventoryPurchase inventorypurchase) throws SQLException
    {
        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection()) {

            //UPDATING Inventory Purchase Table
            stmt = c.prepareStatement(String.format("INSERT INTO %s.inventorypurchases (inventoryitemid, inventoryitemname, inventoryitemcost, inventoryitemunit, inventoryitemquantity, purchasedate) VALUES (?, ?, ?, ? ,? ,?)", tenantId));
            stmt.setLong(1,inventorypurchase.getInventoryItemId());
            stmt.setString(2, inventorypurchase.getInventoryItemName());
            stmt.setDouble(3, inventorypurchase.getInventoryItemPrice());
            stmt.setString(4, inventorypurchase.getInventoryItemUnit());
            stmt.setDouble(5, inventorypurchase.getInventoryItemQuantity());
            stmt.setString(6, String.valueOf(LocalDate.now()));

            int rowsInserted = stmt.executeUpdate();

            //UPDATING quantity in Inventory Item
            stmt = c.prepareStatement(String.format("UPDATE %s.inventory SET quantity = ? + quantity WHERE id = ?",tenantId));
            stmt.setDouble(1,inventorypurchase.getInventoryItemQuantity());
            stmt.setInt(2,inventorypurchase.getInventoryItemId());
            stmt.executeUpdate();


            //UPDATING Avg Rate/Unit in Inventory Item
            stmt = c.prepareStatement(String.format("SELECT inventoryitemcost,inventoryitemquantity FROM %s.inventorypurchases WHERE inventoryitemid = ?",tenantId));
            stmt.setInt(1,inventorypurchase.getInventoryItemId());

            ResultSet rs = stmt.executeQuery();

            double totalCost = 0.0;
            double totalQuantity = 0.0;
            double avgRatePerUnit;
            while(rs.next())
            {
                totalCost = totalCost + rs.getDouble("inventoryitemcost");
                totalQuantity = totalQuantity + rs.getDouble("inventoryitemquantity");
            }
            avgRatePerUnit = totalCost/totalQuantity;

            stmt = c.prepareStatement(String.format("UPDATE %s.inventory SET rate = ? WHERE id = ?",tenantId));
            stmt.setDouble(1,avgRatePerUnit);
            stmt.setDouble(2,inventorypurchase.getInventoryItemId());
            stmt.executeUpdate();

            return (rowsInserted > 0);
        } catch (SQLException e) {
            e.getMessage();
            throw e;
        }
    }

    public ObservableList<InventoryPurchase> fetchInventoryPurchaseItems(int inventoryItemId) throws SQLException
    {
        ObservableList<InventoryPurchase> inventoryItems = FXCollections.observableArrayList();
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT * FROM %s.inventorypurchases WHERE inventoryitemid = ?", tenantId));
            stmt.setInt(1,inventoryItemId);
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                InventoryPurchase inventoryPurchaseItem = new InventoryPurchase();

                //Getting Purchase ID
                inventoryPurchaseItem.setPurchaseId(rs.getInt("purchaseid"));

                //Getting Inventory Item ID
                inventoryPurchaseItem.setInventoryItemId(rs.getInt("inventoryitemid"));

                //Getting Name
                inventoryPurchaseItem.setInventoryItemName(rs.getString("inventoryitemname"));

                //Getting Price
                inventoryPurchaseItem.setInventoryItemPrice(rs.getDouble("inventoryitemcost"));

                //Getting unit
                inventoryPurchaseItem.setInventoryItemUnit(rs.getString("inventoryitemunit"));

                //Getting Stock Quantity
                inventoryPurchaseItem.setInventoryItemQuantity(rs.getDouble("inventoryitemquantity"));

                //Getting Purchase Date
                inventoryPurchaseItem.setPurchaseDate(rs.getString("purchasedate"));


                inventoryItems.add(inventoryPurchaseItem);
            }

            if(!inventoryItems.isEmpty())
            {
                rs.close();
                stmt.close();
                c.close();
                return inventoryItems;
            }

            rs.close();
            stmt.close();
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }

        return null;
    }

    public boolean updateInventoryPurchaseItem(InventoryPurchase inventoryPurchase) throws SQLException
    {
        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection()) {
            stmt = c.prepareStatement(String.format("UPDATE %s.inventorypurchases SET inventoryitemname=?, inventoryitemcost=?, inventoryitemunit=?, inventoryitemquantity=? WHERE purchaseid=?", tenantId));
            stmt.setString(1, inventoryPurchase.getInventoryItemName());
            stmt.setDouble(2, inventoryPurchase.getInventoryItemPrice());
            stmt.setString(3, inventoryPurchase.getInventoryItemUnit());
            stmt.setDouble(4, inventoryPurchase.getInventoryItemQuantity());
            stmt.setLong(5, inventoryPurchase.getPurchaseId());
            int rowsUpdated1 = stmt.executeUpdate();

            //UPDATING QUANTITY & AVG Rate/Unit in INVENTORY TABLE
            stmt = c.prepareStatement(String.format("SELECT inventoryitemcost,inventoryitemquantity FROM %s.inventorypurchases WHERE inventoryitemid = ? ",tenantId));
            stmt.setInt(1,inventoryPurchase.getInventoryItemId());
            ResultSet rs = stmt.executeQuery();

            double updatedQuantity = 0.0;
            double updatedCost = 0.0;
            while (rs.next())
             {
                 updatedQuantity = updatedQuantity + rs.getDouble("inventoryitemquantity");
                 updatedCost = updatedCost + rs.getDouble("inventoryitemcost");
             }
            double avgRatePerUnit = updatedCost/updatedQuantity;

            stmt = c.prepareStatement(String.format("UPDATE %s.inventory SET quantity = ?, rate = ? WHERE id = ?",tenantId));
            stmt.setDouble(1,updatedQuantity);
            stmt.setDouble(2,avgRatePerUnit);
            stmt.setInt(3,inventoryPurchase.getInventoryItemId());
            int rowsUpdated2 = stmt.executeUpdate();


            return (rowsUpdated1 > 0 && rowsUpdated2 > 0);

        } catch (SQLException e) {
            e.getMessage();
            throw e;
        }
    }

    public boolean deleteInventoryPurchaseItem(int purchaseid) throws SQLException {
        PreparedStatement stmt;
    
        try (Connection c = ConnectionManager.getConnection()) {
            // Retrieve the inventory purchase information before deletion
            stmt = c.prepareStatement(String.format("SELECT inventoryitemid, inventoryitemcost, inventoryitemquantity FROM %s.inventorypurchases WHERE purchaseid = ?", tenantId));
            stmt.setInt(1, purchaseid);
            ResultSet rs = stmt.executeQuery();
    
            double updatedQuantity = 0.0;
            double updatedCost = 0.0;
            int inventoryItemId = 0;
    
            // Calculate the updated quantity and cost
            while (rs.next()) {
                updatedQuantity += rs.getDouble("inventoryitemquantity");
                updatedCost += rs.getDouble("inventoryitemcost");
                inventoryItemId = rs.getInt("inventoryitemid");
            }
            
            // Delete the inventory purchase item
            stmt = c.prepareStatement(String.format("DELETE FROM %s.inventorypurchases WHERE purchaseid = ?", tenantId));
            stmt.setInt(1, purchaseid);
            int rowsDeleted = stmt.executeUpdate();
    
            // Update the quantity and rate in the inventory table
            stmt = c.prepareStatement(String.format("UPDATE %s.inventory SET quantity = ?, rate = ? WHERE id = ?", tenantId));
            stmt.setDouble(1, updatedQuantity);
            stmt.setDouble(2, updatedQuantity != 0 ? updatedCost / updatedQuantity : 0); // Avoid division by zero
            stmt.setInt(3, inventoryItemId);
            int rowsUpdated = stmt.executeUpdate();
    
            return (rowsDeleted > 0 && rowsUpdated > 0);
        } catch (SQLException e) {
            e.getMessage();
            throw e;
        }
    }
    
    public ObservableList<String> fetchUnits() throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT DISTINCT unit FROM %s.inventory", tenantId));
            ResultSet rs = stmt.executeQuery();
            ObservableList<String> units = FXCollections.observableArrayList();
            while(rs.next())
            {
                units.add(rs.getString("unit"));
            }

            stmt.close();

            return units;
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }

    }

    public ObservableList<String> fetchInventoryItemsNames() throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT DISTINCT name,id FROM %s.inventory", tenantId));
            ResultSet rs = stmt.executeQuery();
            ObservableList<String> names = FXCollections.observableArrayList();
            while(rs.next())
            {
                names.add(rs.getString("name")+" "+"["+rs.getInt("id")+"]");
            }

            stmt.close();

            return names;
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }

    }

    public long addRecipe(Recipe recipe) throws SQLException
    {
        PreparedStatement stmt;
        ResultSet generatedKeys; // ResultSet to hold the generated keys

        try (Connection c = ConnectionManager.getConnection()) {
            stmt = c.prepareStatement(String.format("INSERT INTO %s.recipe (menuitemid, variant, rawmaterials) VALUES (?, ?, ?) RETURNING id", tenantId));
            stmt.setInt(1, recipe.getMenuItemId());
            stmt.setString(2, recipe.getVariant());

            // Serialize the rawMaterials ArrayList to JSON using ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            String rawMaterialsJson = mapper.writeValueAsString(recipe.getRawMaterials());
            stmt.setString(3, rawMaterialsJson);

            boolean inserted = stmt.execute();

            // Check if the insert was successful
            if (inserted)
            {
                generatedKeys = stmt.getResultSet(); // Retrieve the generated keys

                if (generatedKeys.next()) {
                    // Get the generated ID from the ResultSet
                    long generatedId = generatedKeys.getLong("id");
                    return generatedId;
                }
                else
                {
                    throw new SQLException("Failed to retrieve generated ID for recipe");
                }
            }
            else
            {
                throw new SQLException("Failed to insert recipe");
            }
        } catch (SQLException e) {
            e.getMessage(); // Handle the exception appropriately
            throw e;
        } catch (JsonProcessingException e) {
            e.getMessage(); // Handle the exception appropriately
            throw new RuntimeException(e);
        }
    }

    public boolean updateRecipe(Recipe recipe) throws SQLException
    {
        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection()) {
            stmt = c.prepareStatement(String.format("UPDATE %s.recipe SET rawmaterials = ? WHERE menuitemid = ? AND variant = ? AND id= ?", tenantId));

            // Serialize the rawMaterials ArrayList to JSON using ObjectMapper
            ObjectMapper mapper = new ObjectMapper();
            String rawMaterialsJson = mapper.writeValueAsString(recipe.getRawMaterials());
            stmt.setString(1, rawMaterialsJson);

            stmt.setInt(2, recipe.getMenuItemId());
            stmt.setString(3, recipe.getVariant());
            stmt.setInt(4,recipe.getId());


            int rowsInserted = stmt.executeUpdate();

            return (rowsInserted > 0);
        }
        catch (SQLException e)
        {
            e.getMessage();
            throw e;
        }
        catch (JsonProcessingException e)
        {
            e.getMessage();
            throw new RuntimeException(e);
        }
    }

    public boolean deleteRecipe(int recipeId) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("DELETE FROM %s.recipe WHERE id = ? ", tenantId));
            stmt.setInt(1,recipeId);

            int rowsDeleted = stmt.executeUpdate();

            return (rowsDeleted > 0);
        }
        catch(Exception e)
        {
            e.getMessage();
            throw e;
        }
    }

    public Recipe fetchRecipe(int menuItemId,String variant) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt  = c.prepareStatement(String.format("SELECT id,menuitemid, variant, rawmaterials FROM %s.recipe WHERE menuitemid = ? AND variant = ?", tenantId));
            stmt.setInt(1, menuItemId);
            stmt.setString(2, variant);
            ResultSet rs = stmt.executeQuery();

            Recipe recipe = new Recipe();
            if(rs.next())
            {
                recipe.setId(rs.getInt("id"));
                recipe.setMenuItemId(rs.getInt("menuitemid"));
                recipe.setVariant(rs.getString("variant"));

                ObjectMapper mapper = new ObjectMapper();
                ArrayList<Inventory> rawMaterials = mapper.readValue(rs.getString("rawmaterials"), new TypeReference<ArrayList<Inventory>>() {});
                recipe.setRawMaterials(rawMaterials);
            }
            else
            {
                recipe = null;
            }

            stmt.close();

            return recipe;
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }
        catch (JsonProcessingException e)
        {
            e.getMessage();
            throw new RuntimeException(e);
        }
    }

    public boolean changeTrackingStatus(int menuItemId,String status) throws SQLException
    {
        PreparedStatement stmt;

        try (Connection c = ConnectionManager.getConnection()) {
            stmt = c.prepareStatement(String.format("UPDATE %s.menu SET isinventorytracked = ? WHERE id = ?", tenantId));

            stmt.setString(1, status);
            stmt.setInt(2, menuItemId);


            int rowsUpdated = stmt.executeUpdate();

            return (rowsUpdated > 0);
        }
        catch (SQLException e)
        {
            e.getMessage();
            throw e;
        }
    }

    public int checkIfRecipeExists(int menuItemId,String variant) throws SQLException
    {

        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            if(variant == null)
            {
                stmt = c.prepareStatement(String.format("SELECT id FROM %s.recipe WHERE menuitemid = ? ", tenantId));
                stmt.setInt(1,menuItemId);
                ResultSet rs = stmt.executeQuery();

                if(rs.next())
                {
                    return rs.getInt("id");
                }
                else
                {
                    stmt.close();
                    return 0;
                }
            }
            else
            {
                stmt = c.prepareStatement(String.format("SELECT id FROM %s.recipe WHERE menuitemid = ? and variant = ?", tenantId));
                stmt.setInt(1,menuItemId);
                stmt.setString(2,variant);
                ResultSet rs = stmt.executeQuery();

                if(rs.next())
                {
                    return rs.getInt("id");
                }
                else
                {
                    stmt.close();
                    return 0;
                }
            }
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }

    }

    public int checkIfRawMaterialIsUsedInRecipe(int rawMaterialId) throws SQLException
    {
        PreparedStatement stmt;

        try(Connection c = ConnectionManager.getConnection())
        {
            stmt = c.prepareStatement(String.format("SELECT menuitemid,rawmaterials FROM %s.recipe  ", tenantId));
            ResultSet rs = stmt.executeQuery();

            while(rs.next())
            {
                ObjectMapper mapper = new ObjectMapper();
                ArrayList<Inventory> rawMaterials = mapper.readValue(rs.getString("rawmaterials"), new TypeReference<ArrayList<Inventory>>() {});
                for(Inventory rawMaterial : rawMaterials)
                {
                    if(rawMaterial.getId()==rawMaterialId)
                    {
                        return rs.getInt("menuitemid");
                    }
                }
            }

            rs.close();
            c.close();
            stmt.close();
            return -1;
        }
        catch(SQLException e)
        {
            e.getMessage();
            throw e;
        }
        catch (JsonProcessingException e)
        {

            throw new RuntimeException(e);
        }
    }

    public Integer calculateStockQuantity(Menu menuItem,String variant) throws Exception
    {
        ArrayList<Integer> stock = new ArrayList<Integer>();

        Recipe recipe = fetchRecipe(menuItem.getId(), variant);
        ArrayList<Inventory> recipeRawMaterials = recipe.getRawMaterials();

        for(Inventory recipeRawMaterial :recipeRawMaterials)
        {
            Inventory inventoryItem = fetchSpecificInventoryItem(recipeRawMaterial.getId());

            int inventoryItemStockQuantity = (int)(inventoryItem.getInventoryItemQuantity() / recipeRawMaterial.getInventoryItemQuantity());
            if( inventoryItemStockQuantity >= 0)
            {
                stock.add(inventoryItemStockQuantity);
            }
        }

        return min(stock);
    }
}
