package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;

import java.util.List;

public class RestaurantService {


    //This method returns all the restaurants according to the customer ratings
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();
        return restaurantEntities;
    }

}
