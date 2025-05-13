package com.example.laixea1.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;

public class SettingsActivity extends AppCompatActivity {

    private RadioGroup modeRadioGroup;
    private LinearLayout speedButtons;
    private Button speed1xButton, speed1_5xButton, speed2xButton;
    private Button saveButton;

    private SharedPreferences sharedPreferences;
    private float selectedSpeed = 1.0f;
    private boolean isDefaultMode = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        // Lấy current_user từ App_Settings
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        String currentUser = appPrefs.getString("current_user", "Guest");

        // Khởi tạo SharedPreferences dựa trên current_user
        sharedPreferences = getSharedPreferences("TTS_Settings_" + currentUser, MODE_PRIVATE);

        // Ánh xạ các thành phần UI
        modeRadioGroup = findViewById(R.id.modeRadioGroup);
        speedButtons = findViewById(R.id.speedButtons);
        speed1xButton = findViewById(R.id.speed1xButton);
        speed1_5xButton = findViewById(R.id.speed1_5xButton);
        speed2xButton = findViewById(R.id.speed2xButton);
        saveButton = findViewById(R.id.saveButton);

        // Load saved settings
        loadSavedSettings();

        // Xử lý chế độ
        modeRadioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.defaultModeRadio) {
                isDefaultMode = true;
                speedButtons.setVisibility(View.GONE);
                findViewById(R.id.speedLabel).setVisibility(View.GONE);
                selectedSpeed = 1.0f;
                clearSpeedButtonSelection();
            } else if (checkedId == R.id.customModeRadio) {
                isDefaultMode = false;
                speedButtons.setVisibility(View.VISIBLE);
                findViewById(R.id.speedLabel).setVisibility(View.VISIBLE);
                updateSpeedButtonSelection();
            }
        });

        // Xử lý nút tốc độ
        speed1xButton.setOnClickListener(v -> {
            selectedSpeed = 1.0f;
            updateSpeedButtonSelection();
            Log.d("SettingsActivity", "1x clicked, selectedSpeed: " + selectedSpeed);
        });
        speed1_5xButton.setOnClickListener(v -> {
            selectedSpeed = 1.5f;
            updateSpeedButtonSelection();
            Log.d("SettingsActivity", "1.5x clicked, selectedSpeed: " + selectedSpeed);
        });
        speed2xButton.setOnClickListener(v -> {
            selectedSpeed = 2.0f;
            updateSpeedButtonSelection();
            Log.d("SettingsActivity", "2x clicked, selectedSpeed: " + selectedSpeed);
        });

        // Xử lý nút lưu
        saveButton.setOnClickListener(v -> {
            saveSettings();
            Toast.makeText(SettingsActivity.this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
            setResult(RESULT_OK);
            finish();
        });
    }

    private void loadSavedSettings() {
        isDefaultMode = sharedPreferences.getBoolean("isDefaultMode", true);
        selectedSpeed = sharedPreferences.getFloat("speed", 1.0f);

        // Cập nhật UI dựa trên cài đặt đã lưu
        if (isDefaultMode) {
            modeRadioGroup.check(R.id.defaultModeRadio);
            speedButtons.setVisibility(View.GONE);
            findViewById(R.id.speedLabel).setVisibility(View.GONE);
            clearSpeedButtonSelection();
        } else {
            modeRadioGroup.check(R.id.customModeRadio);
            speedButtons.setVisibility(View.VISIBLE);
            findViewById(R.id.speedLabel).setVisibility(View.VISIBLE);
            updateSpeedButtonSelection();
        }
        Log.d("SettingsActivity", "Loaded settings, selectedSpeed: " + selectedSpeed + ", isDefaultMode: " + isDefaultMode);
    }

    private void saveSettings() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("isDefaultMode", isDefaultMode);
        editor.putFloat("speed", selectedSpeed);
        editor.apply();
    }

    private void updateSpeedButtonSelection() {
        speed1xButton.setSelected(selectedSpeed == 1.0f);
        speed1xButton.setActivated(selectedSpeed == 1.0f);
        speed1_5xButton.setSelected(selectedSpeed == 1.5f);
        speed1_5xButton.setActivated(selectedSpeed == 1.5f);
        speed2xButton.setSelected(selectedSpeed == 2.0f);
        speed2xButton.setActivated(selectedSpeed == 2.0f);
        Log.d("SettingsActivity", "Updated selection - 1x: " + speed1xButton.isSelected() + "/" + speed1xButton.isActivated() +
                ", 1.5x: " + speed1_5xButton.isSelected() + "/" + speed1_5xButton.isActivated() +
                ", 2x: " + speed2xButton.isSelected() + "/" + speed2xButton.isActivated());
    }

    private void clearSpeedButtonSelection() {
        speed1xButton.setSelected(false);
        speed1xButton.setActivated(false);
        speed1_5xButton.setSelected(false);
        speed1_5xButton.setActivated(false);
        speed2xButton.setSelected(false);
        speed2xButton.setActivated(false);
        Log.d("SettingsActivity", "Cleared selection - 1x: " + speed1xButton.isSelected() + "/" + speed1xButton.isActivated() +
                ", 1.5x: " + speed1_5xButton.isSelected() + "/" + speed1_5xButton.isActivated() +
                ", 2x: " + speed2xButton.isSelected() + "/" + speed2xButton.isActivated());
    }
}