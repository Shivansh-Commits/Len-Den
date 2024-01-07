package org.lenden.model;

public class MenuItems {

    public String foodItemName;

    public int foodItemPrice;

    public String foodItemAvailability;

    public String foodItemCategory; // Food category ( Main Course / Dessert / Snacks etc..) or Alcohol

    public String stockQuantity; // Amount of stock available

    public String itemType; // Single unit or Dish

    public String itemVariant; // Variant (Half/Full/Regular/Medium)



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


    public String getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(String stockQuantity) {
        this.stockQuantity = stockQuantity;
    }


    public String getItemType() {
        return itemType;
    }

    public void setItemType(String itemType) {
        this.itemType = itemType;
    }
}
