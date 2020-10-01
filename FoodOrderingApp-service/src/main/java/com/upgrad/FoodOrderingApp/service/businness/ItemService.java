package com.upgrad.FoodOrderingApp.service.businness;


import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ItemService {

  @Autowired
  private RestaurantDao restaurantDao;

  @Autowired
  private CategoryDao categoryDao;

  @Autowired
  private OrderItemDao orderItemDao;

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

  public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity) {
    return orderItemDao.getItemsByOrder(orderEntity);
  }


}
