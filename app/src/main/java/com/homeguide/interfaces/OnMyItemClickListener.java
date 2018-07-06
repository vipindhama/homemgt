package com.homeguide.interfaces;

/**
 * Created by Shweta on 28-01-2018.
 */

public interface OnMyItemClickListener {
    void onItemClicked(int position);
    void onItemLongClicked(int position);
    void onItemLongUnClicked(int position);
    void onCounterIncreased();
}
