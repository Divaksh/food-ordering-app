package com.upgrad.FoodOrderingApp.service.dao;

import com.upgrad.FoodOrderingApp.service.entity.RestaurantEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class RestaurantDao {

    @PersistenceContext
    private EntityManager entityManager;



    public RestaurantEntity getRestaurantById(String restaurantId) {
        try {
            RestaurantEntity restaurantEntity = entityManager.createNamedQuery("restaurantById", RestaurantEntity.class)
                    .setParameter("restaurantId", restaurantId)
                    .getSingleResult();
            return restaurantEntity;
        }catch (NoResultException nre){
            return null;
        }
    }

    //This method queries DB to get all the restaurants from DB
    //Restaurant List is returned as per the restaurant ratings

    public List<RestaurantEntity> getAllRestaurantsByRating(){
        List<RestaurantEntity> restaurantEntities = entityManager.createNamedQuery("getAllRestaurantsByRating", RestaurantEntity.class).getResultList();
        return restaurantEntities;
    }

    //This method queries DB to get the single restaurant from DB
    //Restaurant details are returned as per the input parameter restaurant uuid

    public RestaurantEntity restaurantByUUID(String uuid){
        try {
            return entityManager.createNamedQuery("restaurantByUUID", RestaurantEntity.class).setParameter("uuid", uuid).getSingleResult();
        } catch (NoResultException nre){
            return null;
        }
    }

    public RestaurantEntity updateRestaurantEntity(RestaurantEntity restaurantEntity){
        entityManager.merge(restaurantEntity);
        return  restaurantEntity;
    }

}
