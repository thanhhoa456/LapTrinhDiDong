package com.example.laixea1.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
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
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.entity.Answer;
import com.example.laixea1.fragment.QuestionFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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
    private Map<Integer, QuestionDTO> questionCache = new HashMap<>();
    private Map<Integer, List<Answer>> answerCache = new HashMap<>();
    private List<Integer> questionNumbers = new ArrayList<>();

    // TextToSpeech components
    private TextToSpeech textToSpeech;
    private SharedPreferences sharedPreferences;
    private static final int SETTINGS_REQUEST_CODE = 100;
    private String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

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
        boolean isCritical = intent.getBooleanExtra("isCritical", false);
        String categoryName = intent.getStringExtra("category_name");

        // Nếu là câu hỏi điểm liệt, set titleText ngay lập tức
        if (isCritical && categoryName != null) {
            titleText.setText(categoryName);
        }

        // Load danh sách câu hỏi
        loadQuestions(groupId, isCritical);
    }

    @Override
    public void onAnswerSelected() {
        // Cập nhật QuestionNumberAdapter khi answerCache thay đổi
        if (questionNumberAdapter != null) {
            int position = questionPager.getCurrentItem();
            if (position < questionList.size()) {
                int questionId = questionList.get(position).getId();
                Log.d("QuizActivity", "Updating QuestionNumberAdapter for position: " + position + ", questionId: " + questionId);
                questionNumberAdapter.notifyItemChanged(position);

                // Debug answerCache
                Log.d("QuizActivity", "answerCache after onAnswerSelected: ");
                for (Map.Entry<Integer, List<Answer>> entry : answerCache.entrySet()) {
                    Log.d("QuizActivity", "Question ID: " + entry.getKey() + ", Answers: " + entry.getValue().size());
                    for (Answer answer : entry.getValue()) {
                        Log.d("QuizActivity", "Answer: " + answer.getText() + ", isSelected: " + answer.isSelected());
                    }
                }
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
                List<Integer> questionNumbers = new ArrayList<>();
                for (int i = 0; i < questionList.size(); i++) {
                    questionNumbers.add(i + 1);
                }
                questionNumberAdapter = new QuestionNumberAdapter(questionNumbers, currentQuestionIndex, questionList, answerCache, position -> {
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

    private void loadQuestions(int groupId, boolean isCritical) {
        ApiService apiService = RetrofitClient.getApiService();
        Call<List<QuestionDTO>> call;

        if (isCritical) {
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

                    // Lưu tất cả câu hỏi vào cache với key là id của câu hỏi
                    for (QuestionDTO question : questionList) {
                        questionCache.put(question.getId(), question);
                    }

                    // Debug questionList
                    Log.d("QuizActivity", "Loaded questionList: ");
                    for (int i = 0; i < questionList.size(); i++) {
                        Log.d("QuizActivity", "Position: " + i + ", Question ID: " + questionList.get(i).getId());
                    }

                    // Nếu không phải câu hỏi điểm liệt, set titleText từ groupName của câu hỏi đầu tiên
                    if (!isCritical && !questionList.isEmpty()) {
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

                    // Khởi tạo QuestionNumberAdapter sau khi questionList được cập nhật
                    questionNumberAdapter = new QuestionNumberAdapter(questionNumbers, currentQuestionIndex, questionList, answerCache, position -> {
                        currentQuestionIndex = position;
                        questionPager.setCurrentItem(currentQuestionIndex);
                        questionNumberLayout.setVisibility(View.GONE);
                    });
                    questionNumberRecyclerView.setAdapter(questionNumberAdapter);

                    // Thiết lập ViewPager2
                    pagerAdapter = new QuestionPagerAdapter(QuizActivity.this, questionList, answerCache, QuizActivity.this);
                    questionPager.setAdapter(pagerAdapter);
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
}