package com.example.laixea1.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "QuizApp.db";
    private static final int DATABASE_VERSION = 3;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Questions (" +
                "id INTEGER PRIMARY KEY, " +
                "text TEXT, " +
                "groupId INTEGER, " +
                "failingScore BOOLEAN, " + // Thay isCritical thành failingScore
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

        // Thêm index để tối ưu truy vấn
        db.execSQL("CREATE INDEX idx_answers_questionId ON Answers(questionId)");
        db.execSQL("CREATE INDEX idx_userprogress_questionId ON UserProgress(questionId)");
        db.execSQL("CREATE INDEX idx_images_questionId ON Images(questionId)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS Questions");
        db.execSQL("DROP TABLE IF EXISTS Answers");
        db.execSQL("DROP TABLE IF EXISTS UserProgress");
        db.execSQL("DROP TABLE IF EXISTS Images");
        onCreate(db);
    }
}