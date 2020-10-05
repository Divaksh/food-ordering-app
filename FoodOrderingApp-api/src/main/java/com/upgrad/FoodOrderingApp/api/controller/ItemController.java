package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.ItemList;
import com.upgrad.FoodOrderingApp.api.model.ItemListResponse;
import com.upgrad.FoodOrderingApp.service.businness.ItemService;
import com.upgrad.FoodOrderingApp.service.businness.RestaurantService;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import com.upgrad.FoodOrderingApp.service.exception.RestaurantNotFoundException;
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
public class ItemController {

  @Autowired
  private ItemService itemService;

  @Autowired
  private RestaurantService restaurantService;

  //This end point returns top 5 items by populatity of a restaurant
  //It takes restaurant Id as input param..and genetates ResponseEntity<ItemListResponse> type object along with HttpStatus

  @CrossOrigin
  @RequestMapping(method = RequestMethod.GET, path = "/item/restaurant/{restaurant_id}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
  public ResponseEntity<ItemListResponse> getItemsByPopularity(
      @PathVariable("restaurant_id") final String restaurantId) throws RestaurantNotFoundException {

    //Fetching the restaurant info. if restaurant Id entered is valid
    RestaurantEntity restaurantEntity = restaurantService.restaurantByUUID(restaurantId);

    //Fetching all items of restaurant in order of popularity
    List<ItemEntity> itemList = itemService.getItemsByPopularity(restaurantEntity);

    ItemListResponse itemListResponse = new ItemListResponse();

    //Considering only top 5 items..
    int count = 0;
    for (ItemEntity itemEntity : itemList) {
      if (count < 5) {
        ItemList items = new ItemList()
            .id(UUID.fromString(itemEntity.getUuid()))
            .itemName(itemEntity.getItemName())
            .price(itemEntity.getPrice())
            .itemType(ItemList.ItemTypeEnum.fromValue(itemEntity.getType().getValue()));
        itemListResponse.add(items);
        count = count + 1;
      } else {
        break;
      }
    }

    //Sending response with the desired body and required status code.

    return new ResponseEntity<ItemListResponse>(itemListResponse, HttpStatus.OK);

  }

}
