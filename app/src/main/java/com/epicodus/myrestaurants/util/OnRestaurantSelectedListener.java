package com.epicodus.myrestaurants.util;

import com.epicodus.myrestaurants.models.Restaurant;

import java.util.ArrayList;

/**
 * Created by Guest on 5/11/16.
 */
public interface OnRestaurantSelectedListener {
    public void onRestaurantSelected(Integer position, ArrayList<Restaurant> restaurants);
}
