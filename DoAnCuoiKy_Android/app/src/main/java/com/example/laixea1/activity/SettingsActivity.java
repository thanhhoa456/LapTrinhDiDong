package com.example.laixea1.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.laixea1.R;

public class SettingsActivity extends BaseActivity {

    private SeekBar fontSizeSeekBar;
    private TextView sampleText;
    private Button speed1xButton, speed15xButton, speed2xButton;
    private RadioButton defaultModeRadio, customModeRadio;
    private TextView speedLabel;
    private View speedButtons;
    private Button saveButton, defaultButton, accountButton;

    private static final String TTS_PREF_NAME = "TTS_Settings_";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final String KEY_READ_SPEED = "speed";
    private static final String KEY_MODE = "isDefaultMode";
    private static final int DEFAULT_FONT_SIZE = 16;
    private static final float DEFAULT_READ_SPEED = 1.0f;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Kiểm tra Guest
        if (currentUser.equals("Guest")) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Yêu cầu đăng nhập");
            builder.setMessage("Chức năng cài đặt chỉ dành cho người dùng đã đăng nhập. Bạn có muốn đăng nhập ngay không?");

            builder.setPositiveButton("Đăng nhập", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish(); // nếu muốn thoát khỏi activity hiện tại
                }
            });

            builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish(); // hoặc không finish() nếu muốn quay lại
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();

            return;
        }


        setContentView(R.layout.activity_settings);

        // Ánh xạ view
        fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar);
        sampleText = findViewById(R.id.sampleText);
        speed1xButton = findViewById(R.id.speed1xButton);
        speed15xButton = findViewById(R.id.speed1_5xButton);
        speed2xButton = findViewById(R.id.speed2xButton);
        defaultModeRadio = findViewById(R.id.defaultModeRadio);
        customModeRadio = findViewById(R.id.customModeRadio);
        speedLabel = findViewById(R.id.speedLabel);
        speedButtons = findViewById(R.id.speedButtons);
        saveButton = findViewById(R.id.saveButton);
        defaultButton = findViewById(R.id.defaultButton);
        accountButton = findViewById(R.id.accountButton);

        // Load cài đặt
        loadSettings();

        // Xử lý SeekBar
        fontSizeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                sampleText.setTextSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        // Xử lý chế độ giọng đọc
        defaultModeRadio.setOnCheckedChangeListener((buttonView, isChecked) -> {
            speedLabel.setVisibility(isChecked ? View.GONE : View.VISIBLE);
            speedButtons.setVisibility(isChecked ? View.GONE : View.VISIBLE);
        });

        // Xử lý nút tốc độ
        final float[] selectedSpeed = {DEFAULT_READ_SPEED};
        speed1xButton.setOnClickListener(v -> {
            selectedSpeed[0] = 1.0f;
            updateSpeedButtonStates(speed1xButton, speed15xButton, speed2xButton);
        });
        speed15xButton.setOnClickListener(v -> {
            selectedSpeed[0] = 1.5f;
            updateSpeedButtonStates(speed15xButton, speed1xButton, speed2xButton);
        });
        speed2xButton.setOnClickListener(v -> {
            selectedSpeed[0] = 2.0f;
            updateSpeedButtonStates(speed2xButton, speed1xButton, speed15xButton);
        });

        // Xử lý nút lưu
        saveButton.setOnClickListener(v -> {
            SharedPreferences.Editor editor = getSharedPreferences(PREF_NAME + currentUser, MODE_PRIVATE).edit();
            editor.putInt(KEY_FONT_SIZE, fontSizeSeekBar.getProgress());
            editor.apply();

            SharedPreferences.Editor ttsEditor = getSharedPreferences(TTS_PREF_NAME + currentUser, MODE_PRIVATE).edit();
            ttsEditor.putFloat(KEY_READ_SPEED, defaultModeRadio.isChecked() ? DEFAULT_READ_SPEED : selectedSpeed[0]);
            ttsEditor.putBoolean(KEY_MODE, defaultModeRadio.isChecked());
            ttsEditor.apply();

            Toast.makeText(this, "Đã lưu cài đặt!", Toast.LENGTH_SHORT).show();
        });

        // Xử lý nút mặc định
        defaultButton.setOnClickListener(v -> {
            fontSizeSeekBar.setProgress(DEFAULT_FONT_SIZE);
            defaultModeRadio.setChecked(true);
            selectedSpeed[0] = DEFAULT_READ_SPEED;
            updateSpeedButtonStates(speed1xButton, speed15xButton, speed2xButton);
            speedLabel.setVisibility(View.GONE);
            speedButtons.setVisibility(View.GONE);
            sampleText.setTextSize(DEFAULT_FONT_SIZE);

            getSharedPreferences(PREF_NAME + currentUser, MODE_PRIVATE).edit().clear().apply();
            getSharedPreferences(TTS_PREF_NAME + currentUser, MODE_PRIVATE).edit().clear().apply();

            Toast.makeText(this, "Đã đặt lại cài đặt mặc định!", Toast.LENGTH_SHORT).show();
        });
        accountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });

    }

    private void loadSettings() {
        SharedPreferences prefs = getSharedPreferences(PREF_NAME + currentUser, MODE_PRIVATE);
        SharedPreferences ttsPrefs = getSharedPreferences(TTS_PREF_NAME + currentUser, MODE_PRIVATE);
        int fontSize = prefs.getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        float readSpeed = ttsPrefs.getFloat(KEY_READ_SPEED, DEFAULT_READ_SPEED);
        boolean isDefaultMode = ttsPrefs.getBoolean(KEY_MODE, true);

        fontSizeSeekBar.setProgress(fontSize);
        sampleText.setTextSize(fontSize);
        defaultModeRadio.setChecked(isDefaultMode);
        customModeRadio.setChecked(!isDefaultMode);
        speedLabel.setVisibility(isDefaultMode ? View.GONE : View.VISIBLE);
        speedButtons.setVisibility(isDefaultMode ? View.GONE : View.VISIBLE);

        if (!isDefaultMode) {
            if (readSpeed == 1.0f) {
                updateSpeedButtonStates(speed1xButton, speed15xButton, speed2xButton);
            } else if (readSpeed == 1.5f) {
                updateSpeedButtonStates(speed15xButton, speed1xButton, speed2xButton);
            } else if (readSpeed == 2.0f) {
                updateSpeedButtonStates(speed2xButton, speed1xButton, speed15xButton);
            }
        }
    }

    private void updateSpeedButtonStates(Button selected, Button... others) {
        selected.setBackgroundTintList(getResources().getColorStateList(android.R.color.holo_blue_light));
        for (Button button : others) {
            button.setBackgroundTintList(getResources().getColorStateList(android.R.color.transparent));
        }
    }

    public void logout() {
        getSharedPreferences("App_Settings", MODE_PRIVATE).edit()
                .putString("current_user", "Guest")
                .apply();
        startActivity(new Intent(this, LoginActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP));
        finish();
    }

}