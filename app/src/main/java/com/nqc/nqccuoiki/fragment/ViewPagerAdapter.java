package com.nqc.nqccuoiki.fragment;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.nqc.nqccuoiki.Song;

import java.util.ArrayList;

public class ViewPagerAdapter extends FragmentStatePagerAdapter {
    ArrayList<Song> online_songs;
    ArrayList<Song> favourite_songs;
    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior, ArrayList<Song> online_songs, ArrayList<Song> favourite_songs) {
        super(fm, behavior);
        this.online_songs = online_songs;
        this.favourite_songs = favourite_songs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 1:
                return ListMusicFragment.newInstance(online_songs);
            case 2:
                return MoreFragment.newInstance(favourite_songs);
            default:
                return new HomeFragment();
        }
    }

    @Override
    public int getCount() {
        return 3;
    }
}
