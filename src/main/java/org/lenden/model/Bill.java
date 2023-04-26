package org.lenden.model;

import javafx.fxml.Initializable;
import org.lenden.dao.DaoImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

public class Bill {

    String tableNumber;
    double grandTotal;
    double discount;
    double serviceCharge;
    double cgst;
    double sgst;
    double vat;
    List<BillItems> billItems;

     public Bill()
     {
         try
         {
             DaoImpl daoimpl = new DaoImpl();
             this.cgst = daoimpl.getTax("cgst");
             this.sgst = daoimpl.getTax("sgst");
             this.vat = daoimpl.getTax("vat");
             this.serviceCharge = daoimpl.getTax("servicecharge");
             this.discount = 0;
         }
         catch(SQLException e)
         {
             e.getMessage();
         }
    }

    public String getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(String tableNumber) {
        this.tableNumber = tableNumber;
    }

    public double getGrandTotal() {
        return grandTotal;
    }

    public void setGrandTotal(double grandTotal) {
        this.grandTotal = grandTotal;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public double getServiceCharge() {
        return serviceCharge;
    }

    public void setServiceCharge(double serviceCharge) {
        this.serviceCharge = serviceCharge;
    }

    public double getCgst() {
        return cgst;
    }

    public void setCgst(double cgst) {
        this.cgst = cgst;
    }

    public double getSgst() {
        return sgst;
    }

    public void setSgst(double sgst) {
        this.sgst = sgst;
    }

    public double getVat() {
        return vat;
    }

    public void setVat(double vat) {
        this.vat = vat;
    }

    public List<BillItems> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItems> billItems) {
        this.billItems = billItems;
    }
}
