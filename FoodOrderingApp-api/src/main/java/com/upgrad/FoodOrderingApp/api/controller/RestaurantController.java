package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoryList;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddress;
import com.upgrad.FoodOrderingApp.api.model.RestaurantDetailsResponseAddressState;
import com.upgrad.FoodOrderingApp.api.model.RestaurantList;
import com.upgrad.FoodOrderingApp.api.model.RestaurantListResponse;
import com.upgrad.FoodOrderingApp.api.model.RestaurantUpdatedResponse;
import com.upgrad.FoodOrderingApp.api.provider.BearerAuthDecoder;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.businness.CustomerService;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthorizationFailedException;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

  /**
   * This API endpoint gets list of all restaurant in order of their ratings
   *
   * @return all the restaurants in order of their ratings
   */
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
          locality(restaurantEntity.getAddress().getLocality())
          .city(restaurantEntity.getAddress().getCity()).
              pincode(restaurantEntity.getAddress().getPincode()).state(addressState);

      String restaurantCategories = categoryService
          .getCategoriesByRestaurant(restaurantEntity.getUuid())
          .stream().map(rc -> String.valueOf(rc.getCategoryName()))
          .collect(Collectors.joining(","));

      RestaurantList restaurantList = new RestaurantList()
          .id(UUID.fromString(restaurantEntity.getUuid())).
              restaurantName(restaurantEntity.getRestaurantName())
          .photoURL(restaurantEntity.getPhotoUrl())
          .customerRating(new BigDecimal(restaurantEntity.getCustomerRating()))
          .averagePrice(restaurantEntity.getAvgPrice())
          .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
          .address(address).categories(restaurantCategories);

      restaurantListResponse.addRestaurantsItem(restaurantList);
    }
    return new ResponseEntity<RestaurantListResponse>(restaurantListResponse, HttpStatus.OK);
  }

  /**
   * This API endpoint gets list of all restaurant found for given search string
   *
   * @param restaurantName Name of the restaurant that one would like to search
   * @return RestaurantListResponse restaurant/s by name.
   * @throws RestaurantNotFoundException If the restaurant doesn't exist in database.
   */
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/restaurant/name/{restaurant_name}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantListResponse> getRestaurantsByName(
      @PathVariable("restaurant_name") final String restaurantName)
      throws RestaurantNotFoundException {

    List<RestaurantEntity> matchedRestaurantsByNameList = restaurantService
        .restaurantsByName(restaurantName);

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
          locality(restaurantEntity.getAddress().getLocality())
          .city(restaurantEntity.getAddress().getCity()).
              pincode(restaurantEntity.getAddress().getPincode()).state(responseAddressState);

      String categories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
          .stream().map(rc -> String.valueOf(rc.getCategoryName()))
          .collect(Collectors.joining(","));

      RestaurantList restaurantList = new RestaurantList()
          .id(UUID.fromString(restaurantEntity.getUuid())).
              restaurantName(restaurantEntity.getRestaurantName())
          .photoURL(restaurantEntity.getPhotoUrl())
          .customerRating(new BigDecimal(restaurantEntity.getCustomerRating())).
              averagePrice(restaurantEntity.getAvgPrice())
          .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
          .address(responseAddress).categories(categories);

      listResponse.addRestaurantsItem(restaurantList);
    }
    return new ResponseEntity<RestaurantListResponse>(listResponse, HttpStatus.OK);
  }


  /**
   * This API endpoint gets list of all restaurant found for given category id
   *
   * @param categoryId of the category
   * @return RestaurantListResponse returns restaurant list based on the input param category Id
   * @throws CategoryNotFoundException if the category with the given id is not found in the
   *                                   database with the message code (CNF-001) and message
   *                                   (Category id field should not be empty).
   */
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/restaurant/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantListResponse> getRestaurantsByCategoryId(
      @PathVariable("category_id") final String categoryId)
      throws CategoryNotFoundException {

    List<RestaurantEntity> restaurantListByCategoryId = restaurantService
        .restaurantByCategory(categoryId);

    RestaurantListResponse restaurantResponseByCategoryId = new RestaurantListResponse();

    if (restaurantListByCategoryId.isEmpty()) {
      return new ResponseEntity<RestaurantListResponse>(restaurantResponseByCategoryId,
          HttpStatus.NOT_FOUND);
    }

    for (RestaurantEntity restaurantEntity : restaurantListByCategoryId) {
      RestaurantDetailsResponseAddressState state = new RestaurantDetailsResponseAddressState()
          .id(UUID.fromString(restaurantEntity.getAddress().getState().getUuid())).
              stateName(restaurantEntity.getAddress().getState().getStateName());

      RestaurantDetailsResponseAddress responseAddress = new RestaurantDetailsResponseAddress().
          id(UUID.fromString(restaurantEntity.getAddress().getUuid())).
          flatBuildingName(restaurantEntity.getAddress().getFlatBuilNo()).
          locality(restaurantEntity.getAddress().getLocality())
          .city(restaurantEntity.getAddress().getCity()).
              pincode(restaurantEntity.getAddress().getPincode()).state(state);

      String categories = categoryService.getCategoriesByRestaurant(restaurantEntity.getUuid())
          .stream().map(rc -> String.valueOf(rc.getCategoryName()))
          .collect(Collectors.joining(","));

      RestaurantList restaurantsByCategory = new RestaurantList()
          .id(UUID.fromString(restaurantEntity.getUuid())).
              restaurantName(restaurantEntity.getRestaurantName())
          .photoURL(restaurantEntity.getPhotoUrl())
          .customerRating(new BigDecimal(restaurantEntity.getCustomerRating()))
          .averagePrice(restaurantEntity.getAvgPrice())
          .numberCustomersRated(restaurantEntity.getNumberCustomersRated())
          .address(responseAddress).categories(categories);

      restaurantResponseByCategoryId.addRestaurantsItem(restaurantsByCategory);
    }
    return new ResponseEntity<RestaurantListResponse>(restaurantResponseByCategoryId,
        HttpStatus.OK);
  }

  /**
   * This API endpoint gets restaurant for given restaurant id
   *
   * @param restaurantId ID of the restaurant whose details are requested
   * @return RestaurantDetailsResponse
   * @throws RestaurantNotFoundException if the restaurant with the ID is not found in the
   *                                     database.
   */
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<RestaurantDetailsResponse> getRestaurantById(
      @PathVariable("restaurant_id") final String restaurantId)
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

    List<CategoryEntity> restaurantCategoryList = categoryService
        .getCategoriesByRestaurant(restaurantId);

    for (CategoryEntity categoryEntity : restaurantCategoryList) {
      CategoryList restaurantCategories = new CategoryList()
          .id(UUID.fromString(categoryEntity.getUuid()))
          .categoryName(categoryEntity.getCategoryName());

      List<ItemEntity> categoryItems = itemService
          .getItemsByCategoryAndRestaurant(restaurantId, categoryEntity.getUuid());

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

  /**
   * This API endpoint updates the restaurant rating by customer
   *
   * @param authorization  Bearer <access-token>
   * @param restaurantId   id of the restaurant whose rating is to be updated.
   * @param customerRating Actual rating value that is to be updated.
   * @return
   * @throws AuthorizationFailedException if the given token is not valid.
   * @throws RestaurantNotFoundException  if the restaurant with the given uuid doesn't exist in
   *                                      database.
   * @throws InvalidRatingException       if the rating is less than 1 or grater than 5.
   */
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

    RestaurantEntity updatedRestaurantEntity = restaurantService
        .updateRestaurantRating(restaurantEntity, customerRating);

    RestaurantUpdatedResponse restaurantUpdatedResponse = new RestaurantUpdatedResponse()
        .id(UUID.fromString(restaurantId))
        .status("RESTAURANT RATING UPDATED SUCCESSFULLY");
    return new ResponseEntity<RestaurantUpdatedResponse>(restaurantUpdatedResponse, HttpStatus.OK);
  }
}





