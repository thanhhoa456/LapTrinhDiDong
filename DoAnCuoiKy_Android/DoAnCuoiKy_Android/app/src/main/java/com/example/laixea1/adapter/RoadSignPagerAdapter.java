package com.example.laixea1.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.laixea1.fragment.RoadSignFragment;

public class RoadSignPagerAdapter extends FragmentStateAdapter {
    public RoadSignPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        int groupId = position + 1; // Giả sử groupId từ 1 đến 5
        return RoadSignFragment.newInstance(groupId);
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
