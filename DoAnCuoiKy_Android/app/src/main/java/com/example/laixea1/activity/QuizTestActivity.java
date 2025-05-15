package com.example.laixea1.activity;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import com.example.laixea1.R;
import com.example.laixea1.adapter.TestQuestionNumberAdapter;
import com.example.laixea1.adapter.TestQuestionPagerAdapter;
import com.example.laixea1.api.ApiService;
import com.example.laixea1.api.RetrofitClient;
import com.example.laixea1.database.DatabaseHelper;
import com.example.laixea1.dto.QuestionDTO;
import com.example.laixea1.fragment.TestQuestionFragment;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QuizTestActivity extends BaseActivity implements TestQuestionFragment.OnAnswerSelectedListener {
    private TextView titleText, questionCounter, timerText;
    private ImageButton headerBtnBack, headerBtnForward, footerBtnBack, footerBtnForward, btnUp, btnDown;
    private Button submitButton, retryButton;
    private LinearLayout questionCounterLayout, questionNumberLayout;
    private ViewPager2 questionPager;
    private RecyclerView questionNumberRecyclerView;
    private DatabaseHelper dbHelper;
    private String currentUser;
    private int quizId;
    private String quizName;
    private int remainingTime;
    private CountDownTimer timer;
    private boolean isPaused = false;
    private List<QuestionDTO> questions;
    private TestQuestionPagerAdapter pagerAdapter;
    private TestQuestionNumberAdapter numberAdapter;
    private List<Integer> questionNumbers;
    private int currentQuestionIndex = 0;
    private boolean isCompleted = false;
    private boolean isPassed = false;
    private boolean isReviewMode = false;

    public DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    public boolean isReviewMode() {
        return isReviewMode;
    }

    // Add this method to provide access to TestQuestionNumberAdapter
    public TestQuestionNumberAdapter getTestQuestionNumberAdapter() {
        return numberAdapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_quiz);

        dbHelper = new DatabaseHelper(this);
        SharedPreferences appPrefs = getSharedPreferences("App_Settings", MODE_PRIVATE);
        currentUser = appPrefs.getString("current_user", "Guest");

        titleText = findViewById(R.id.titleText);
        questionCounter = findViewById(R.id.questionCounter);
        timerText = findViewById(R.id.timerText);
        headerBtnBack = findViewById(R.id.headerBtnBack);
        headerBtnForward = findViewById(R.id.headerBtnForward);
        footerBtnBack = findViewById(R.id.footerBtnBack);
        footerBtnForward = findViewById(R.id.footerBtnForward);
        btnUp = findViewById(R.id.btnUp);
        btnDown = findViewById(R.id.btnDown);
        submitButton = findViewById(R.id.submitButton);
        retryButton = findViewById(R.id.retryButton);
        questionCounterLayout = findViewById(R.id.questionCounterLayout);
        questionNumberLayout = findViewById(R.id.questionNumberLayout);
        questionNumberRecyclerView = findViewById(R.id.questionNumberRecyclerView);
        questionPager = findViewById(R.id.questionPager);

        isReviewMode = getIntent().getBooleanExtra("reviewMode", false);
        quizId = getIntent().getIntExtra("quizId", -1);
        quizName = getIntent().getStringExtra("quiz_name");
        Log.d("QuizTestActivity", "Received quizId: " + quizId + ", quizName: " + quizName + ", reviewMode: " + isReviewMode);
        if (quizId == -1 || quizName == null) {
            Toast.makeText(this, "Dữ liệu không hợp lệ", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        titleText.setText(quizName);

        questions = new ArrayList<>();
        questionNumbers = new ArrayList<>();

        pagerAdapter = new TestQuestionPagerAdapter(this, questions, currentUser, quizId, this);
        questionPager.setAdapter(pagerAdapter);
        questionPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentQuestionIndex = position;
                updateQuestionCounter(position);
                if (numberAdapter != null) {
                    numberAdapter.setCurrentQuestionIndex(position);
                }
                if (!isReviewMode) {
                    saveTestProgress();
                }
            }
        });

        questionNumberRecyclerView.setLayoutManager(new GridLayoutManager(this, 5));
        numberAdapter = new TestQuestionNumberAdapter(questionNumbers, currentQuestionIndex, questions, currentUser, quizId, dbHelper,
                position -> {
                    currentQuestionIndex = position;
                    questionPager.setCurrentItem(currentQuestionIndex);
                    questionNumberLayout.setVisibility(View.GONE);
                }, isReviewMode);
        questionNumberRecyclerView.setAdapter(numberAdapter);

        View.OnClickListener navigateListener = v -> {
            int currentItem = questionPager.getCurrentItem();
            if (v.getId() == R.id.headerBtnBack || v.getId() == R.id.footerBtnBack || v.getId() == R.id.btnUp) {
                if (currentItem > 0) questionPager.setCurrentItem(currentItem - 1);
            } else if (v.getId() == R.id.headerBtnForward || v.getId() == R.id.footerBtnForward || v.getId() == R.id.btnDown) {
                if (currentItem < questions.size() - 1) questionPager.setCurrentItem(currentItem + 1);
                else Toast.makeText(QuizTestActivity.this, "Đã đến câu hỏi cuối cùng!", Toast.LENGTH_SHORT).show();
            }
        };
        headerBtnBack.setOnClickListener(navigateListener);
        headerBtnForward.setOnClickListener(navigateListener);
        footerBtnBack.setOnClickListener(navigateListener);
        footerBtnForward.setOnClickListener(navigateListener);
        btnUp.setOnClickListener(navigateListener);
        btnDown.setOnClickListener(navigateListener);

        questionCounterLayout.setOnClickListener(v -> {
            questionNumberLayout.setVisibility(
                    questionNumberLayout.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE
            );
        });

        submitButton.setOnClickListener(v -> submitQuiz());
        retryButton.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Xác nhận làm lại")
                    .setMessage("Bạn có muốn xóa tiến trình hiện tại và làm lại bài thi không?")
                    .setPositiveButton("Có", (dialog, which) -> {
                        resetTestProgress();
                        Toast.makeText(this, "Đã reset bài thi, bắt đầu làm lại", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Không", null)
                    .show();
        });

        loadTestProgress();
        updateUI();
        loadQuestionsFromSQLite();
        if (questions.isEmpty()) {
            fetchQuestions();
        } else {
            initializeQuiz();
        }
    }

    private void updateUI() {
        submitButton.setEnabled(!isReviewMode);
        retryButton.setVisibility(isReviewMode ? View.VISIBLE : View.GONE);
        if (isReviewMode) {
            timerText.setText("00:00");
        }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private void loadQuestionsFromSQLite() {
        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT * FROM TestQuestions WHERE testId = ?";
            cursor = db.rawQuery(query, new String[]{String.valueOf(quizId)});
            Log.d("QuizTestActivity", "Cursor count: " + cursor.getCount());
            while (cursor.moveToNext()) {
                QuestionDTO question = new QuestionDTO();
                try {
                    question.setId(cursor.getInt(cursor.getColumnIndexOrThrow("questionId")));
                    question.setQuestion(cursor.getString(cursor.getColumnIndexOrThrow("questionText")));
                    question.setOption1(cursor.getString(cursor.getColumnIndexOrThrow("option1")));
                    question.setOption2(cursor.getString(cursor.getColumnIndexOrThrow("option2")));
                    question.setOption3(cursor.getString(cursor.getColumnIndexOrThrow("option3")));
                    question.setOption4(cursor.getString(cursor.getColumnIndexOrThrow("option4")));
                    question.setAnswer(cursor.getInt(cursor.getColumnIndexOrThrow("answer")));
                    question.setExplainQuestion(cursor.getString(cursor.getColumnIndexOrThrow("explainQuestion")));
                    question.setFailingScore(cursor.getInt(cursor.getColumnIndexOrThrow("failingScore")) == 1);
                    String imagePath = cursor.getString(cursor.getColumnIndexOrThrow("image"));
                    question.setImage(imagePath);
                    Log.d("QuizTestActivity", "Loaded question ID: " + question.getId() + ", Image: " + imagePath);
                    questions.add(question);
                } catch (Exception e) {
                    Log.e("QuizTestActivity", "Error processing question ID: " + cursor.getInt(cursor.getColumnIndexOrThrow("questionId")), e);
                }
            }
        } catch (Exception e) {
            Log.e("QuizTestActivity", "loadQuestionsFromSQLite: " + e.getMessage(), e);
        } finally {
            if (cursor != null) cursor.close();
            if (db != null && db.isOpen()) db.close();
        }
        Log.d("QuizTestActivity", "Loaded " + questions.size() + " questions from SQLite, quizId: " + quizId);
    }

    private void fetchQuestions() {
        if (!isNetworkAvailable()) {
            Toast.makeText(QuizTestActivity.this, "Không có kết nối mạng!", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();
        Call<List<QuestionDTO>> call = apiService.getQuestionsByTopic(quizId);

        call.enqueue(new Callback<List<QuestionDTO>>() {
            @Override
            public void onResponse(Call<List<QuestionDTO>> call, Response<List<QuestionDTO>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    questions.clear();
                    questions.addAll(response.body());
                    for (QuestionDTO question : questions) {
                        String image = question.getImage();
                        Log.d("QuizTestActivity", "Question " + question.getId() + " Base64 length: " +
                                (image != null ? image.length() : 0) + ", starts with data:image: " +
                                (image != null && image.startsWith("data:image")));
                    }
                    Log.d("QuizTestActivity", "Questions from API: " + questions.size());
                    saveQuestionsToSQLite();
                    initializeQuiz();
                } else {
                    Log.e("QuizTestActivity", "Response code: " + response.code() + ", Message: " + response.message());
                    Toast.makeText(QuizTestActivity.this, "Không thể tải câu hỏi: " + response.message(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<QuestionDTO>> call, Throwable t) {
                Log.e("QuizTestActivity", "API call failed: " + t.getMessage());
                Toast.makeText(QuizTestActivity.this, "Lỗi kết nối: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveQuestionsToSQLite() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            db.beginTransaction();
            for (QuestionDTO question : questions) {
                ContentValues values = new ContentValues();
                values.put("testId", quizId);
                values.put("questionId", question.getId());
                values.put("questionText", question.getQuestion());
                values.put("option1", question.getOption1());
                values.put("option2", question.getOption2());
                values.put("option3", question.getOption3());
                values.put("option4", question.getOption4());
                int adjustedAnswer = question.getAnswer() - 1;
                if (adjustedAnswer < 0 || adjustedAnswer > 3) {
                    Log.e("QuizTestActivity", "Invalid answer for question " + question.getId() + ": " + question.getAnswer());
                    adjustedAnswer = 0;
                }
                values.put("answer", adjustedAnswer);
                values.put("explainQuestion", question.getExplainQuestion());
                values.put("failingScore", question.isFailingScore() ? 1 : 0);

                String imagePath = question.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    imagePath = saveImageToFile(imagePath, question.getId());
                    Log.d("QuizTestActivity", "Saved image path for question " + question.getId() + ": " + imagePath);
                } else {
                    imagePath = null;
                }
                values.put("image", imagePath);
                question.setImage(imagePath);

                String selection = "testId = ? AND questionId = ?";
                String[] selectionArgs = {String.valueOf(quizId), String.valueOf(question.getId())};
                long count = db.update("TestQuestions", values, selection, selectionArgs);

                if (count == 0) {
                    db.insert("TestQuestions", null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error saving questions to database: " + e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
                db.close();
            }
        }
    }

    private String saveImageToFile(String base64Image, int questionId) {
        try {
            if (base64Image == null || base64Image.isEmpty()) {
                Log.w("QuizTestActivity", "Base64 image is null or empty for question " + questionId);
                return null;
            }

            String cleanImageData = base64Image.trim();
            if (cleanImageData.startsWith("data:image")) {
                cleanImageData = cleanImageData.replaceFirst("^data:image/[^;]+;base64,", "");
            }

            if (!isValidBase64(cleanImageData)) {
                Log.e("QuizTestActivity", "Invalid Base64 string for question " + questionId);
                return null;
            }

            int mod = cleanImageData.length() % 4;
            if (mod > 0) {
                cleanImageData += "====".substring(mod);
                Log.d("QuizTestActivity", "Added padding to Base64 for question " + questionId);
            }

            byte[] decodedBytes;
            try {
                decodedBytes = Base64.decode(cleanImageData, Base64.DEFAULT);
            } catch (IllegalArgumentException e) {
                Log.e("QuizTestActivity", "Base64 decode failed for question " + questionId + ": " + e.getMessage());
                return null;
            }

            if (decodedBytes == null || decodedBytes.length == 0) {
                Log.w("QuizTestActivity", "Decoded Base64 data is empty for question " + questionId);
                return null;
            }

            File imageFile = new File(getFilesDir(), "question_" + questionId + ".jpg");
            try (FileOutputStream fos = new FileOutputStream(imageFile)) {
                fos.write(decodedBytes);
                fos.flush();
            } catch (Exception e) {
                Log.e("QuizTestActivity", "Error writing image file for question " + questionId + ": " + e.getMessage());
                return null;
            }

            if (imageFile.exists() && imageFile.length() > 0) {
                Log.d("QuizTestActivity", "Image saved successfully: " + imageFile.getAbsolutePath());
                return imageFile.getAbsolutePath();
            } else {
                Log.w("QuizTestActivity", "Image file not created or empty for question " + questionId);
                return null;
            }
        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error saving image for question " + questionId + ": " + e.getMessage());
            return null;
        }
    }

    private boolean isValidBase64(String base64) {
        if (base64 == null || base64.isEmpty()) return false;
        return base64.matches("^[A-Za-z0-9+/=]+$");
    }

    private void initializeQuiz() {
        questionNumbers.clear();
        for (int i = 0; i < questions.size(); i++) {
            questionNumbers.add(i + 1);
        }
        pagerAdapter.notifyDataSetChanged();
        numberAdapter.notifyDataSetChanged();
        updateQuestionCounter(questionPager.getCurrentItem());
        if (!isReviewMode) {
            startTimer();
        } else {
            timerText.setText("00:00");
        }
    }

    private void loadTestProgress() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT remainingTime, isPaused, completedQuestions, isCompleted, isPassed FROM TestProgress WHERE userId = ? AND testId = ?";
            Cursor cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(quizId)});
            if (cursor != null && cursor.moveToFirst()) {
                int remainingTimeIndex = cursor.getColumnIndex("remainingTime");
                int isPausedIndex = cursor.getColumnIndex("isPaused");
                int completedQuestionsIndex = cursor.getColumnIndex("completedQuestions");
                int isCompletedIndex = cursor.getColumnIndex("isCompleted");
                int isPassedIndex = cursor.getColumnIndex("isPassed");
                if (remainingTimeIndex >= 0 && isPausedIndex >= 0 && completedQuestionsIndex >= 0 &&
                        isCompletedIndex >= 0 && isPassedIndex >= 0) {
                    remainingTime = cursor.getInt(remainingTimeIndex);
                    isPaused = cursor.getInt(isPausedIndex) == 1;
                    currentQuestionIndex = cursor.getInt(completedQuestionsIndex);
                    isCompleted = cursor.getInt(isCompletedIndex) == 1;
                    isPassed = cursor.getInt(isPassedIndex) == 1;
                    if (currentQuestionIndex >= questions.size()) {
                        currentQuestionIndex = 0;
                    }
                }
            } else {
                remainingTime = 20 * 60;
                isCompleted = false;
                isPassed = false;
            }
            if (cursor != null) {
                cursor.close();
            }
        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error loading test progress: " + e.getMessage());
            remainingTime = 20 * 60;
            isCompleted = false;
            isPassed = false;
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private void startTimer() {
        if (timer != null) {
            timer.cancel();
        }
        if (isCompleted || isReviewMode) {
            timerText.setText("00:00");
            return;
        }
        timer = new CountDownTimer(remainingTime * 1000L, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                remainingTime = (int) (millisUntilFinished / 1000);
                int minutes = remainingTime / 60;
                int seconds = remainingTime % 60;
                timerText.setText(String.format("%02d:%02d", minutes, seconds));
                saveTestProgress();
            }

            @Override
            public void onFinish() {
                timerText.setText("00:00");
                Toast.makeText(QuizTestActivity.this, "Hết thời gian!", Toast.LENGTH_SHORT).show();
                submitQuiz();
            }
        }.start();
    }

    private void saveTestProgress() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put("userId", currentUser);
            values.put("testId", quizId);
            values.put("remainingTime", remainingTime);
            values.put("isPaused", isPaused ? 1 : 0);
            values.put("completedQuestions", currentQuestionIndex);
            values.put("isCompleted", isCompleted ? 1 : 0);
            values.put("isPassed", isPassed ? 1 : 0);

            String selection = "userId = ? AND testId = ?";
            String[] selectionArgs = {currentUser, String.valueOf(quizId)};
            long count = db.update("TestProgress", values, selection, selectionArgs);

            if (count == 0) {
                db.insert("TestProgress", null, values);
            }
        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error saving test progress: " + e.getMessage());
        } finally {
            if (db != null) {
                db.close();
            }
        }
    }

    private void submitQuiz() {
        if (timer != null) {
            timer.cancel();
        }

        int correctAnswers = 0;
        boolean hasFailingScoreError = false;
        final int TOTAL_QUESTIONS = 25;
        final int PASSING_SCORE = 21;

        SQLiteDatabase db = null;
        Cursor cursor = null;
        try {
            db = dbHelper.getReadableDatabase();
            String query = "SELECT uta.questionId, uta.selectedAnswer, tq.answer, tq.failingScore " +
                    "FROM UserTestAnswers uta " +
                    "INNER JOIN TestQuestions tq ON uta.questionId = tq.questionId " +
                    "WHERE uta.userId = ? AND uta.testId = ? AND tq.testId = ?";
            cursor = db.rawQuery(query, new String[]{currentUser, String.valueOf(quizId), String.valueOf(quizId)});

            while (cursor.moveToNext()) {
                int questionId = cursor.getInt(cursor.getColumnIndexOrThrow("questionId"));
                int selectedAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("selectedAnswer"));
                int correctAnswer = cursor.getInt(cursor.getColumnIndexOrThrow("answer"));
                boolean isFailingScore = cursor.getInt(cursor.getColumnIndexOrThrow("failingScore")) == 1;

                Log.d("QuizTestActivity", "Question " + questionId + ": Selected=" + selectedAnswer +
                        ", Correct=" + correctAnswer + ", FailingScore=" + isFailingScore);

                if (selectedAnswer == correctAnswer) {
                    correctAnswers++;
                }
                if (isFailingScore && selectedAnswer != correctAnswer && selectedAnswer != 0) {
                    hasFailingScoreError = true;
                }
            }

            isPassed = correctAnswers >= PASSING_SCORE && !hasFailingScoreError;
            isCompleted = true;
            saveTestProgress();

            String message;
            if (isPassed) {
                message = "Đạt! Bạn đúng " + correctAnswers + "/" + TOTAL_QUESTIONS + " câu.";
            } else {
                if (hasFailingScoreError) {
                    message = "Không đạt! Bạn sai câu điểm liệt.";
                } else {
                    message = "Không đạt! Bạn đúng " + correctAnswers + "/" + TOTAL_QUESTIONS +
                            " câu, cần ít nhất " + PASSING_SCORE + " câu.";
                }
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Kết quả bài thi");
            builder.setMessage(message);
            builder.setPositiveButton("OK", (dialog, which) -> finish());
            builder.setCancelable(false);
            builder.show();

            Log.d("QuizTestActivity", "Result: Correct=" + correctAnswers + ", HasFailingScoreError=" +
                    hasFailingScoreError + ", Passed=" + isPassed);

        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error calculating quiz result: " + e.getMessage(), e);
            Toast.makeText(this, "Lỗi khi chấm điểm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            if (db != null && db.isOpen()) {
                db.close();
            }
        }
    }

    private void resetTestProgress() {
        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            String whereClause = "userId = ? AND testId = ?";
            String[] whereArgs = {currentUser, String.valueOf(quizId)};
            db.delete("TestProgress", whereClause, whereArgs);
            db.delete("UserTestAnswers", whereClause, whereArgs);
            Log.d("QuizTestActivity", "Deleted TestProgress and UserTestAnswers for quizId=" + quizId + ", userId=" + currentUser);
        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error resetting test progress: " + e.getMessage(), e);
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        isReviewMode = false;
        isCompleted = false;
        isPassed = false;
        isPaused = false;
        remainingTime = 20 * 60;
        currentQuestionIndex = 0;

        pagerAdapter = new TestQuestionPagerAdapter(this, questions, currentUser, quizId, this);
        questionPager.setAdapter(pagerAdapter);
        questionPager.setCurrentItem(0);
        pagerAdapter.notifyDataSetChanged();

        questionNumbers.clear();
        for (int i = 0; i < questions.size(); i++) {
            questionNumbers.add(i + 1);
        }
        numberAdapter = new TestQuestionNumberAdapter(questionNumbers, currentQuestionIndex, questions, currentUser, quizId, dbHelper,
                position -> {
                    currentQuestionIndex = position;
                    questionPager.setCurrentItem(currentQuestionIndex);
                    questionNumberLayout.setVisibility(View.GONE);
                }, isReviewMode);
        questionNumberRecyclerView.setAdapter(numberAdapter);
        numberAdapter.notifyDataSetChanged();

        updateUI();
        startTimer();
        updateQuestionCounter(0);
    }

    @Override
    public void onAnswerSelected(int questionId, int selectedAnswer) {
        if (isReviewMode) {
            return;
        }
        Log.d("QuizTestActivity", "Answer selected: questionId=" + questionId + ", selectedAnswer=" + selectedAnswer);

        SQLiteDatabase db = null;
        try {
            db = dbHelper.getWritableDatabase();
            if (selectedAnswer >= 0) {
                ContentValues values = new ContentValues();
                values.put("userId", currentUser);
                values.put("testId", quizId);
                values.put("questionId", questionId);
                values.put("selectedAnswer", selectedAnswer);
                values.put("isCorrect", 0);
                values.put("timestamp", System.currentTimeMillis());

                String selection = "userId = ? AND testId = ? AND questionId = ?";
                String[] selectionArgs = {currentUser, String.valueOf(quizId), String.valueOf(questionId)};
                long count = db.update("UserTestAnswers", values, selection, selectionArgs);

                if (count == 0) {
                    db.insert("UserTestAnswers", null, values);
                    Log.d("QuizTestActivity", "Inserted new answer for questionId=" + questionId + ", selectedAnswer=" + selectedAnswer);
                } else {
                    Log.d("QuizTestActivity", "Updated answer for questionId=" + questionId + ", selectedAnswer=" + selectedAnswer);
                }
            } else {
                String selection = "userId = ? AND testId = ? AND questionId = ?";
                String[] selectionArgs = {currentUser, String.valueOf(quizId), String.valueOf(questionId)};
                db.delete("UserTestAnswers", selection, selectionArgs);
                Log.d("QuizTestActivity", "Deleted answer for questionId=" + questionId);
            }
        } catch (Exception e) {
            Log.e("QuizTestActivity", "Error saving user answer: " + e.getMessage());
        } finally {
            if (db != null && db.isOpen()) {
                db.close();
            }
        }

        if (numberAdapter != null) {
            numberAdapter.notifyDataSetChanged();
            Log.d("QuizTestActivity", "Notified TestQuestionNumberAdapter for questionId=" + questionId);
        }
        saveTestProgress();
    }

    private void updateQuestionCounter(int position) {
        questionCounter.setText("Câu hỏi " + (position + 1) + "/" + questions.size());
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (!isCompleted && !isReviewMode) {
            isPaused = true;
            saveTestProgress();
            if (timer != null) {
                timer.cancel();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isCompleted && !isReviewMode) {
            isPaused = false;
            loadTestProgress();
            if (!questions.isEmpty()) {
                questionPager.setCurrentItem(currentQuestionIndex);
                startTimer();
            }
        } else {
            timerText.setText("00:00");
        }
        updateUI();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }
        if (dbHelper != null) {
            dbHelper.close();
        }
    }
}