package com.example.laixea1.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.laixea1.R;

public abstract class BaseActivity extends AppCompatActivity {

    protected String currentUser;
    protected static final String PREF_NAME = "Settings_";
    private static final String KEY_FONT_SIZE = "fontSize";
    private static final int DEFAULT_FONT_SIZE = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        currentUser = appPrefs.getString("current_user", "Guest");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!currentUser.equals("Guest")) {
            applyFontSizeToViews(findViewById(android.R.id.content));
        }
    }

    // Sửa phương thức để nhận tham số View
    protected void applyFontSizeToViews(View rootView) {
        if (rootView == null) return;
        float fontSize = getSharedPreferences(PREF_NAME + currentUser, MODE_PRIVATE)
                .getInt(KEY_FONT_SIZE, DEFAULT_FONT_SIZE);
        applyFontSizeById(rootView, R.id.questionText, fontSize);
        applyFontSizeById(rootView, R.id.explanationText, fontSize);
        // Không áp dụng cho answerText ở đây vì nó nằm trong RecyclerView
    }

    private void applyFontSizeById(View rootView, int viewId, float fontSize) {
        View view = rootView.findViewById(viewId);
        if (view instanceof TextView) {
            ((TextView) view).setTextSize(fontSize);
        }
    }
}