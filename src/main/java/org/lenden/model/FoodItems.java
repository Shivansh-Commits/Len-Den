package org.lenden.model;

public class FoodItems {

    @Override
    public String toString() {
        return "MenuItems{" +
                "foodItem='" + foodItemName + '\'' +
                ", price='" + foodItemPrice + '\'' +
                ", availability=" + foodItemAvailability +
                '}';
    }

    public String foodItemName;

    public int foodItemPrice;

    public String foodItemAvailability;


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

    public void setFoodItemPrice(int
                                         foodItemPrice) {
        this.foodItemPrice = foodItemPrice;
    }



}
