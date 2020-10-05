package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.CategoriesListResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryDetailsResponse;
import com.upgrad.FoodOrderingApp.api.model.CategoryListResponse;
import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.service.businness.CategoryService;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.CategoryNotFoundException;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class CategoryController {

  @Autowired
  private CategoryService categoryService;


  /**
   * This api endpoint retrieves all the categories present in the database, ordered by their name
   * and display the response
   *
   * @return ResponseEntity<CategoriesListResponse> type object along with HttpStatus OK
   */
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/category", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<CategoriesListResponse> getAllCategories() {

    //get all categories ordered by their names
    List<CategoryEntity> categoryList = categoryService.getAllCategoriesOrderedByName();

    CategoriesListResponse categoriesListResponse = new CategoriesListResponse();

    for (CategoryEntity categoryEntity : categoryList) {
      CategoryListResponse categoryListResponse = new CategoryListResponse()
          .id(UUID.fromString(categoryEntity.getUuid()))
          .categoryName(categoryEntity.getCategoryName());

      categoriesListResponse.addCategoriesItem(categoryListResponse);
    }
    return new ResponseEntity<CategoriesListResponse>(categoriesListResponse, HttpStatus.OK);
  }

  /**
   * This api endpoint is used to retrieve category for given id with all items within that
   * category
   *
   * @param categoryId ID of category
   * @return ResponseEntity<CategoryDetailsResponse> type object along with HttpStatus OK
   * @throws CategoryNotFoundException in cases where category Id is empty and there are no
   *                                   categories available by the id provided
   */
  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/category/{category_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<CategoryDetailsResponse> getCategoryById(
      @PathVariable("category_id") final String categoryId) throws CategoryNotFoundException {

    // get all categories ordered by name
    CategoryEntity categoryEntity = categoryService.getCategoryById(categoryId);

    // create response
    CategoryDetailsResponse categoryDetailsResponse = new CategoryDetailsResponse()
        .id(UUID.fromString(categoryEntity.getUuid()))
        .categoryName(categoryEntity.getCategoryName());

    for (ItemEntity itemEntity : categoryEntity.getItems()) {

      ItemList itemList = new ItemList()
          .id(UUID.fromString(itemEntity.getUuid()))
          .itemName(itemEntity.getItemName())
          .price(itemEntity.getPrice())
          .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
      categoryDetailsResponse.addItemListItem(itemList);
    }
    return new ResponseEntity<CategoryDetailsResponse>(categoryDetailsResponse, HttpStatus.OK);
  }
}
