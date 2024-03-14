package org.lenden.model;

import java.util.HashMap;

public class Menu {

    public int id;
    public String foodItemName;

    public Double foodItemPrice;

    public String foodItemAvailability;

    public String foodItemCategory; // Food category ( Main Course / Dessert / Snacks etc..) or Alcohol

    public int stockQuantity; // Amount of stock available

    public HashMap<String,Double> variantData; // Variant (Half/Full/Regular/Medium)

    public String isInventoryTracked;



    //GETTERS AND SETTERS

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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


    public Double getFoodItemPrice() {
        return foodItemPrice;
    }
    public void setFoodItemPrice(Double foodItemPrice) {
        this.foodItemPrice = foodItemPrice;
    }


    public int getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(int stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public HashMap<String, Double> getVariantData() {
        return variantData;
    }

    public void setVariantData(HashMap<String, Double> variantData) {
        this.variantData = variantData;
    }

    public String getIsInventoryTracked() {
        return isInventoryTracked;
    }

    public void setIsInventoryTracked(String isInventoryTracked) {
        this.isInventoryTracked = isInventoryTracked;
    }

}
