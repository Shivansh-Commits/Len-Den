package org.lenden.model;

public class Inventory {

    private int id;

    private String inventoryItemName;

    private double inventoryItemPrice;

    private String inventoryItemUnit;

    private double inventoryItemQuantity;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = inventoryItemName;
    }

    public double getInventoryItemPrice() {
        return inventoryItemPrice;
    }

    public void setInventoryItemPrice(double inventoryItemPrice) {
        this.inventoryItemPrice = inventoryItemPrice;
    }

    public String getInventoryItemUnit() {
        return inventoryItemUnit;
    }

    public void setInventoryItemUnit(String inventoryItemUnit) {
        this.inventoryItemUnit = inventoryItemUnit;
    }

    public double getInventoryItemQuantity() {
        return inventoryItemQuantity;
    }

    public void setInventoryItemQuantity(double inventoryItemQuantity) {
        this.inventoryItemQuantity = inventoryItemQuantity;
    }
}
