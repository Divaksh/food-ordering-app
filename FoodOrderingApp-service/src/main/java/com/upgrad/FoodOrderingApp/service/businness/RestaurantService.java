package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class RestaurantService {

    @Autowired
    private RestaurantDao restaurantDao;

    //This method returns all the restaurants according to the customer ratings
    public List<RestaurantEntity> restaurantsByRating() {
        List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();
        return restaurantEntities;
    }
    //This method checks for the restaurant search field if its empty it throws corresponding exception
    //It also returns the restaurants even if there is partial match in the restaurant in DB and the resto. mentioned in search field

    public List<RestaurantEntity> restaurantsByName(final String restaurantName) throws RestaurantNotFoundException {
        if (restaurantName.isEmpty()) {
            throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
        }

        List<RestaurantEntity> restaurantListByRating = restaurantDao.getAllRestaurantsByRating();
        List<RestaurantEntity> matchingRestaurantList = new ArrayList<>();

        for (RestaurantEntity restaurantEntity : restaurantListByRating) {
            if (restaurantEntity.getRestaurantName().toLowerCase().contains(restaurantName.toLowerCase())) {
                matchingRestaurantList.add(restaurantEntity);
            }
        }

        return matchingRestaurantList;
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(@PathVariable("category_id") final String categoryId)
            throws CategoryNotFoundException {

        List<RestaurantEntity> restaurantListByCategoryId = restaurantService.restaurantByCategory(categoryId);

        RestaurantListResponse restaurantResponseByCategoryId = new RestaurantListResponse();

        if (restaurantListByCategoryId.isEmpty()) {
            return new ResponseEntity<RestaurantListResponse>(restaurantResponseByCategoryId, HttpStatus.NOT_FOUND);
        }

        for (RestaurantEntity restaurantEntity : restaurantListByCategoryId) {
            RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
                            stateName(restaurantEntity.getAddress().getState().getStateName());
            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress().
                    id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
                    flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).
                    locality(restaurantEntity.getAddress().getLocality()).city(restaurantEntity.getAddress().getCity()).
                    pincode(restaurantEntity.getAddress().getPincode()).state(state);
            String categories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
                    .stream().map(rc -> String.valueOf(rc.getCategoryName())).collect(Collectors.joining(","));
            RestaurantList restaurantsByCategory = new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid())).
                    restaurantName(restaurantEntity.getRestaurantName()).photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(new BigDecimal(restaurantEntity.getCustomerRating()))
                    .averagePrice(restaurantEntity.getAvgPrice())
                    .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .address(responseAddress).categories(categories);
            restaurantResponseByCategoryId.addRestaurantsItem(restaurantsByCategory);
        }
        return new ResponseEntity<RestaurantListResponse>(restaurantResponseByCategoryId, HttpStatus.OK);
    }
}
