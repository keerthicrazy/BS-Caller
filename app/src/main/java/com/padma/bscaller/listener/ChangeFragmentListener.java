package com.padma.bscaller.listener;

import androidx.fragment.app.Fragment;

/**
 * This interface is used to change the Fragments in Base Activity.
 * <p>
 * Created by Keerthivasan on 07-Mar-18.
 */
public interface ChangeFragmentListener {

    /**
     * Use this common method to add a new fragment to the current view with animation
     *
     * @param fragment                  Reference to the Fragment
     * @param stacked                   Stacked boolean value
     * @param addToBackStack            decides add to backstack or not
     */
    void addFragmentWithAnimation(Fragment fragment,
                                  Boolean stacked,
                                  Boolean addToBackStack);


    void textToVoice(String text);
}