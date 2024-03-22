package org.lenden.model;

public class Variant
{

    public int id;

    public int menuItemId;

    public String variantName;

    public Double variantPrice;

    public String stockQuantity;

    public Variant()
    {

    }
    public Variant(String variantName, Double variantPrice,String stockQuantity) {

        this.variantName = variantName;
        this.variantPrice = variantPrice;
        this.stockQuantity = stockQuantity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getVariantName() {
        return variantName;
    }

    public void setVariantName(String variantName) {
        this.variantName = variantName;
    }

    public Double getVariantPrice() {
        return variantPrice;
    }

    public void setVariantPrice(Double variantPrice) {
        this.variantPrice = variantPrice;
    }

    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
}
