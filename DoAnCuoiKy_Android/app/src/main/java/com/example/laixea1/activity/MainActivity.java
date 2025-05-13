package com.example.laixea1.activity;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.laixea1.R;
import com.example.laixea1.adapter.AdvAdapter;
import com.example.laixea1.adapter.MenuAdapter;

import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends BaseActivity {
    private static final String PREFS_NAME = "A1StudyPrefs";
    private static final String KEY_THEME = "theme";
    private ViewPager2 adViewPager;
    private Handler handler = new Handler();
    private Runnable runnable;
    private static final int SETTINGS_REQUEST_CODE = 100;

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
        AdvAdapter adAdapter = new AdvAdapter(adImages);
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
            Toast.makeText(this, "Click at: " + position, Toast.LENGTH_SHORT).show();

            switch (position) {
                case 0:
                    Intent theoryIntent = new Intent(MainActivity.this, TheoryActivity.class);
                    startActivity(theoryIntent);
                    break;
                case 1:
                    // TODO: Chuyển sang màn hình Thi sát hạch
                    break;
                case 2:
                    Intent roadSignIntent = new Intent(MainActivity.this, RoadSignActivity.class);
                    startActivity(roadSignIntent);
                    break;
                case 3:
                    Intent tipsIntent = new Intent(MainActivity.this, TipsActivity.class);
                    startActivity(tipsIntent);
                    break;
                case 4:
                    // TODO: Chuyển sang màn hình Các câu sai
                    break;
            }
        });

        settingsButton.setOnClickListener(v -> {
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Xử lý nếu cần sau khi lưu cài đặt
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(runnable);
    }

    // Thêm phương thức đăng xuất
    public void logout() {
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        SharedPreferences.Editor editor = appPrefs.edit();
        editor.putString("current_user", "Guest");
        editor.putBoolean("remember_me", false);
        editor.remove("saved_email");
        editor.apply();

        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}