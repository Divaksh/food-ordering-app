package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryService {

  @Autowired
  private CategoryDao categoryDao;

  @Autowired
  private RestaurantDao restaurantDao;

  /**
   * Returns all categories for a given restaurant
   *
   * @param RestaurantUuid UUID of restaurant entity
   * @return List<CategoryEntity> object
   */
  public List<CategoryEntity> getCategoriesByRestaurant(String RestaurantUuid) {
    RestaurantEntity restaurantEntity = restaurantDao.restaurantByUUID(RestaurantUuid);
    return restaurantEntity.getCategories().stream()
        .sorted(Comparator.comparing(CategoryEntity::getCategoryName)).collect(Collectors.toList());
  }


  /**
   * This method implements the business logic for 'category' endpoint
   *
   * @return List<CategoryEntity> object
   */
  public List<CategoryEntity> getAllCategoriesOrderedByName() {
    return categoryDao.getAllCategories().stream()
        .sorted(Comparator.comparing(CategoryEntity::getCategoryName))
        .collect(Collectors.toList());
  }


  /**
   * This method implements the business logic for 'getCategoryById' endpoint
   *
   * @param categoryId UUID of category
   * @return CategoryEntity object
   * @throws CategoryNotFoundException If category is not found for given UUID
   */
  public CategoryEntity getCategoryById(String categoryId) throws CategoryNotFoundException {

    if (categoryId.equals("")) {
      throw new CategoryNotFoundException("CNF-001", "Category id field should not be empty");
    }

    CategoryEntity categoryEntity = categoryDao.getCategoryByUuid(categoryId);

    if (categoryEntity == null) {
      throw new CategoryNotFoundException("CNF-002", "No category by this id");
    }
    return categoryEntity;
  }

}
