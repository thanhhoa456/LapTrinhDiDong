package com.example.laixea1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.core.content.ContextCompat;

import com.example.laixea1.R;

public class ThemeActivity extends BaseActivity {
    private static final String PREFS_NAME = "A1StudyPrefs";
    private static final String KEY_THEME = "theme";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        RadioGroup themeRadioGroup = findViewById(R.id.themeRadioGroup);
        RadioButton radioLight = findViewById(R.id.radioLight);
        RadioButton radioDark = findViewById(R.id.radioDark);
        Button nextButton = findViewById(R.id.nextButton);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Khôi phục lựa chọn trước đó (nếu có)
        String savedTheme = prefs.getString(KEY_THEME, "light");
        if (savedTheme.equals("dark")) {
            themeRadioGroup.check(R.id.radioDark);
            radioLight.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            radioDark.setTextColor(ContextCompat.getColor(this, android.R.color.white));
        } else {
            themeRadioGroup.check(R.id.radioLight);
            radioLight.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            radioDark.setTextColor(ContextCompat.getColor(this, android.R.color.black));
        }

        // Thay đổi theme và màu chữ ngay khi chọn RadioButton
        themeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (checkedId == R.id.radioDark) {
                editor.putString(KEY_THEME, "dark");
                setTheme(R.style.Theme_A1StudyApp_Dark);
                radioLight.setTextColor(ContextCompat.getColor(this, android.R.color.white));
                radioDark.setTextColor(ContextCompat.getColor(this, android.R.color.white));
            } else {
                editor.putString(KEY_THEME, "light");
                setTheme(R.style.Theme_A1StudyApp);
                radioLight.setTextColor(ContextCompat.getColor(this, android.R.color.black));
                radioDark.setTextColor(ContextCompat.getColor(this, android.R.color.black));
            }
            editor.apply();
            recreate(); // Làm mới giao diện
        });

        nextButton.setOnClickListener(v -> {
            Intent intent = new Intent(ThemeActivity.this, FontSizeActivity.class);
            startActivity(intent);
        });
    }
}