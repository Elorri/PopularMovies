package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;

/**
 * Manages a local database for movies data.
 */
public class MoviesDbHelper extends SQLiteOpenHelper {


    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_MOVIE_TABLE="CREATE TABLE "+MovieEntry.TABLE_NAME+"("+MovieEntry._ID+" INTEGER NOT NULL, "+MovieEntry.COLUMN_TITLE+" TEXT NOT NULL,  "+MovieEntry.COLUMN_DURATION+" INTEGER,   "+MovieEntry.COLUMN_RELEASE_DATE+" INTEGER NOT NULL,   "+MovieEntry.COLUMN_POSTER_PATH+" TEXT NOT NULL ,  "+MovieEntry.COLUMN_PLOT_SYNOPSIS+" TEXT NOT NULL,   "+MovieEntry.COLUMN_RATE+" REAL NOT NULL ,  "+MovieEntry.COLUMN_POPULARITY+" TEXT NOT NULL,   "+MovieEntry.COLUMN_FAVORITE+" INTEGER NOT NULL,   PRIMARY KEY( "+MovieEntry._ID+" ) ON CONFLICT REPLACE); " ;
        final String SQL_CREATE_TRAILER_TABLE="CREATE TABLE "+ReviewEntry.TABLE_NAME+" (   "+ReviewEntry._ID+" TEXT NOT NULL ,  "+ReviewEntry.COLUMN_AUTHOR+" TEXT NOT NULL ,  "+ReviewEntry.COLUMN_CONTENT+" TEXT NOT NULL,   "+ReviewEntry.COLUMN_MOVIE_ID+" INTEGER NOT NULL,   FOREIGN KEY( "+ReviewEntry.COLUMN_MOVIE_ID+" )   REFERENCES "+MovieEntry.TABLE_NAME+"( "+MovieEntry._ID+" ),   PRIMARY KEY( "+ReviewEntry._ID+" ) ON CONFLICT REPLACE); " ;
        final String SQL_CREATE_REVIEW_TABLE="CREATE TABLE "+TrailerEntry.TABLE_NAME+" (   "+TrailerEntry._ID+" TEXT NOT NULL ,  "+TrailerEntry.COLUMN_KEY+" TEXT NOT NULL UNIQUE ,  "+TrailerEntry.COLUMN_NAME+" TEXT NOT NULL ,  "+TrailerEntry.COLUMN_TYPE+" TEXT NOT NULL,   "+TrailerEntry.COLUMN_MOVIE_ID+" INTEGER NOT NULL,   FOREIGN KEY( "+TrailerEntry.COLUMN_MOVIE_ID+" )   REFERENCES "+MovieEntry.TABLE_NAME+"( "+MovieEntry._ID+" ),   PRIMARY KEY( "+TrailerEntry._ID+" ) ON CONFLICT REPLACE,  UNIQUE ( "+TrailerEntry.COLUMN_KEY+" ) ON CONFLICT REPLACE); "  ;

        db.execSQL(SQL_CREATE_MOVIE_TABLE);
        db.execSQL(SQL_CREATE_TRAILER_TABLE);
        db.execSQL(SQL_CREATE_REVIEW_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME);
        onCreate(db);
    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        if (!db.isReadOnly()) {
            // Enable foreign key constraints
            db.execSQL("PRAGMA foreign_keys=ON;");
        }
    }
}
