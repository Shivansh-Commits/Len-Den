package org.lenden.model;

public class MenuItems {

    public String foodItemName;

    public int foodItemPrice;

    public String foodItemAvailability;

    public String foodItemCategory;


    //TO STRING


    @Override
    public String toString() {
        return "MenuItems{" +
                "foodItemName='" + foodItemName + '\'' +
                ", foodItemPrice=" + foodItemPrice +
                ", foodItemAvailability='" + foodItemAvailability + '\'' +
                ", foodItemCategory='" + foodItemCategory + '\'' +
                '}';
    }

    //GETTERS AND SETTERS
    public String getFoodItemCategory() {
        return foodItemCategory;
    }
    public void setFoodItemCategory(String foodItemCategory) {
        this.foodItemCategory = foodItemCategory;
    }

    public String getFoodItemAvailability() {
        return foodItemAvailability;
    }
    public void setFoodItemAvailability(String foodItemAvailability) {
        this.foodItemAvailability = foodItemAvailability;
    }

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



}
