package org.lenden.model;

import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import org.lenden.dao.DaoImpl;
import java.util.Date;


public class Bill {

    String outletName;
    String outletAddress;
    String gstNumber;
    String phone;
    String tableNumber;
    String modeOfpayment;
    int billNumber;
    double grandTotal;
    double total;
    double subTotal;
    double discount;
    double serviceCharge;
    double cgst;
    double sgst;
    double vat;
    String date;
    String status;
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
             this.outletName = daoimpl.getOutletDetails("name");
             this.outletAddress = daoimpl.getOutletDetails("address");
             this.phone = daoimpl.getOutletDetails("phone");
             this.gstNumber = daoimpl.getOutletDetails("gstnumber");
             this.discount = 0;
             this.date = (new Date()).toString();
         }
         catch(Exception e)
         {
             e.printStackTrace();
             Alert alert = new Alert(Alert.AlertType.ERROR, "Database operation Exception - "+e.getMessage(), ButtonType.OK);
             alert.setHeaderText("Failed");
             alert.setTitle("Error!");
             alert.showAndWait();
         }
    }


    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public int getBillnumber() {
        return billNumber;
    }

    public void setBillnumber(int billnumber) {
        this.billNumber = billnumber;
    }

    public String getOutletName() {
        return outletName;
    }

    public void setOutletName(String outletName) {
        this.outletName = outletName;
    }

    public String getOutletAddress() {
        return outletAddress;
    }

    public void setOutletAddress(String outletAddress) {
        this.outletAddress = outletAddress;
    }

    public String getGstNumber() {
        return gstNumber;
    }

    public void setGstNumber(String gstNumber) {
        this.gstNumber = gstNumber;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
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

    public String getModeOfpayment() {  return modeOfpayment; }

    public void setModeOfpayment(String modeOfpayment) {  this.modeOfpayment = modeOfpayment;}
}
