package com.maroufb.beastchat.views;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.maroufb.beastchat.fragments.FindFriendsFragment;
import com.maroufb.beastchat.fragments.FriendRequestFragment;
import com.maroufb.beastchat.fragments.UserFriendsFragment;

public class FriendsViewPagerAdapter extends FragmentStatePagerAdapter {
    public FriendsViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
       Fragment returnFragment;

       switch (position){
           case 0:
               returnFragment = UserFriendsFragment.newInstance();
               break;
           case 1:
               returnFragment = FriendRequestFragment.newInstance();
               break;
           case 2:
               returnFragment = FindFriendsFragment.newInstance();
               break;
           default:
                   return null;
       }
       return returnFragment;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        CharSequence title;
        switch (position){
            case 0:
                title = "Friends";
                break;
            case 1:
                title = "Requests";
                break;
            case 2:
                title = "Find Friends";
                break;
            default:
                return null;


        }
        return title;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
