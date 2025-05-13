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

public class QuizActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, QuestionFragment.OnAnswerSelectedListener {

    ViewPager2 questionPager;
    QuestionPagerAdapter pagerAdapter;
    TextView titleText;
    TextView questionCounter;
    ImageButton footerBtnForward;
    ImageButton footerBtnBack;
    LinearLayout questionCounterLayout;
    private LinearLayout questionNumberLayout;
    private RecyclerView questionNumberRecyclerView;
    private QuestionNumberAdapter questionNumberAdapter;
    private Button speakButton;

    private int currentQuestionIndex = 0;
    private List<QuestionDTO> questionList = new ArrayList<>();
    private List<Integer> questionNumbers = new ArrayList<>();
    private DatabaseHelper dbHelper;

    // TextToSpeech components
    private TextToSpeech textToSpeech;
    private SharedPreferences sharedPreferences;
    private static final int SETTINGS_REQUEST_CODE = 100;
    private String currentUser;

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
        sharedPreferences = getSharedPreferences("TTS_Settings_" + currentUser, MODE_PRIVATE);

        // Initialize TextToSpeech
        textToSpeech = new TextToSpeech(this, this);

        AnhXa();

        // Lấy dữ liệu từ Intent
        Intent intent = getIntent();
        int groupId = intent.getIntExtra("groupId", -1);
        boolean isFailingScore = intent.getBooleanExtra("isFailingScore", false);
        String categoryName = intent.getStringExtra("category_name");

        // Nếu là câu hỏi điểm liệt, set titleText ngay lập tức
        if (isFailingScore && categoryName != null) {
            titleText.setText(categoryName);
        }

        // Load danh sách câu hỏi
        loadQuestions(groupId, isFailingScore);
    }

    @Override
    public void onAnswerSelected() {
        // Cập nhật QuestionNumberAdapter khi trạng thái đáp án thay đổi
        if (questionNumberAdapter != null) {
            questionNumberAdapter.notifyItemChanged(currentQuestionIndex);
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

    private void loadQuestions(int groupId, boolean isFailingScore) {
        // Kiểm tra xem câu hỏi đã có trong SQLite chưa
        questionList = loadQuestionsFromSQLite(groupId, isFailingScore);
        if (!questionList.isEmpty()) {
            // Nếu đã có dữ liệu trong SQLite, sử dụng nó
            initializeQuiz();
            return;
        }

        // Nếu chưa có, tải từ API và lưu vào SQLite
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<QuestionDTO>> call;

        if (isFailingScore) {
            call = apiService.getCriticalQuestions();
        } else {
            call = apiService.getQuestionsByGroupId(groupId);
        }

        call.enqueue(new Callback<List<QuestionDTO>>() {
            @Override
            public void onResponse(Call<List<QuestionDTO>> call, Response<List<QuestionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questionList = response.body();
                    if (questionList.isEmpty()) {
                        Toast.makeText(QuizActivity.this, "Không có câu hỏi nào!", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Lưu câu hỏi vào SQLite
                    saveQuestionsToSQLite(questionList);

                    // Khởi tạo giao diện quiz
                    initializeQuiz();
                } else {
                    Toast.makeText(QuizActivity.this, "Lỗi khi lấy dữ liệu: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuestionDTO>> call, Throwable t) {
                Toast.makeText(QuizActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("QuizActivity", "API call failed", t);
            }
        });
    }

    private void initializeQuiz() {
        // Nếu không phải câu hỏi điểm liệt, set titleText từ groupName của câu hỏi đầu tiên
        Intent intent = getIntent();
        boolean isFailingScore = intent.getBooleanExtra("isFailingScore", false);
        if (!isFailingScore && !questionList.isEmpty()) {
            QuestionDTO firstQuestion = questionList.get(0);
            titleText.setText(firstQuestion.getGroupName() != null ? firstQuestion.getGroupName() : "Không có tiêu đề");
        }

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

    private List<QuestionDTO> loadQuestionsFromSQLite(int groupId, boolean isFailingScore) {
        List<QuestionDTO> questions = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String query = "SELECT * FROM Questions WHERE groupId = ? AND failingScore = ?";
        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(groupId), isFailingScore ? "1" : "0"});
        while (cursor.moveToNext()) {
            QuestionDTO question = new QuestionDTO();
            question.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow("text")));
            question.setGroupId(cursor.getInt(cursor.getColumnIndexOrThrow("groupId")));
            question.setFailingScore(cursor.getInt(cursor.getColumnIndexOrThrow("failingScore")) == 1);
            question.setExplainQuestion(cursor.getString(cursor.getColumnIndexOrThrow("explainQuestion")));
            question.setAnswer(cursor.getInt(cursor.getColumnIndexOrThrow("answer")));

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
            if (options.size() >= 1) question.setOption1(options.get(0));
            if (options.size() >= 2) question.setOption2(options.get(1));
            if (options.size() >= 3) question.setOption3(options.get(2));
            if (options.size() >= 4) question.setOption4(options.get(3));

            // Tải đường dẫn ảnh
            String imageQuery = "SELECT imagePath FROM Images WHERE questionId = ?";
            Cursor imageCursor = db.rawQuery(imageQuery, new String[]{String.valueOf(question.getId())});
            if (imageCursor.moveToFirst()) {
                String imagePath = imageCursor.getString(imageCursor.getColumnIndexOrThrow("imagePath"));
                question.setImage(imagePath);
                Log.d("QuizActivity", "Loaded image path from SQLite: " + imagePath + " for question " + question.getId());
            } else {
                Log.w("QuizActivity", "No image found in SQLite for question " + question.getId());
            }
            imageCursor.close();

            questions.add(question);
        }
        cursor.close();
        db.close();
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
            if (cleanImageData.length() < 100) { // Kiểm tra độ dài tối thiểu để tránh dữ liệu không hợp lệ
                Log.w("QuizActivity", "Base64 image data too short for question " + questionId + ": " + cleanImageData);
                return null;
            }

            byte[] decodedBytes = Base64.decode(cleanImageData, Base64.DEFAULT);
            if (decodedBytes == null || decodedBytes.length == 0) {
                Log.w("QuizActivity", "Failed to decode Base64 image for question " + questionId);
                return null;
            }

            // Tạo file
            File imageFile = new File(getFilesDir(), "question_" + questionId + ".jpg");
            FileOutputStream fos = new FileOutputStream(imageFile);
            fos.write(decodedBytes);
            fos.flush();
            fos.close();

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
        } else if (item.getItemId() == R.id.action_logout) {
            SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
            SharedPreferences.Editor editor = appPrefs.edit();
            editor.putString("current_user", "Guest");
            editor.putBoolean("remember_me", false);
            editor.remove("saved_email");
            editor.apply();

            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
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
            boolean isDefaultMode = sharedPreferences.getBoolean("isDefaultMode", true);
            float speed = isDefaultMode ? 1.0f : sharedPreferences.getFloat("speed", 1.0f);
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
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SETTINGS_REQUEST_CODE && resultCode == RESULT_OK) {
            // Đọc lại cài đặt từ SharedPreferences
            boolean isDefaultMode = sharedPreferences.getBoolean("isDefaultMode", true);
            float speed = isDefaultMode ? 1.0f : sharedPreferences.getFloat("speed", 1.0f);

            // Áp dụng cài đặt mới cho TextToSpeech
            if (textToSpeech != null) {
                textToSpeech.setSpeechRate(speed);
                Log.d("QuizActivity", "Speed applied, user: " + currentUser + ", speed: " + speed);
            }
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

        // Khôi phục titleText nếu có categoryName
        if (isFailingScore && categoryName != null) {
            titleText.setText(categoryName);
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
}