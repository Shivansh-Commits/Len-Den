package org.lenden.model;

import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import org.lenden.dao.DaoImpl;

import java.net.URL;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class Bill {

    String tableNumber;
    double grandTotal;
    double total;
    double subTotal;
    double discount;
    double serviceCharge;
    double cgst;
    double sgst;
    double vat;
    String date;
    ObservableList<BillItems> billItems;


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
             this.date = (new Date()).toString();
         }
         catch(SQLException e)
         {
             e.getMessage();
         }
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public ObservableList<BillItems> getBillItems() {
        return billItems;
    }

    public void setBillItems(ObservableList<BillItems> billItems) {
        this.billItems = billItems;
    }
}
