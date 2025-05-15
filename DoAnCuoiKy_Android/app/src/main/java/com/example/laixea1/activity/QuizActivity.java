package com.example.laixea1.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.laixea1.R;
import com.example.laixea1.adapter.AnswerAdapter;
import com.example.laixea1.adapter.QuestionNumberAdapter;
import com.example.laixea1.adapter.QuestionPagerAdapter;
import com.example.laixea1.api.ApiService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;
import com.example.laixea1.fragment.QuestionFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizActivity extends BaseActivity implements TextToSpeech.OnInitListener, QuestionFragment.OnAnswerSelectedListener {

    ViewPager2 questionPager;
    QuestionPagerAdapter pagerAdapter;
    TextView titleText;
    TextView questionCounter;
    ImageButton footerBtnForward;
    ImageButton footerBtnBack;
    LinearLayout questionCounterLayout;
    private LinearLayout questionNumberLayout;
    private RecyclerView questionNumberRecyclerView;
    protected QuestionNumberAdapter questionNumberAdapter;
    private Button speakButton;

    private int currentQuestionIndex = 0;
    protected List<QuestionDTO> questionList = new ArrayList<>();
    protected List<Integer> questionNumbers = new ArrayList<>();
    protected DatabaseHelper dbHelper;

    // TextToSpeech components
    private TextToSpeech textToSpeech;
    private SharedPreferences sharedPreferences;
    private SharedPreferences ttsPreferences;
    private static final int SETTINGS_REQUEST_CODE = 100;
    protected String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        // Khởi tạo DatabaseHelper
        dbHelper = new DatabaseHelper(this);

        // Lấy current_user từ App_Settings
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        currentUser = appPrefs.getString("current_user", "Guest");

        // Khởi tạo SharedPreferences dựa trên current_user
        sharedPreferences = getSharedPreferences("Settings_" + currentUser, MODE_PRIVATE);
        ttsPreferences = getSharedPreferences("TTS_Settings_" + currentUser, MODE_PRIVATE);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        AnhXa();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        int groupId = intent.getIntExtra("groupId", -1);
        boolean isFailingScore = intent.getBooleanExtra("isFailingScore", false);
        String categoryName = intent.getStringExtra("category_name");

        // Đặt titleText từ categoryName cho mọi trường hợp
        if (categoryName != null && !categoryName.isEmpty()) {
            titleText.setText(categoryName);
        } else {
            titleText.setText("Không có tiêu đề");
            Log.w("QuizActivity", "category_name is null or empty in Intent");
        }

        // Load danh sách câu hỏi
        loadQuestions(groupId, isFailingScore);

        // Đăng ký listener cho thay đổi TTS settings
        ttsPreferences.registerOnSharedPreferenceChangeListener(ttsPreferenceChangeListener);
        Log.d("QuizActivity", "TTS listener registered for user: " + currentUser);
    }
    @Override
    protected void onResume() {
        super.onResume();
        // Áp dụng font size cho fragment hiện tại
        applyFontSizeToCurrentFragment();
    }

    @Override
    public void onAnswerSelected(int questionId, int position) {
        if (questionNumberAdapter != null) {
            int index = -1;
            for (int i = 0; i < questionList.size(); i++) {
                if (questionList.get(i).getId() == questionId) {
                    index = i;
                    break;
                }
            }
            if (index != -1) {
                // Cập nhật trạng thái trong userProgressMap
                SQLiteDatabase db = dbHelper.getReadableDatabase();
                String query = "SELECT selectedAnswer, isCorrect FROM UserProgress WHERE userId = ? AND questionId = ?";
                Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(questionId)});
                if (cursor.moveToFirst()) {
                    int selectedAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
                    boolean isCorrect = cursor.getInt(cursor.getColumnIndexOrThrow("isCorrect")) == 1;
                    questionNumberAdapter.updateProgress(questionId, selectedAnswer, isCorrect);
                }
                cursor.close();
                db.close();
                questionNumberAdapter.notifyItemChanged(index);
                Log.d("QuizActivity", "Notified QuestionNumberAdapter for questionId: " + questionId + ", index: " + index);
            } else {
                Log.w("QuizActivity", "QuestionId " + questionId + " not found in questionList");
            }
        }
    }

    private void AnhXa() {
        questionPager = findViewById(R.id.questionPager);
        titleText = findViewById(R.id.titleText);
        questionCounter = findViewById(R.id.questionCounter);
        footerBtnForward = findViewById(R.id.footerBtnForward);
        footerBtnBack = findViewById(R.id.footerBtnBack);
        questionCounterLayout = findViewById(R.id.questionCounterLayout);
        questionNumberLayout = findViewById(R.id.questionNumberLayout);
        questionNumberRecyclerView = findViewById(R.id.questionNumberRecyclerView);
        speakButton = findViewById(R.id.speakButton);

        // Thiết lập RecyclerView cho danh sách số câu hỏi
        questionNumberRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));

        // Xử lý sự kiện nhấn nút Forward
        footerBtnForward.setOnClickListener(v -> {
            if (currentQuestionIndex < questionList.size() - 1) {
                currentQuestionIndex++;
                questionPager.setCurrentItem(currentQuestionIndex);
            } else {
                Toast.makeText(this, "Đã đến câu hỏi cuối cùng!", Toast.LENGTH_SHORT).show();
            }
        });

        // Xử lý sự kiện nhấn nút Back
        footerBtnBack.setOnClickListener(v -> {
            if (currentQuestionIndex > 0) {
                currentQuestionIndex--;
                questionPager.setCurrentItem(currentQuestionIndex);
            } else {
                Toast.makeText(this, "Đã đến câu hỏi đầu tiên!", Toast.LENGTH_SHORT).show();
            }
        });

        // Đồng bộ ViewPager2 với currentQuestionIndex
        questionPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentQuestionIndex = position;
                questionCounter.setText("Câu hỏi " + (currentQuestionIndex + 1) + "/" + questionList.size());

                // Cập nhật số câu hỏi được chọn trong danh sách
                if (questionNumberAdapter != null) {
                    questionNumberAdapter.setCurrentQuestionIndex(currentQuestionIndex);
                }

                // Ẩn explanationText khi chuyển câu hỏi
                QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager()
                        .findFragmentByTag("f" + questionPager.getCurrentItem());
                if (fragment != null) {
                    View view = fragment.getView();
                    if (view != null) {
                        TextView explanationText = view.findViewById(R.id.explanationText);
                        if (explanationText != null) {
                            explanationText.setVisibility(View.GONE);
                        }
                    }
                }
            }
        });

        // Xử lý toggle hiển thị/ẩn danh sách số câu hỏi
        questionCounterLayout.setOnClickListener(v -> {
            if (questionNumberLayout.getVisibility() == View.VISIBLE) {
                questionNumberLayout.setVisibility(View.GONE);
            } else {
                questionNumberLayout.setVisibility(View.VISIBLE);
                questionNumberAdapter = new QuestionNumberAdapter(questionNumbers, currentQuestionIndex, questionList, currentUser, dbHelper, position -> {
                    currentQuestionIndex = position;
                    questionPager.setCurrentItem(currentQuestionIndex);
                    questionNumberLayout.setVisibility(View.GONE);
                });
                questionNumberRecyclerView.setAdapter(questionNumberAdapter);
            }
        });

        // Xử lý nút đọc
        speakButton.setOnClickListener(v -> {
            QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager()
                    .findFragmentByTag("f" + questionPager.getCurrentItem());
            if (fragment != null) {
                View view = fragment.getView();
                if (view != null) {
                    TextView questionText = view.findViewById(R.id.questionText);
                    if (questionText != null && questionText.getText() != null) {
                        String textToRead = questionText.getText().toString();
                        if (!textToRead.isEmpty()) {
                            textToSpeech.speak(textToRead, TextToSpeech.QUEUE_FLUSH, null, null);
                        } else {
                            Toast.makeText(QuizActivity.this, "Không có nội dung để đọc!", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
            }
        });
    }
    private void debugCriticalQuestionsInSQLite() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT id, groupId, failingScore FROM Questions WHERE failingScore = 1", null);
        Log.d("QuizActivity", "Critical questions in SQLite: " + cursor.getCount());
        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
            int groupId = cursor.getInt(cursor.getColumnIndexOrThrow("groupId"));
            int failingScore = cursor.getInt(cursor.getColumnIndexOrThrow("failingScore"));
            Log.d("QuizActivity", "Question ID: " + id + ", groupId: " + groupId + ", failingScore: " + failingScore);
        }
        cursor.close();
        db.close();
    }
    protected void loadQuestions(int groupId, boolean isFailingScore) {
        Log.d("QuizActivity", "loadQuestions called with groupId: " + groupId + ", isFailingScore: " + isFailingScore);
        debugCriticalQuestionsInSQLite();
        checkCriticalQuestionsInSQLite();
        questionList = loadQuestionsFromSQLite(groupId, isFailingScore);
        if (!questionList.isEmpty()) {
            Log.d("QuizActivity", "Loaded questions from SQLite, initializing quiz");
            initializeQuiz();
            return;
        }

        // Nếu SQLite rỗng, tải từ API
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<QuestionDTO>> call;

        if (isFailingScore) {
            Log.d("QuizActivity", "Calling API for critical questions");
            call = apiService.getCriticalQuestions();
        } else {
            Log.d("QuizActivity", "Calling API for groupId: " + groupId);
            call = apiService.getQuestionsByGroupId(groupId);
        }

        call.enqueue(new Callback<List<QuestionDTO>>() {
            @Override
            public void onResponse(Call<List<QuestionDTO>> call, Response<List<QuestionDTO>> response) {
                Log.d("QuizActivity", "API Response Code: " + response.code());
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();
                    Log.d("QuizActivity", "Questions from API: " + questionList.size());
                    if (questionList.isEmpty()) {
                        Log.w("QuizActivity", "API returned empty question list");
                        Toast.makeText(QuizActivity.this, "Không có câu hỏi từ API!", Toast.LENGTH_LONG).show();
                        return;
                    }
                    saveQuestionsToSQLite(questionList);
                    initializeQuiz();
                } else {
                    Log.e("QuizActivity", "API Error: " + response.message());
                    Toast.makeText(QuizActivity.this, "Lỗi khi lấy dữ liệu: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuestionDTO>> call, Throwable t) {
                Log.e("QuizActivity", "API Failure: " + t.getMessage());
                Toast.makeText(QuizActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    protected void initializeQuiz() {
        // Cập nhật bộ đếm câu hỏi
        questionCounter.setText("Câu hỏi 1/" + questionList.size());

        // Cập nhật questionNumbers
        questionNumbers.clear();
        for (int i = 0; i < questionList.size(); i++) {
            questionNumbers.add(i + 1);
        }

        // Khởi tạo QuestionNumberAdapter
        questionNumberAdapter = new QuestionNumberAdapter(questionNumbers, currentQuestionIndex, questionList, currentUser, dbHelper, position -> {
            currentQuestionIndex = position;
            questionPager.setCurrentItem(currentQuestionIndex);
            questionNumberLayout.setVisibility(View.GONE);
        });
        questionNumberRecyclerView.setAdapter(questionNumberAdapter);

        // Thiết lập ViewPager2
        pagerAdapter = new QuestionPagerAdapter(QuizActivity.this, questionList, currentUser, QuizActivity.this);
        questionPager.setAdapter(pagerAdapter);
    }

    protected List<QuestionDTO> loadQuestionsFromSQLite(int groupId, boolean isFailingScore) {
        List<QuestionDTO> questions = new ArrayList<>();
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query;
            String[] selectionArgs;
            if (isFailingScore) {
                query = "SELECT * FROM Questions WHERE failingScore = ?";
                selectionArgs = new String[]{"1"};
                Log.d("QuizActivity", "Executing query for critical questions: " + query);
            } else {
                query = "SELECT * FROM Questions WHERE groupId = ?";
                selectionArgs = new String[]{String.valueOf(groupId)};
                Log.d("QuizActivity", "Executing query for groupId: " + groupId + ", query: " + query);
            }

            cursor = db.rawQuery(query, selectionArgs);
            Log.d("QuizActivity", "Cursor count: " + cursor.getCount());
            while (cursor.moveToNext()) {
                QuestionDTO question = new QuestionDTO();
                try {
                    question.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                    question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow("text")));
                    question.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("groupId")));
                    question.setFailingScore(cursor.getInt(cursor.getColumnIndexOrThrow("failingScore")) == 1);
                    question.setExplainQuestion(cursor.getString(cursor.getColumnIndexOrThrow("explainQuestion")));
                    question.setAnswer(cursor.getInt(cursor.getColumnIndexOrThrow("answer")));
                    Log.d("QuizActivity", "Loaded question ID: " + question.getId() + ", failingScore: " + question.isFailingScore());

                    // Tải đáp án
                    List<String> options = new ArrayList<>();
                    String answerQuery = "SELECT * FROM Answers WHERE questionId = ? ORDER BY id";
                    Cursor answerCursor = db.rawQuery(answerQuery, new String[]{String.valueOf(question.getId())});
                    while (answerCursor.moveToNext()) {
                        String text = answerCursor.getString(answerCursor.getColumnIndexOrThrow("text"));
                        if (text != null) {
                            options.add(text);
                        }
                    }
                    answerCursor.close();
                    Log.d("QuizActivity", "Loaded " + options.size() + " options for question ID: " + question.getId());
                    if (options.size() >= 1) question.setOption1(options.get(0));
                    if (options.size() >= 2) question.setOption2(options.get(1));
                    if (options.size() >= 3) question.setOption3(options.get(2));
                    if (options.size() >= 4) question.setOption4(options.get(3));

                    // Tải hình ảnh
                    String imageQuery = "SELECT imagePath FROM Images WHERE questionId = ?";
                    Cursor imageCursor = db.rawQuery(imageQuery, new String[]{String.valueOf(question.getId())});
                    if (imageCursor.moveToFirst()) {
                        String imagePath = imageCursor.getString(imageCursor.getColumnIndexOrThrow("imagePath"));
                        question.setImage(imagePath);
                        Log.d("QuizActivity", "Loaded image path: " + imagePath);
                    }
                    imageCursor.close();

                    questions.add(question);
                } catch (Exception e) {
                    Log.e("QuizActivity", "Error processing question ID: " + cursor.getInt(cursor.getColumnIndexOrThrow("id")) + ", " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            Log.e("QuizActivity", "loadQuestionsFromSQLite: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        Log.d("QuizActivity", "Loaded " + questions.size() + " questions from SQLite, groupId: " + groupId);
        return questions;
    }
    private void saveQuestionsToSQLite(List<QuestionDTO> questions) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.beginTransaction();
        try {
            for (QuestionDTO question : questions) {
                // Lưu câu hỏi
                ContentValues questionValues = new ContentValues();
                questionValues.put("id", question.getId());
                questionValues.put("text", question.getQuestion());
                questionValues.put("groupId", question.getGroupId());
                questionValues.put("failingScore", question.isFailingScore() ? 1 : 0);
                questionValues.put("explainQuestion", question.getExplainQuestion());
                questionValues.put("answer", question.getAnswer());
                db.insertWithOnConflict("Questions", null, questionValues, SQLiteDatabase.CONFLICT_REPLACE);

                // Lưu đáp án
                List<String> options = new ArrayList<>();
                if (question.getOption1() != null) options.add(question.getOption1());
                if (question.getOption2() != null) options.add(question.getOption2());
                if (question.getOption3() != null) options.add(question.getOption3());
                if (question.getOption4() != null) options.add(question.getOption4());
                for (int i = 0; i < options.size(); i++) {
                    ContentValues answerValues = new ContentValues();
                    answerValues.put("questionId", question.getId());
                    answerValues.put("text", options.get(i));
                    answerValues.put("isCorrect", (i + 1) == question.getAnswer() ? 1 : 0);
                    db.insertWithOnConflict("Answers", null, answerValues, SQLiteDatabase.CONFLICT_REPLACE);
                }

                // Lưu ảnh nếu có
                if (question.getImage() != null && !question.getImage().isEmpty()) {
                    String imagePath = saveImageToFile(question.getImage(), question.getId());
                    if (imagePath != null) {
                        ContentValues imageValues = new ContentValues();
                        imageValues.put("questionId", question.getId());
                        imageValues.put("imagePath", imagePath);
                        db.insertWithOnConflict("Images", null, imageValues, SQLiteDatabase.CONFLICT_REPLACE);
                        Log.d("QuizActivity", "Saved image path to SQLite: " + imagePath + " for question " + question.getId());
                        // Gán lại imagePath vào question để đảm bảo hiển thị ngay lập tức
                        question.setImage(imagePath);
                    } else {
                        Log.w("QuizActivity", "Failed to save image for question " + question.getId());
                    }
                } else {
                    Log.d("QuizActivity", "No image to save for question " + question.getId());
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("QuizActivity", "Error saving questions to SQLite", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    private String saveImageToFile(String base64Image, int questionId) {
        try {
            if (base64Image == null || base64Image.isEmpty()) {
                Log.w("QuizActivity", "Base64 image is null or empty for question " + questionId);
                return null;
            }

            // Loại bỏ tiền tố Base64 nếu có
            String cleanImageData = base64Image;
            if (base64Image.startsWith("data:image")) {
                cleanImageData = base64Image.replaceFirst("^data:image/[^;]+;base64,", "");
            }

            // Kiểm tra dữ liệu Base64
            byte[] decodedBytes = Base64.decode(cleanImageData, Base64.DEFAULT);
            if (decodedBytes == null || decodedBytes.length == 0) {
                Log.w("QuizActivity", "Failed to decode Base64 image for question " + questionId);
                return null;
            }

            // Tạo file
            File imageFile = new File(getFilesDir(), "question_" + questionId + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(decodedBytes);
                fos.flush();
            }

            // Kiểm tra file có tồn tại không
            if (imageFile.exists()) {
                Log.d("QuizActivity", "Image saved successfully: " + imageFile.getAbsolutePath() + " for question " + questionId);
                return imageFile.getAbsolutePath();
            } else {
                Log.w("QuizActivity", "Image file not created: " + imageFile.getAbsolutePath() + " for question " + questionId);
                return null;
            }
        } catch (Exception e) {
            Log.e("QuizActivity", "Error saving image for question " + questionId + ": " + e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.quiz_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            startActivityForResult(new Intent(this, SettingsActivity.class), SETTINGS_REQUEST_CODE);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Đồng bộ phương thức logout với SettingsActivity
    public void logout() {
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        appPrefs.edit()
                .putString("current_user", "Guest")
                .apply();
        Intent intent = new Intent(this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        finish();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            int result = textToSpeech.setLanguage(new Locale("vi_VN"));
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                textToSpeech.setLanguage(Locale.getDefault());
                Toast.makeText(this, "Ngôn ngữ tiếng Việt không được hỗ trợ, sử dụng ngôn ngữ mặc định.", Toast.LENGTH_SHORT).show();
            }

            // Apply saved TTS settings
            boolean isDefaultMode = ttsPreferences.getBoolean("isDefaultMode", true);
            float speed = isDefaultMode ? 1.0f : ttsPreferences.getFloat("speed", 1.0f);
            textToSpeech.setSpeechRate(speed);
            Log.d("QuizActivity", "TTS initialized, user: " + currentUser + ", speed: " + speed);
        } else {
            Toast.makeText(this, "Khởi tạo TextToSpeech thất bại!", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release TextToSpeech resources
        if (textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
            textToSpeech = null;
        }
        // Release AnswerAdapter MediaPlayers
        if (pagerAdapter != null) {
            for (int i = 0; i < questionList.size(); i++) {
                QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager()
                        .findFragmentByTag("f" + i);
                if (fragment != null) {
                    View view = fragment.getView();
                    if (view != null) {
                        RecyclerView answerRecyclerView = view.findViewById(R.id.answerRecyclerView);
                        if (answerRecyclerView != null && answerRecyclerView.getAdapter() instanceof AnswerAdapter) {
                            ((AnswerAdapter) answerRecyclerView.getAdapter()).releaseMediaPlayers();
                        }
                    }
                }
            }
        }
        // Đóng DatabaseHelper
        if (dbHelper != null) {
            dbHelper.close();
        }
        // Hủy đăng ký listener
        if (ttsPreferences != null) {
            ttsPreferences.unregisterOnSharedPreferenceChangeListener(ttsPreferenceChangeListener);
            Log.d("QuizActivity", "TTS listener unregistered for user: " + currentUser);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Không cần làm gì vì listener đã xử lý
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("currentQuestionIndex", currentQuestionIndex);
        outState.putInt("groupId", getIntent().getIntExtra("groupId", -1));
        outState.putBoolean("isFailingScore", getIntent().getBooleanExtra("isFailingScore", false));
        outState.putString("categoryName", getIntent().getStringExtra("category_name"));
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        currentQuestionIndex = savedInstanceState.getInt("currentQuestionIndex", 0);
        int groupId = savedInstanceState.getInt("groupId", -1);
        boolean isFailingScore = savedInstanceState.getBoolean("isFailingScore", false);
        String categoryName = savedInstanceState.getString("categoryName", null);

        // Khôi phục titleText từ categoryName
        if (categoryName != null && !categoryName.isEmpty()) {
            titleText.setText(categoryName);
        } else {
            titleText.setText("Không có tiêu đề");
            Log.w("QuizActivity", "categoryName is null or empty in savedInstanceState");
        }

        // Tải lại câu hỏi nếu questionList rỗng
        if (questionList.isEmpty()) {
            loadQuestions(groupId, isFailingScore);
        } else {
            // Cập nhật UI nếu questionList đã có dữ liệu
            questionCounter.setText("Câu hỏi " + (currentQuestionIndex + 1) + "/" + questionList.size());
            questionPager.setCurrentItem(currentQuestionIndex);
            if (questionNumberAdapter != null) {
                questionNumberAdapter.setCurrentQuestionIndex(currentQuestionIndex);
            }
        }
    }
    private void applyFontSizeToCurrentFragment() {
        QuestionFragment fragment = (QuestionFragment) getSupportFragmentManager()
                .findFragmentByTag("f" + questionPager.getCurrentItem());
        if (fragment != null && fragment.getView() != null) {
            applyFontSizeToViews(fragment.getView());
        }
    }
    private void checkCriticalQuestionsInSQLite() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Questions WHERE failingScore = 1", null);
        int count = cursor.getCount();
        Log.d("QuizActivity", "Critical questions in SQLite: " + count);
        cursor.close();
        db.close();
    }

    private final SharedPreferences.OnSharedPreferenceChangeListener ttsPreferenceChangeListener =
            (sharedPreferences, key) -> {
                Log.d("QuizActivity", "TTS preference changed: " + key + " for user: " + currentUser);
                if (key.equals("speed") || key.equals("isDefaultMode")) {
                    if (textToSpeech != null) {
                        boolean isDefaultMode = ttsPreferences.getBoolean("isDefaultMode", true);
                        float speed = isDefaultMode ? 1.0f : ttsPreferences.getFloat("speed", 1.0f);
                        textToSpeech.setSpeechRate(speed);
                        Log.d("QuizActivity", "TTS speed updated to: " + speed + " for user: " + currentUser);
                    } else {
                        Log.w("QuizActivity", "TextToSpeech is null, cannot update speed for user: " + currentUser);
                    }
                }
            };
}