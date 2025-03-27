package com.example.laixea1;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
    private static final String PREFS_NAME = "A1StudyPrefs";
    private static final String KEY_THEME = "theme";
    private static final String KEY_FONT_SIZE = "fontSize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        String theme = prefs.getString(KEY_THEME, "light");
        if (theme.equals("dark")) {
            setTheme(R.style.Theme_A1StudyApp_Dark);
        } else {
            setTheme(R.style.Theme_A1StudyApp);
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    public void setContentView(int layoutResID) {
        setTheme(R.style.Theme_LaiXeA1);
        super.setContentView(layoutResID);
        applyFontSize(findViewById(android.R.id.content));
    }

    private void applyFontSize(View view) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        int fontSize = prefs.getInt(KEY_FONT_SIZE, 16); // Mặc định 16 nếu chưa lưu

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyFontSize(viewGroup.getChildAt(i));
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextSize(fontSize);
        }
    }
}