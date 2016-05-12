package com.epicodus.myrestaurants.ui;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.epicodus.myrestaurants.Constants;
import com.epicodus.myrestaurants.R;
import com.epicodus.myrestaurants.adapters.RestaurantListAdapter;
import com.epicodus.myrestaurants.models.Restaurant;
import com.epicodus.myrestaurants.services.YelpService;
import com.epicodus.myrestaurants.util.EndlessRecyclerViewScrollListener;
import com.epicodus.myrestaurants.util.OnRestaurantSelectedListener;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class RestaurantListFragment extends BaseFragment {
    @Bind(R.id.recyclerView) RecyclerView mRecyclerView;
    private RestaurantListAdapter mAdapter;
    public ArrayList<Restaurant> mRestaurants = new ArrayList<>();
    OnRestaurantSelectedListener mOnRestaurantSelectedListener;
    private String mLocation;


    public RestaurantListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnRestaurantSelectedListener = (OnRestaurantSelectedListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + e.getMessage());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_list, container, false);
        ButterKnife.bind(this, view);
        mLocation = mSharedPreferences.getString(Constants.PREFERENCES_LOCATION_KEY, null);
        if (mLocation != null) {
            getRestaurants(mLocation);
        }
        return view;
    }

    public void getRestaurants(String location) {
        final YelpService yelpService = new YelpService();

        yelpService.findRestaurants(location, 0, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) {
                mRestaurants = yelpService.processResults(response);

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter = new RestaurantListAdapter(mRestaurants, mOnRestaurantSelectedListener);

                        mRecyclerView.setAdapter(mAdapter);
                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(layoutManager);
                        mRecyclerView.addOnScrollListener(new EndlessRecyclerViewScrollListener(layoutManager) {
                            @Override
                            public void onLoadMore(int page, int totalItemsCount) {
                                loadMoreFromYelp(totalItemsCount);
                            }
                        });
                    }
                });
            }
        });
    }

    public void loadMoreFromYelp(int offset){
        final YelpService yelpService = new YelpService();
        yelpService.findRestaurants(mLocation, offset, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                mRestaurants.addAll(yelpService.processResults(response));

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int currentSize = mAdapter.getItemCount();
                        mAdapter.notifyItemRangeInserted(currentSize, mRestaurants.size() - 1);
                    }
                });

            }
        });


    }

}
