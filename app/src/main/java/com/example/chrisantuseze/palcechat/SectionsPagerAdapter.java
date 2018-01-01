package com.example.chrisantuseze.palcechat;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.chrisantuseze.palcechat.Fragments.ChatsFragment;
import com.example.chrisantuseze.palcechat.Fragments.FriendsFragment;
import com.example.chrisantuseze.palcechat.Fragments.GroupsFragment;

/**
 * Created by CHRISANTUS EZE on 23/11/2017.
 */

class SectionsPagerAdapter extends FragmentPagerAdapter{
    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch(position){

            case 0:
                return new ChatsFragment();
            case 1:
                return new FriendsFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
    public CharSequence getPageTitle(int position){
        switch (position){
            case 0: return "CHATS";
            case 1: return "FRIENDS";

            default: return null;
        }
    }

}
