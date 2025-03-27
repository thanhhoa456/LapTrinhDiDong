package com.example.laixea1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.GridView;
import android.widget.ImageButton;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends BaseActivity {
    private static final String PREFS_NAME = "A1StudyPrefs";
    private static final String KEY_THEME = "theme";
    private ViewPager2 adViewPager;
    private Handler handler = new Handler();
    private Runnable runnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        adViewPager = findViewById(R.id.adViewPager);
        GridView menuGridView = findViewById(R.id.menuGridView);
        ImageButton settingsButton = findViewById(R.id.settingsButton);

        // Dữ liệu cho GridView
        String[] menuTitles = {"Học lý thuyết", "Thi sát hạch", "Biển báo", "Mẹo thi", "Các câu sai"};
        int[] menuIcons = {
                R.drawable.theory,
                R.drawable.test,
                R.drawable.sign,
                R.drawable.tips,
                R.drawable.wrong
        };

        // Thiết lập Adapter cho GridView
        MenuAdapter menuAdapter = new MenuAdapter(this, menuTitles, menuIcons);
        menuGridView.setAdapter(menuAdapter);

        // Áp dụng màu cho biểu tượng Settings
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "light");
        if (theme.equals("dark")) {
            settingsButton.setColorFilter(ContextCompat.getColor(this, android.R.color.white));
        } else {
            settingsButton.setColorFilter(ContextCompat.getColor(this, android.R.color.black));
        }

        // Danh sách ảnh quảng cáo mẫu
        ArrayList<Integer> adImages = new ArrayList<>(Arrays.asList(
                R.drawable.sach, R.drawable.sahinh, R.drawable.huongdi, R.drawable.bienbao
        ));

        // Adapter cho ViewPager2
        Adapter adAdapter = new Adapter(adImages);
        adViewPager.setAdapter(adAdapter);

        // Tự động chuyển ảnh quảng cáo mỗi 3 giây
        runnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = adViewPager.getCurrentItem();
                int nextItem = (currentItem + 1) % adImages.size();
                adViewPager.setCurrentItem(nextItem);
                handler.postDelayed(this, 3000);
            }
        };
        handler.postDelayed(runnable, 3000);

        // Sự kiện nhấn cho GridView
        menuGridView.setOnItemClickListener((parent, view, position, id) -> {
            switch (position) {
                case 0:
                    // TODO: Chuyển sang màn hình Học lý thuyết
                    break;
                case 1:
                    // TODO: Chuyển sang màn hình Thi sát hạch
                    break;
                case 2:
                    // TODO: Chuyển sang màn hình Biển báo
                    break;
                case 3:
                    // TODO: Chuyển sang màn hình Mẹo thi
                    break;
                case 4:
                    // TODO: Chuyển sang màn hình Các câu sai
                    break;
            }
        });

        settingsButton.setOnClickListener(v -> {
            // TODO: Chuyển sang màn hình cài đặt
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }
}