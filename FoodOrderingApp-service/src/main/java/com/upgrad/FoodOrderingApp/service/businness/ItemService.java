package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.OrderItemDao;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderEntity;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

import com.upgrad.FoodOrderingApp.service.dao.CategoryDao;
import com.upgrad.FoodOrderingApp.service.dao.ItemDao;
import com.upgrad.FoodOrderingApp.service.dao.RestaurantDao;
import com.upgrad.FoodOrderingApp.service.entity.CategoryEntity;
import com.upgrad.FoodOrderingApp.service.entity.ItemEntity;
import com.upgrad.FoodOrderingApp.service.dao.*;
import com.upgrad.FoodOrderingApp.service.entity.OrderItemEntity;
import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import java.util.*;


@Service
public class ItemService {

    @Autowired
    private ItemDao itemDao;

    @Autowired
    private OrderItemDao orderItemDao;

    @Autowired
    private OrderDao orderDao;

    @Autowired
    private RestaurantDao restaurantDao;

    @Autowired
    private CategoryDao categoryDao;

    //Method called to get the ItemEntity Instance by passing the UUID


    public ItemEntity getItemByUUID(String itemId) throws ItemNotFoundException {
        ItemEntity itemEntity = itemDao.getItemById(itemId);
        if(itemEntity == null){
            throw new ItemNotFoundException("INF-003", "No item by this id exist");
        }else {
            return itemEntity;
        }
    }

    public List<OrderItemEntity> getItemsByOrder(OrderEntity orderEntity) {
        return orderItemDao.getItemsByOrder(orderEntity);
    }


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

    //This method fetches and returns Items list (All items) based on popularity of a given restaurant

    public List<ItemEntity> getItemsByPopularity(RestaurantEntity restaurantEntity) {
        List<ItemEntity> itemEntityList = new ArrayList<ItemEntity>();
        for (OrderEntity orderEntity : orderDao.getOrdersByRestaurant(restaurantEntity)) {
            for (OrderItemEntity orderItemEntity : orderItemDao.getItemsByOrder(orderEntity)) {
                itemEntityList.add(orderItemEntity.getItemId());
            }
        }

        // counting all items with map
        Map<String, Integer> map = new HashMap<String, Integer>();
        for (ItemEntity itemEntity : itemEntityList) {
            Integer count = map.get(itemEntity.getUuid());
            map.put(itemEntity.getUuid(), (count == null) ? 1 : count + 1);
        }

        Map<String, Integer> map1 = new TreeMap<String, Integer>(map);
        List<ItemEntity> sortedItemEntityList = new ArrayList<ItemEntity>();
        for (Map.Entry<String, Integer> entry : map1.entrySet()) {
            sortedItemEntityList.add(itemDao.getItemByUUID(entry.getKey()));
        }
        Collections.reverse(sortedItemEntityList);

        return sortedItemEntityList;
    }
}
