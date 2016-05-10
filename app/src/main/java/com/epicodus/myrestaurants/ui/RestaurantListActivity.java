//
//package com.epicodus.myrestaurants.ui;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.v7.app.AppCompatActivity;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.util.Log;
//import android.widget.ArrayAdapter;
//import android.widget.ListView;
//
//import com.epicodus.myrestaurants.R;
//import com.epicodus.myrestaurants.adapters.RestaurantListAdapter;
//import com.epicodus.myrestaurants.models.Restaurant;
//import com.epicodus.myrestaurants.services.YelpService;
//
//import java.io.IOException;
//import java.util.ArrayList;
//
//import butterknife.Bind;
//import butterknife.ButterKnife;
//import okhttp3.Call;
//import okhttp3.Response;
//import okhttp3.Callback;
//
//public class RestaurantListActivity extends AppCompatActivity {
//    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
//    private RestaurantListAdapter mAdapter;
//    public ArrayList<Restaurant> mRestaurants = new ArrayList<>();
//    public static final String TAG = RestaurantListActivity.class.getSimpleName();
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_restaurants);
//        ButterKnife.bind(this);
//
//        Intent intent = getIntent();
//        String location = intent.getStringExtra("location");
//
//        getRestaurants(location);
//    }
//
//    private void getRestaurants(String location) {
//        YelpService.findRestaurants(location, new Callback() {
//            @Override
//            public void onFailure(Call call, IOException e) {
//                e.printStackTrace();
//            }
//
//            @Override
//            public void onResponse(Call call, Response response) throws IOException {
//                try {
//                    String jsonData = response.body().string();
//                    if (response.isSuccessful()) {
//                        Log.v("RESPONSE", jsonData);
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
//    }
//}



package com.epicodus.myrestaurants.ui;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.epicodus.myrestaurants.R;

public class RestaurantListActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurants);
    }

}
