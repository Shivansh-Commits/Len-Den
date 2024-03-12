package org.lenden.model;

import java.util.ArrayList;

public class Recipe
{
    int menuItemId;

    String variant;

    ArrayList<Inventory> rawMaterials = new ArrayList<>();


    public Recipe() {
    }

    public int getMenuItemId() {
        return menuItemId;
    }

    public void setMenuItemId(int menuItemId) {
        this.menuItemId = menuItemId;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public ArrayList<Inventory> getRawMaterials() {
        return rawMaterials;
    }

    public void setRawMaterials(ArrayList<Inventory> rawMaterials) {
        this.rawMaterials = rawMaterials;
    }
}
