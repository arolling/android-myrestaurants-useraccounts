package com.epicodus.myrestaurants.ui;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.epicodus.myrestaurants.Constants;
import com.epicodus.myrestaurants.R;
import com.epicodus.myrestaurants.models.Restaurant;
import com.firebase.client.Firebase;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RestaurantDetailFragment extends BaseFragment implements View.OnClickListener {
    private static final int MAX_WIDTH= 400;
    private static final int MAX_HEIGHT = 300;
    private static final int REQUEST_IMAGE_CAPTURE = 111;
    private SharedPreferences mSharedPreferences;
    private String mSource;
    @Bind(R.id.restaurantImageView) ImageView mImageLabel;
    @Bind(R.id.restaurantNameTextView) TextView mNameLabel;
    @Bind(R.id.cuisineTextView) TextView mCategoriesLabel;
    @Bind(R.id.ratingTextView) TextView mRatingLabel;
    @Bind(R.id.websiteTextView) TextView mWebsiteLabel;
    @Bind(R.id.phoneTextView) TextView mPhoneLabel;
    @Bind(R.id.addressTextView) TextView mAddressLabel;
    @Bind(R.id.saveRestaurantButton) TextView mSaveRestaurantButton;

    private Restaurant mRestaurant;
    private ArrayList<Restaurant> mRestaurants;
    private Integer mPosition;

    public static RestaurantDetailFragment newInstance(ArrayList<Restaurant> restaurants, Integer position, String source) {
        RestaurantDetailFragment restaurantDetailFragment = new RestaurantDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable(Constants.EXTRA_KEY_RESTAURANTS, Parcels.wrap(restaurants));
        args.putInt(Constants.EXTRA_KEY_POSITION, position);
        args.putString(Constants.KEY_SOURCE, source);
        restaurantDetailFragment.setArguments(args);
        return restaurantDetailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRestaurants = Parcels.unwrap(getArguments().getParcelable(Constants.EXTRA_KEY_RESTAURANTS));
        mPosition = getArguments().getInt(Constants.EXTRA_KEY_POSITION);
        mRestaurant = mRestaurants.get(mPosition);
        mSource = getArguments().getString(Constants.KEY_SOURCE);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_restaurant_detail, container, false);
        ButterKnife.bind(this, view);

        if (mSource.equals(Constants.SOURCE_SAVED)) {
            mSaveRestaurantButton.setVisibility(View.GONE);
        } else {
            mSaveRestaurantButton.setOnClickListener(this);
        }

        if (!mRestaurant.getImageUrl().contains("http")) {
            try {
                Bitmap image = decodeFromFirebaseBase64(mRestaurant.getImageUrl());
                mImageLabel.setImageBitmap(image);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Picasso.with(view.getContext())
                    .load(mRestaurant.getImageUrl())
                    .resize(MAX_WIDTH, MAX_HEIGHT)
                    .centerCrop()
                    .into(mImageLabel);
        }
        mNameLabel.setText(mRestaurant.getName());
        mCategoriesLabel.setText(android.text.TextUtils.join(", ", mRestaurant.getCategories()));
        mRatingLabel.setText(Double.toString(mRestaurant.getRating()) + "/5");
        mPhoneLabel.setText(mRestaurant.getPhone());
        mAddressLabel.setText(android.text.TextUtils.join(", ", mRestaurant.getAddress()));
        mWebsiteLabel.setOnClickListener(this);
        mPhoneLabel.setOnClickListener(this);
        mAddressLabel.setOnClickListener(this);
        return view;
    }

    public static Bitmap decodeFromFirebaseBase64(String image) throws IOException {
        byte[] decodedByte = com.firebase.client.utilities.Base64.decode(image);
        return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        if (mSource.equals(Constants.SOURCE_SAVED)) {
            inflater.inflate(R.menu.menu_photo, menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                return true;
            case R.id.action_photo:
                onLaunchCamera();
            default:
                break;
        }
        return false;
    }

    public void onLaunchCamera() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == getActivity().RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            mImageLabel.setImageBitmap(imageBitmap);
            encodeBitmapAndSaveToFirebase(imageBitmap);
        }
    }

    public void encodeBitmapAndSaveToFirebase(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = com.firebase.client.utilities.Base64.encodeBytes(b);
        Firebase restaurantRef = new Firebase(Constants.FIREBASE_URL_RESTAURANTS)
                .child(mUid)
                .child(mRestaurant.getPushId())
                .child("imageUrl");
        restaurantRef.setValue(imageEncoded);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.saveRestaurantButton:
                String userUid = mSharedPreferences.getString(Constants.KEY_UID, null);
                Firebase userRestaurantsFirebaseRef = new Firebase(Constants.FIREBASE_URL_RESTAURANTS).child(userUid);
                Firebase pushRef = userRestaurantsFirebaseRef.push();
                String restaurantPushId = pushRef.getKey();
                mRestaurant.setPushId(restaurantPushId);
                pushRef.setValue(mRestaurant);
                Toast.makeText(getContext(), "Restaurant Saved!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.addressTextView:
                Intent mapIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("geo:" + mRestaurant.getLatitude()
                                + "," + mRestaurant.getLongitude()
                                + "?q=(" + mRestaurant.getName() + ")"));
                startActivity(mapIntent);
                break;
            case R.id.phoneTextView:
                Intent phoneIntent = new Intent(Intent.ACTION_DIAL,
                        Uri.parse("tel:" + mRestaurant.getPhone()));
                startActivity(phoneIntent);
                break;
            case R.id.websiteTextView:
                Intent webIntent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse(mRestaurant.getWebsite()));
                startActivity(webIntent);
                break;
            default:
                break;

        }
    }

}