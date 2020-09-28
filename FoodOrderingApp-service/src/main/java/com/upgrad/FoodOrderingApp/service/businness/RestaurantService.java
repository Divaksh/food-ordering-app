package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    //This method returns all the restaurants according to the customer ratings
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();
        return restaurantEntities;
    }

}
