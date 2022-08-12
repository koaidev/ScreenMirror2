package com.bangbangcoding.screenmirror.web.utils.fragment;

import androidx.annotation.AnimRes;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bangbangcoding.screenmirror.R;

public class FragmentUtils {
    public static void replaceFragmentWithAnimationDefault(FragmentManager fragmentManager, int idResRootFrame, Fragment f) {
        FragmentTransaction trans = fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        R.anim.decelerate_slide_in_right, // enter
                        R.anim.decelerate_slide_out_left_fragment, // exit
                        R.anim.decelerate_slide_in_left_fragment, // popEnter
                        R.anim.decelerate_slide_out_right // popExit
                );
        /*
         * IMPORTANT: We use the "root frame" defined in
         * "root_fragment.xml" as the reference to replace fragment
         */
        trans.add(idResRootFrame, f, f.getClass().getSimpleName());
        /*
         * IMPORTANT: The following lines allow us to add the fragment
         * to the stack and return to it later, by pressing back
         */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commitAllowingStateLoss();
    }

    public static void replaceFragmentWithAnimation(FragmentManager fragmentManager, int idResRootFrame,
                                                    @AnimRes int animEnter, @AnimRes int animExist,
                                                    @AnimRes int animPopEnter,
                                                    @AnimRes int animPopExist,
                                                    Fragment f) {
        FragmentTransaction trans = fragmentManager
                .beginTransaction()
                .setCustomAnimations(
                        animEnter,  // enter
                        animExist,  // exit
                        animPopEnter,   // popEnter
                        animPopExist  // popExit
                );
        /*
         * IMPORTANT: We use the "root frame" defined in
         * "root_fragment.xml" as the reference to replace fragment
         */
        trans.replace(idResRootFrame, f);
        /*
         * IMPORTANT: The following lines allow us to add the fragment
         * to the stack and return to it later, by pressing back
         */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }

    public static void replaceFragment(FragmentManager fragmentManager, int idResRootFrame, Fragment f) {
        FragmentTransaction trans = fragmentManager
                .beginTransaction();
        /*
         * IMPORTANT: We use the "root frame" defined in
         * "root_fragment.xml" as the reference to replace fragment
         */
        trans.replace(idResRootFrame, f, f.getClass().getSimpleName());
        /*
         * IMPORTANT: The following lines allow us to add the fragment
         * to the stack and return to it later, by pressing back
         */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }

    public static void addFragment(FragmentManager fragmentManager, int idResRootFrame, Fragment f) {
        FragmentTransaction trans = fragmentManager
                .beginTransaction();
        /*
         * IMPORTANT: We use the "root frame" defined in
         * "root_fragment.xml" as the reference to replace fragment
         */
        trans.add(idResRootFrame, f, f.getClass().getSimpleName());
        /*
         * IMPORTANT: The following lines allow us to add the fragment
         * to the stack and return to it later, by pressing back
         */
        trans.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        trans.addToBackStack(null);
        trans.commit();
    }
}
