package org.lenden.model;

public class InventoryPurchase {


    private int purchaseId;
    private String purchaseDate;
    private int inventoryItemId;

    private String inventoryItemName;

    private Double inventoryItemPrice;

    private Double inventoryItemQuantity;

    private String inventoryItemUnit;

    public InventoryPurchase() {
    }

    public InventoryPurchase(int inventoryItemId, String inventoryItemName, Double inventoryItemPrice, Double inventoryItemQuantity, String inventoryItemUnit) {
        this.inventoryItemId = inventoryItemId;
        this.inventoryItemName = inventoryItemName;
        this.inventoryItemPrice = inventoryItemPrice;
        this.inventoryItemQuantity = inventoryItemQuantity;
        this.inventoryItemUnit = inventoryItemUnit;
    }

    public int getPurchaseId() {
        return purchaseId;
    }

    public void setPurchaseId(int purchaseId) {
        this.purchaseId = purchaseId;
    }

    public String getPurchaseDate() {
        return purchaseDate;
    }

    public void setPurchaseDate(String purchaseDate) {
        this.purchaseDate = purchaseDate;
    }

    public int getInventoryItemId() {
        return inventoryItemId;
    }

    public void setInventoryItemId(int inventoryItemId) {
        this.inventoryItemId = inventoryItemId;
    }

    public String getInventoryItemName() {
        return inventoryItemName;
    }

    public void setInventoryItemName(String inventoryItemName) {
        this.inventoryItemName = inventoryItemName;
    }

    public Double getInventoryItemPrice() {
        return inventoryItemPrice;
    }

    public void setInventoryItemPrice(Double inventoryItemPrice) {
        this.inventoryItemPrice = inventoryItemPrice;
    }

    public Double getInventoryItemQuantity() {
        return inventoryItemQuantity;
    }

    public void setInventoryItemQuantity(Double inventoryItemQuantity) {
        this.inventoryItemQuantity = inventoryItemQuantity;
    }

    public String getInventoryItemUnit() {
        return inventoryItemUnit;
    }

    public void setInventoryItemUnit(String inventoryItemUnit) {
        this.inventoryItemUnit = inventoryItemUnit;
    }

}
