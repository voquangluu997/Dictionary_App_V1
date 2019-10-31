package com.example.dictionaryv1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import java.util.ArrayList;
import java.util.List;

public class DatabaseAccess {
    private SQLiteOpenHelper openHelper;
    private SQLiteDatabase database;
    private static DatabaseAccess instance;
    private  int getMaxId;

    private DatabaseAccess(Context context) {
        this.openHelper = new DatabaseOpenHelper(context);
    }

    public static DatabaseAccess getInstance(Context context) {
        if (instance == null) {
            instance = new DatabaseAccess(context);
        }
        return instance;
    }

    public void open() {
        this.database = openHelper.getWritableDatabase();
    }

    public void close() {
        if (database != null) {
            this.database.close();
        }
    }

    public List<String> getWords() {
        List<String> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM anh_viet", null);
        cursor.moveToFirst();
        int i=0;
        while (!cursor.isAfterLast()&&i<500) {
            list.add(cursor.getString(1));
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return list;
    }

    // method get definition òf a word
    public String getDefinition(String word) {
        String definition = "";
        Cursor cursor = database.rawQuery("Select*from anh_viet where word ='" + word + "'", null);
        cursor.moveToFirst();
        definition = cursor.getString(2);
        cursor.close();
        return definition;
    }

    public List<Word> getAllWordEV(int loadMore) {

        List<Word> list = new ArrayList<>();
        Cursor cursor = database.rawQuery("SELECT * FROM anh_viet", null);
        int i=loadMore;
        cursor.moveToPosition(i);
        while (!cursor.isAfterLast()&&i<loadMore+20) {
            Word word = new Word();
            word.setWord(cursor.getString(1));
            word.setContent(getDefinition(cursor.getString(1)));
            list.add(word);
            cursor.moveToNext();
            i++;
        }
        cursor.close();
        return list;
    }


}