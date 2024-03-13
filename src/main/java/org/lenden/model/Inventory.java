package org.lenden.model;

public class Inventory {

    public int id;

    public String inventoryItemName;

    public double inventoryItemRate;

    public String inventoryItemUnit;

    public double inventoryItemQuantity;



    public Inventory(String newName, Double newRate, String newUnit, Double newQuantity)
    {
        this.inventoryItemName = newName;
        this.inventoryItemRate = newRate;
        this.inventoryItemUnit = newUnit;
        this.inventoryItemQuantity = newQuantity;
    }

    public Inventory() {

    }

    @Override
    public String toString() {
        return this.inventoryItemName; // Display only the name in the ComboBox
    }

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

    public double getInventoryItemRate() {
        return inventoryItemRate;
    }

    public void setInventoryItemRate(double inventoryItemPrice) {
        this.inventoryItemRate = inventoryItemPrice;
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
