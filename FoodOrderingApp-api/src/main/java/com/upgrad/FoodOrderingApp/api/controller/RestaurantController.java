package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.*;
import com.upgrad.FoodOrderingApp.api.provider.BearerAuthDecoder;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/")
public class RestaurantController {

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ItemService itemService;

    @Autowired
    private CustomerService customerService;

    //This method returns  all the restaurants in order of their ratings
    //It display the response in a JSON format with the corresponding HTTP status
    //And within each restaurant it displays restaurant categories in alphabetical order

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getAllRestaurants() {

        List<RestaurantEntity> restaurantEntities = restaurantService.restaurantsByRating();

        RestaurantListResponse restaurantListResponse = new RestaurantListResponse();

        for (RestaurantEntity restaurantEntity : restaurantEntities) {


            RestaurantDetailsResponseAddressState addressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
                            stateName(restaurantEntity.getAddress().getState().getStateName());


            RestaurantDetailsResponseAddress address = new RestaurantDetailsResponseAddress().
                    id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
                    flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).
                    locality(restaurantEntity.getAddress().getLocality()).city(restaurantEntity.getAddress().getCity()).
                    pincode(restaurantEntity.getAddress().getPincode()).state(addressState);


            String restaurantCategories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
                    .stream().map(rc -> String.valueOf(rc.getCategoryName())).collect(Collectors.joining(","));


            RestaurantList restaurantList = new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid())).
                    restaurantName(restaurantEntity.getRestaurantName()).photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(new BigDecimal(restaurantEntity.getCustomerRating()))
                    .averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .address(address).categories(restaurantCategories);

            restaurantListResponse.addRestaurantsItem(restaurantList);
        }

        return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
    }

    //This method returns restaurant/s by name. Its a GET Request. It takes restaurant name as a path variable
    //If the restaurant name field entered by the customer is empty, throw “RestaurantNotFoundException”
    //If there are no restaurants by the name entered by the customer, return an empty list with corresponding HTTP status
    //It returns the restaurant list as per the restaurant name search field
    //Searched restaurants are also displayed in the alphabetical order

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantListResponse> getRestaurantsByName(@PathVariable("restaurant_name") final String restaurantName)
            throws RestaurantNotFoundException {

        List<RestaurantEntity> matchedRestaurantsByNameList = restaurantService.restaurantsByName(restaurantName);

        RestaurantListResponse listResponse = new RestaurantListResponse();

        if (matchedRestaurantsByNameList.isEmpty()) {
            return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.NOT_FOUND);
        }

        for (RestaurantEntity restaurantEntity : matchedRestaurantsByNameList) {


            RestaurantDetailsResponseAddressState responseAddressState = new RestaurantDetailsResponseAddressState()
                    .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
                            stateName(restaurantEntity.getAddress().getState().getStateName());


            RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress().
                    id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
                    flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).
                    locality(restaurantEntity.getAddress().getLocality()).city(restaurantEntity.getAddress().getCity()).
                    pincode(restaurantEntity.getAddress().getPincode()).state(responseAddressState);


            String categories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
                    .stream().map(rc -> String.valueOf(rc.getCategoryName())).collect(Collectors.joining(","));


            RestaurantList restaurantList = new RestaurantList().id(UUID.fromString(restaurantEntity.getUuid())).
                    restaurantName(restaurantEntity.getRestaurantName()).photoURL(restaurantEntity.getPhotoUrl())
                    .customerRating(new BigDecimal(restaurantEntity.getCustomerRating())).
                            averagePrice(restaurantEntity.getAvgPrice()).numberCustomersRated(restaurantEntity.getNumberCustomersRated())
                    .address(responseAddress).categories(categories);


            listResponse.addRestaurantsItem(restaurantList);
        }
        return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.OK);

    }

    //This method returns restaurant list based on the input param category Id
    //If category Id field is empty CategoryNotFoundException” with the message code (CNF-001) and message (Category id field should not be empty)
    // Catogory Id is invalid categories it throws “CategoryNotFoundException” with the message code (CNF-002) and message (No category by this id)
    //If there are no restaurants under the category entered by the customer, return an empty list with corresponding HTTP status.
    //If the category id entered by the customer matches any category in the database, it should retrieve all the restaurants under this category in alphabetical order
    //Within each restaurant, the list of categories should be displayed in a categories string, in alphabetical order

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

    @CrossOrigin
    @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantDetailsResponse> getRestaurantById(@PathVariable("restaurant_id") final String restaurantId)
            throws RestaurantNotFoundException {

        RestaurantEntity restaurantByRestaurantId = restaurantService.restaurantByUUID(restaurantId);

        RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState()
                .id(UUID.fromString(restaurantByRestaurantId.getAddress().getState().getUuid())).
                        stateName(restaurantByRestaurantId.getAddress().getState().getStateName());

        RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress().
                id(UUID.fromString(restaurantByRestaurantId.getAddress().getUuid())).
                flatBuildingName(restaurantByRestaurantId.getAddress().getFlatBuilNo()).
                locality(restaurantByRestaurantId.getAddress().getLocality())
                .city(restaurantByRestaurantId.getAddress().getCity())
                .pincode(restaurantByRestaurantId.getAddress().getPincode()).state(state);

        RestaurantDetailsResponse restaurantDetailsResponse = new RestaurantDetailsResponse()
                .id(UUID.fromString(restaurantByRestaurantId.getUuid()))
                .restaurantName(restaurantByRestaurantId.getRestaurantName())
                .photoURL(restaurantByRestaurantId.getPhotoUrl())
                .customerRating(new BigDecimal(restaurantByRestaurantId.getCustomerRating()))
                .averagePrice(restaurantByRestaurantId.getAvgPrice())
                .numberCustomersRated(restaurantByRestaurantId.getNumberCustomersRated())
                .address(responseAddress);

        List<CategoryEntity> restaurantCategoryList = categoryService.getCategoriesByRestaurant(restaurantId);

        for (CategoryEntity categoryEntity : restaurantCategoryList) {
            CategoryList restaurantCategories = new CategoryList()
                    .id(UUID.fromString(categoryEntity.getUuid()))
                    .categoryName(categoryEntity.getCategoryName());

            List<ItemEntity> categoryItems = itemService.getItemsByCategoryAndRestaurant(restaurantId, categoryEntity.getUuid());

            for (ItemEntity itemEntity : categoryItems) {
                ItemList itemList = new ItemList()
                        .id(UUID.fromString(itemEntity.getUuid()))
                        .itemName(itemEntity.getItemName())
                        .price(itemEntity.getPrice())
                        .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
                restaurantCategories.addItemListItem(itemList);
            }

            restaurantDetailsResponse.addCategoriesItem(restaurantCategories);

        }

        return new ResponseEntity<RestaurantDetailsResponse>(restaurantDetailsResponse, HttpStatus.OK);
    }

    @CrossOrigin
    @RequestMapping(method = RequestMethod.PUT, path = "/restaurant/{restaurant_id}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<RestaurantUpdatedResponse> updateRestaurantDetails(
            @RequestParam(name = "customer_rating") final Double customerRating,
            @PathVariable("restaurant_id") final String restaurantId,
            @RequestHeader("authorization") final String authorization)
            throws RestaurantNotFoundException, AuthorizationFailedException, InvalidRatingException {

        // String accessToken = authorization.split("Bearer ")[1];

        BearerAuthDecoder bearerAuthDecoder = new BearerAuthDecoder(authorization);
        final String accessToken = bearerAuthDecoder.getAccessToken();
        customerService.getCustomer(accessToken);

        RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

        RestaurantEntity updatedRestaurantEntity = restaurantService.updateRestaurantRating(restaurantEntity, customerRating);

        RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
                .id(UUID.fromString(restaurantId))
                .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
        return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
    }
}





