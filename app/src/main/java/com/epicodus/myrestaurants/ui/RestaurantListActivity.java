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


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.epicodus.myrestaurants.Constants;
import com.epicodus.myrestaurants.R;
import com.epicodus.myrestaurants.models.Restaurant;
import com.epicodus.myrestaurants.util.OnRestaurantSelectedListener;

import org.parceler.Parcels;

import java.util.ArrayList;

public class RestaurantListActivity extends AppCompatActivity implements OnRestaurantSelectedListener{
    private Integer mPosition;
    ArrayList<Restaurant> mRestaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null) {
            if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
                mPosition = savedInstanceState.getInt(Constants.EXTRA_KEY_POSITION);
                mRestaurants = Parcels.unwrap(savedInstanceState.getParcelable(Constants.EXTRA_KEY_RESTAURANTS));
                if (mPosition != null && mRestaurants != null) {
                    Intent intent = new Intent(this, RestaurantDetailActivity.class);
                    intent.putExtra(Constants.EXTRA_KEY_POSITION, mPosition);
                    intent.putExtra(Constants.EXTRA_KEY_RESTAURANTS, Parcels.wrap(mRestaurants));
                    startActivity(intent);
                }
            }
        }
        setContentView(R.layout.activity_restaurants);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState){
        if(mPosition != null && mRestaurants != null){
            outState.putInt(Constants.EXTRA_KEY_POSITION, mPosition);
            outState.putParcelable(Constants.EXTRA_KEY_RESTAURANTS, Parcels.wrap(mRestaurants));
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onRestaurantSelected(Integer position, ArrayList<Restaurant> restaurants){
        mPosition = position;
        mRestaurants = restaurants;
    }

}
