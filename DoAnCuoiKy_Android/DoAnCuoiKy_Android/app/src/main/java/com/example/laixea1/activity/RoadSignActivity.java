package com.example.laixea1.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;

import com.example.laixea1.adapter.RoadSignPagerAdapter;
import com.example.laixea1.databinding.ActivityRoadSignBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class RoadSignActivity extends AppCompatActivity {
    private ActivityRoadSignBinding binding; // Sử dụng View Binding

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Khởi tạo View Binding
        binding = ActivityRoadSignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Thiết lập Toolbar
        Toolbar toolbar = binding.toolBar;
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Biển báo giao thông");
        }

        // Thiết lập ViewPager2 và TabLayout
        RoadSignPagerAdapter adapter = new RoadSignPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    String tabText;
                    switch (position) {
                        case 0:
                            tabText = "Biển báo cấm";
                            break;
                        case 1:
                            tabText = "Biển hiệu lệnh";
                            break;
                        case 2:
                            tabText = "Biển chỉ dẫn";
                            break;
                        case 3:
                            tabText = "Biển nguy hiểm và cảnh báo";
                            break;
                        case 4:
                            tabText = "Biển phụ";
                            break;
                        default:
                            tabText = "";
                    }
                    tab.setText(tabText);
                    tab.setContentDescription(tabText); // Thêm contentDescription cho accessibility
                }).attach();

        // Listener cho TabLayout để thay đổi FAB icon
        binding.tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                binding.viewPager.setCurrentItem(tab.getPosition());

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });

        // Callback cho ViewPager2 để đồng bộ với TabLayout
        binding.viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                binding.tabLayout.selectTab(binding.tabLayout.getTabAt(position));
            }
        });

    }
}