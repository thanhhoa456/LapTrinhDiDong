package com.example.laixea1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.laixea1.R;

public class FontSizeActivity extends BaseActivity {
    private static final String PREFS_NAME = "A1StudyPrefs";
    private static final String KEY_FONT_SIZE = "fontSize";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_font_size);

        SeekBar fontSizeSeekBar = findViewById(R.id.fontSizeSeekBar);
        TextView sampleText = findViewById(R.id.sampleText);
        Button nextButton = findViewById(R.id.nextButton);
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Khôi phục cỡ chữ trước đó (mặc định 16sp)
        int savedFontSize = prefs.getInt(KEY_FONT_SIZE, 16);
        fontSizeSeekBar.setProgress(savedFontSize);
        sampleText.setTextSize(savedFontSize);

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

        nextButton.setOnClickListener(v -> {
            int fontSize = fontSizeSeekBar.getProgress();
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt(KEY_FONT_SIZE, fontSize);
            editor.apply();

            Intent intent = new Intent(FontSizeActivity.this, MainActivity.class);
            startActivity(intent);
        });
    }
}