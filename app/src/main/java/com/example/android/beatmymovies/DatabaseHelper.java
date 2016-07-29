package com.example.android.beatmymovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Admin on 07-01-2016.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    private SQLiteDatabase myDataBase;
    private DatabaseHelper mDbHelper;

    private final Context mCtx;

    public DatabaseHelper(Context context) {
        super(context, "favouriteMovies", null, 1);
        this.mCtx = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.i("Database Create:", "Creating DataBase Connection....");
        db.execSQL("CREATE TABLE IF NOT EXISTS favouriteMovies ("
                + BaseColumns._ID
                + " INTEGER PRIMARY KEY AUTOINCREMENT, poster_path VARCHAR, original_title VARCHAR, overview VARCHAR, " +
                "vote_count VARCHAR, release_date VARCHAR, id INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public long insertRecord(String s, String s1, String s2, String s3, String s4, int s5) {

        long d = 0;
        ContentValues values = new ContentValues();

        values.put("poster_path", s);
        values.put("original_title", s1);
        values.put("overview", s2);
        values.put("vote_count", s3);
        values.put("release_date", s4);
        values.put("id", s5);

        try {
            d = myDataBase.insert("favouriteMovies", null, values);
        } catch (Exception e) {

        }
        return d;
    }

    public DatabaseHelper open() throws SQLException {
        Log.i("Database Open:", "OPening DataBase Connection....");
        mDbHelper = new DatabaseHelper(mCtx);
        myDataBase = mDbHelper.getWritableDatabase();
        onCreate(myDataBase);
        return this;
    }


    public ArrayList<MovieForGrid> getAllMovies() {
        List<MovieForGrid> contactList = new ArrayList<MovieForGrid>();
        // Select All Query
        String selectQuery = "SELECT  * FROM " + "favouriteMovies";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                String id = cursor.getString(6);
                MovieForGrid contact = new MovieForGrid(cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getString(5), Integer.parseInt(id));
                // Adding contact to list
                contactList.add(contact);
            } while (cursor.moveToNext());
        }

        // return contact list
        return (ArrayList<MovieForGrid>) contactList;

    }
}