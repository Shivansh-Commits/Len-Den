package org.lenden.model;

import java.util.List;

public class Bill {

    String tableNumber;
    double grandTotal;
    double discount;
    double serviceCharge;
    List<BillItems> billItems;

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

    public List<BillItems> getBillItems() {
        return billItems;
    }

    public void setBillItems(List<BillItems> billItems) {
        this.billItems = billItems;
    }
}
