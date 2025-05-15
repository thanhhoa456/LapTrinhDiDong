package com.example.laixea1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuizApp.db";
    private static final int DATABASE_VERSION = 15; // Incremented version for new table

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Bảng từ DatabaseHelper (QuizApp)
        db.execSQL("CREATE TABLE Questions (" +
                "id INTEGER PRIMARY KEY, " +
                "text TEXT, " +
                "groupId INTEGER, " +
                "failingScore BOOLEAN, " +
                "explainQuestion TEXT, " +
                "answer INTEGER)");

        db.execSQL("CREATE TABLE Answers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "questionId INTEGER, " +
                "text TEXT, " +
                "isCorrect BOOLEAN, " +
                "FOREIGN KEY (questionId) REFERENCES Questions(id))");

        db.execSQL("CREATE TABLE UserProgress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "questionId INTEGER, " +
                "selectedAnswer INTEGER, " +
                "isCorrect BOOLEAN, " +
                "timestamp LONG, " +
                "FOREIGN KEY (questionId) REFERENCES Questions(id))");

        db.execSQL("CREATE TABLE Images (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "questionId INTEGER, " +
                "imagePath TEXT, " +
                "FOREIGN KEY (questionId) REFERENCES Questions(id))");

        // New table for RetakeProgress
        db.execSQL("CREATE TABLE RetakeProgress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "questionId INTEGER, " +
                "selectedAnswer INTEGER, " +
                "isCorrect BOOLEAN, " +
                "timestamp LONG, " +
                "FOREIGN KEY (questionId) REFERENCES Questions(id))");

        db.execSQL("CREATE INDEX idx_answers_questionId ON Answers(questionId)");
        db.execSQL("CREATE INDEX idx_userprogress_questionId ON UserProgress(questionId)");
        db.execSQL("CREATE INDEX idx_images_questionId ON Images(questionId)");
        db.execSQL("CREATE INDEX idx_retakeprogress_questionId ON RetakeProgress(questionId)");

        // Bảng từ TestDatabaseHelper (TestQuizApp)
        db.execSQL("CREATE TABLE Tests (" +
                "id INTEGER PRIMARY KEY, " +
                "name TEXT, " +
                "totalQuestions INTEGER, " +
                "duration INTEGER)");

        db.execSQL("CREATE TABLE TestQuestions (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "testId INTEGER, " +
                "questionId INTEGER, " +
                "questionText TEXT, " +
                "option1 TEXT, " +
                "option2 TEXT, " +
                "option3 TEXT, " +
                "option4 TEXT, " +
                "answer INTEGER, " +
                "image TEXT, " +
                "explainQuestion TEXT, " +
                "failingScore INTEGER, " +
                "FOREIGN KEY (testId) REFERENCES Tests(id))");

        db.execSQL("CREATE TABLE TestProgress (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "testId INTEGER, " +
                "remainingTime INTEGER, " +
                "isPaused INTEGER, " +
                "completedQuestions INTEGER, " +
                "isCompleted INTEGER DEFAULT 0, " +
                "isPassed INTEGER DEFAULT 0, " +
                "FOREIGN KEY (testId) REFERENCES Tests(id))");

        db.execSQL("CREATE TABLE UserTestAnswers (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "userId TEXT, " +
                "testId INTEGER, " +
                "questionId INTEGER, " +
                "selectedAnswer INTEGER, " +
                "isCorrect INTEGER, " +
                "timestamp LONG, " +
                "FOREIGN KEY (testId) REFERENCES Tests(id), " +
                "FOREIGN KEY (questionId) REFERENCES TestQuestions(id))");

        db.execSQL("CREATE TABLE UserTestRetakeWrong (" +
                "userId TEXT, " +
                "testId INTEGER, " +
                "questionId INTEGER, " +
                "selectedAnswer INTEGER, " +
                "isCorrect INTEGER, " +
                "timestamp INTEGER, " +
                "retakeAttempt INTEGER, " +
                "PRIMARY KEY (userId, testId, questionId, retakeAttempt))");

        db.execSQL("CREATE INDEX idx_testquestions_testId ON TestQuestions(testId)");
        db.execSQL("CREATE INDEX idx_testprogress_testId ON TestProgress(testId)");
        db.execSQL("CREATE INDEX idx_usertestanswers_testId ON UserTestAnswers(testId)");
        db.execSQL("CREATE INDEX idx_usertestanswers_questionId ON UserTestAnswers(questionId)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop all tables
        db.execSQL("DROP TABLE IF EXISTS Questions");
        db.execSQL("DROP TABLE IF EXISTS Answers");
        db.execSQL("DROP TABLE IF EXISTS UserProgress");
        db.execSQL("DROP TABLE IF EXISTS Images");
        db.execSQL("DROP TABLE IF EXISTS RetakeProgress");
        db.execSQL("DROP TABLE IF EXISTS Tests");
        db.execSQL("DROP TABLE IF EXISTS TestQuestions");
        db.execSQL("DROP TABLE IF EXISTS TestProgress");
        db.execSQL("DROP TABLE IF EXISTS UserTestAnswers");
        db.execSQL("DROP TABLE IF EXISTS UserTestRetakeWrong");

        // Recreate tables
        onCreate(db);
    }
}