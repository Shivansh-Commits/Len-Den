package org.lenden.model;

import javafx.scene.control.Button;

public class MenuItems {

    @Override
    public String toString() {
        return "MenuItems{" +
                "foodItem='" + foodItem + '\'' +
                ", price='" + price + '\'' +
                ", availability=" + availability +
                '}';
    }

    public String foodItem;

    public String price;

    public boolean availability;


    public String getFoodItem() {
        return foodItem;
    }

    public void setFoodItem(String foodItem) {
        this.foodItem = foodItem;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public boolean isAvailability() {
        return availability;
    }

    public void setAvailability(boolean availability) {
        this.availability = availability;
    }

}
