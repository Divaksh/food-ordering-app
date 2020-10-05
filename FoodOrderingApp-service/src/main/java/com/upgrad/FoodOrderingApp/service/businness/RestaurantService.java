package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import com.upgrad.FoodOrderingApp.service.exception.InvalidRatingException;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RestaurantService {

  @Autowired
  private RestaurantDao restaurantDao;

  @Autowired
  private CategoryDao categoryDao;

  /**
   * Gets all the restaurants in DB.
   *
   * @return List of all the restaurants according to the customer ratings
   */
  public List<RestaurantEntity> restaurantsByRating() {
    List<RestaurantEntity> restaurantEntities = restaurantDao.getAllRestaurantsByRating();
    return restaurantEntities;
  }

  /**
   * Gets restaurants in DB based on search string.
   *
   * @return List of the restaurants even if there is partial match in the restaurant in DB and the
   * resto. mentioned in search field
   */
  public List<RestaurantEntity> restaurantsByName(final String restaurantName)
      throws RestaurantNotFoundException {
    if (restaurantName.isEmpty()) {
      throw new RestaurantNotFoundException("RNF-003", "Restaurant name field should not be empty");
    }

    List<RestaurantEntity> restaurantListByRating = restaurantDao.getAllRestaurantsByRating();
    List<RestaurantEntity> matchingRestaurantList = new ArrayList<>();

    //matching restaurants with the restaurant name mentioned in the search field and if matched populating the resto. in the matched resto. list
    for (RestaurantEntity restaurantEntity : restaurantListByRating) {
      if (restaurantEntity.getRestaurantName().toLowerCase()
          .contains(restaurantName.toLowerCase())) {
        matchingRestaurantList.add(restaurantEntity);
      }
    }

    return matchingRestaurantList;
  }

  /**
   * Gets all the restaurants in DB based on Category Uuid
   *
   * @return List of all the restaurants based on the input category Id
   */
  public List<RestaurantEntity> restaurantByCategory(final String categoryId)
      throws CategoryNotFoundException {

    if (categoryId.equals("")) {
      throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
    }

    CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

    if (categoryEntity == null) {
      throw new CategoryNotFoundException("CNF-002", "No Category By this id");
    }

    List<RestaurantEntity> restaurantListByCategoryId = categoryEntity.getRestaurants();
    restaurantListByCategoryId.sort(Comparator.comparing(RestaurantEntity::getRestaurantName));
    return restaurantListByCategoryId;
  }

  /**
   * Updates the customer rating for a restaurant
   *
   * @param restaurantEntity Restaurant whose rating is to be done, customerRating as provided by
   *                         customer
   * @return RestaurantEntity the updated restaurant details
   */
  @Transactional(propagation = Propagation.REQUIRED)
  public RestaurantEntity updateRestaurantRating(RestaurantEntity restaurantEntity,
      Double newRating)
      throws InvalidRatingException {
    if (newRating < 1.0 || newRating > 5.0) {
      throw new InvalidRatingException("IRE-001", "Restaurant should be in the range of 1 to 5");
    }

    Double newAvgRating =
        ((restaurantEntity.getCustomerRating().doubleValue()) *
            ((double) restaurantEntity.getNumberCustomersRated()) + newRating) /
            ((double) restaurantEntity.getNumberCustomersRated() + 1);

    restaurantEntity.setCustomerRating(newAvgRating);
    restaurantEntity.setNumberCustomersRated(restaurantEntity.getNumberCustomersRated() + 1);

    return restaurantDao.updateRestaurantEntity(restaurantEntity);

  }

  /**
   * This method gets the restaurant details.
   *
   * @param restaurantId UUID of the restaurant.
   * @return the restaurant based on input restaurant ID
   * @throws RestaurantNotFoundException if restaurant with UUID doesn't exist in the database.
   */
  public RestaurantEntity restaurantByUUID(String restaurantId) throws RestaurantNotFoundException {
    if (restaurantId.equals("")) {
      throw new RestaurantNotFoundException("RNF-002", "Restaurant id field should not be empty");
    }

    RestaurantEntity restaurantByRestaurantId = restaurantDao.restaurantByUUID(restaurantId);

    if (restaurantByRestaurantId == null) {
      throw new RestaurantNotFoundException("RNF-001", "No Restaurant By this Id");
    }

    return restaurantByRestaurantId;
  }

}
