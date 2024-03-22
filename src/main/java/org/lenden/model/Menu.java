package org.lenden.model;

import javafx.collections.ObservableList;

public class Menu {

    public int id;
    public String foodItemName;

    public Double foodItemPrice;

    public String foodItemAvailability;

    public String foodItemCategory; // Food category ( Main Course / Dessert / Snacks etc..) or Alcohol

    public String stockQuantity; // Amount of stock available

    public ObservableList<Variant> variantData; // Variant (Half/Full/Regular/Medium etc..)

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


    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public ObservableList<Variant> getVariantData() {
        return variantData;
    }

    public void setVariantData(ObservableList<Variant> variantData) {
        this.variantData = variantData;
    }

    public String getIsInventoryTracked() {
        return isInventoryTracked;
    }

    public void setIsInventoryTracked(String isInventoryTracked) {
        this.isInventoryTracked = isInventoryTracked;
    }

}
