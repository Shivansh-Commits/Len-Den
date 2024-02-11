package org.lenden.model;

import java.util.HashMap;

public class BillItems {


    public String foodItemName;

    public int foodItemPrice;

    public int foodItemQuantity;

    public HashMap variant;

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }

    public int getFoodItemPrice() {
        return foodItemPrice;
    }

    public void setFoodItemPrice(int foodItemPrice) {
        this.foodItemPrice = foodItemPrice;
    }

    public int getFoodItemQuantity() {
        return foodItemQuantity;
    }

    public void setFoodItemQuantity(int foodItemQuantity) {
        this.foodItemQuantity = foodItemQuantity;
    }


    public HashMap getVariant() {
        return variant;
    }

    public void setVariant(HashMap variant) {
        this.variant = variant;
    }

    public BillItems(String foodItemName, int foodItemPrice, int foodItemQuantity) {
        this.foodItemName = foodItemName;
        this.foodItemPrice = foodItemPrice;
        this.foodItemQuantity = foodItemQuantity;
    }

    public BillItems() {
    }
}
