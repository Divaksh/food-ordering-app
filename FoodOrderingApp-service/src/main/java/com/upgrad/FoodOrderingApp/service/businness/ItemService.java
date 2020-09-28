package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ItemService {

    //Returns category items based on the input restaurant Id and the category Id
    public List<ItemEntity> getItemsByCategoryAndRestaurant(String restaurantId, String categoryId) {
        RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(restaurantId);
        CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);
        List<ItemEntity> restaurantItemList = new ArrayList<>();

        for (ItemEntity restaurantItem : restaurantEntity.getItems()) {
            for (ItemEntity categoryItem : categoryEntity.getItems()) {
                if (restaurantItem.getUuid().equals(categoryItem.getUuid())) {
                    restaurantItemList.add(restaurantItem);
                }
            }
        }
        restaurantItemList.sort(Comparator.comparing(ItemEntity::getItemName));
        return restaurantItemList;
    }
}
