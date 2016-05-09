package com.epicodus.myrestaurants.util;

/**
 * Created by abigailrolling on 5/8/16.
 */
public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
