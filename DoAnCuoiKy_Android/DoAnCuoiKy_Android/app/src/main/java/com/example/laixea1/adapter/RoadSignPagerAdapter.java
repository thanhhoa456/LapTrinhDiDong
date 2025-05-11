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
        // groupId từ 1 đến 5 tương ứng với các tab
        return RoadSignFragment.newInstance(position + 1);
    }

    @Override
    public int getItemCount() {
        return 5; // 5 tab: Biển báo cấm, Biển hiệu lệnh, Biển chỉ dẫn, Biển nguy hiểm, Biển phụ
    }
}