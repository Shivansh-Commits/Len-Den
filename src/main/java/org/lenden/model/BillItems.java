package org.lenden.model;

import java.util.HashMap;

public class BillItems {


    public String foodItemName;

    public Double foodItemPrice;

    public int foodItemQuantity;

    public HashMap variant;

    public String getFoodItemName() {
        return foodItemName;
    }

    public void setFoodItemName(String foodItemName) {
        this.foodItemName = foodItemName;
    }

    public Double getFoodItemPrice() {
        return foodItemPrice;
    }

    public void setFoodItemPrice(Double foodItemPrice) {
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

    public BillItems(String foodItemName, Double foodItemPrice, int foodItemQuantity) {
        this.foodItemName = foodItemName;
        this.foodItemPrice = foodItemPrice;
        this.foodItemQuantity = foodItemQuantity;
    }

    public BillItems() {
    }
}
