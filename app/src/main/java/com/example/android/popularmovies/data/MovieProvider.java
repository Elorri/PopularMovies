package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.Nullable;

/**
 * Created by Elorri on 27/10/2015.
 */
public class MovieProvider extends ContentProvider {

    static final int MOVIE = 100;
    //will match content://com.example.android.popularmovies/movie/
    static final int MOVIES_SORT_BY = 101;
    //will match content://com.example.android.popularmovies/movie/popularity.desc
    //will match content://com.example.android.popularmovies/movie/rate.desc
    static final int MOVIES_FAVORITE = 102;
    //will match content://com.example.android.popularmovies/movie/favorite/popularity.desc
    static final int MOVIE_DETAIL = 103;
    //will match content://com.example.android.popularmovies/movie/135399
    static final int TRAILER = 200;
    //will match content://com.example.android.popularmovies/trailer/
    static final int TRAILERS_MOVIE = 201;
    //will match content://com.example.android.popularmovies/trailer/135399/
    static final int REVIEW = 300;
    //will match content://com.example.android.popularmovies/review/
    static final int REVIEWS_MOVIE = 301;
    //will match content://com.example.android.popularmovies/review/135399/

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE+"/#", MOVIE_DETAIL);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE+"/*", MOVIES_SORT_BY);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE+"/*/*", MOVIES_FAVORITE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER+"/#", TRAILERS_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW+"/#", REVIEWS_MOVIE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        return false;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }
}
