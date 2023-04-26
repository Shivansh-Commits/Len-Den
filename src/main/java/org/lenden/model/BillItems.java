package org.lenden.model;

public class BillItems {

    public String foodItemName;

    public int foodItemPrice;

    public int foodItemQuantity;

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


    public BillItems(String foodItemName, int foodItemPrice, int foodItemQuantity) {
        this.foodItemName = foodItemName;
        this.foodItemPrice = foodItemPrice;
        this.foodItemQuantity = foodItemQuantity;
    }

    public BillItems() {
    }
}
