package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

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


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private MoviesDbHelper mOpenHelper;

    static UriMatcher buildUriMatcher() {
        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE, MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/#", MOVIE_DETAIL);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*", MOVIES_SORT_BY);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_MOVIE + "/*/*", MOVIES_FAVORITE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER, TRAILER);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "/#", TRAILERS_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEWS_MOVIE);
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        String movie_id;
        switch (sUriMatcher.match(uri)) {
            case MOVIES_SORT_BY:
                sortOrder = MovieEntry.getSortOrderFromMovieSortByUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, null, null, null, null, sortOrder + " DESC");
                break;
            case MOVIES_FAVORITE:
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, MovieEntry.COLUMN_FAVORITE + "=?", new String[]{MovieEntry.FAVORITE_ON_VALUE}, null, null, MovieEntry.COLUMN_POPULARITY + " DESC");
                break;
            case MOVIE_DETAIL:
                movie_id = MovieEntry.getMovieIdFromMovieDetailUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, MovieEntry._ID + "=?", new String[]{movie_id}, null, null, null);
                break;
            case TRAILERS_MOVIE:
                movie_id = TrailerEntry.getMovieIdFromMovieTrailerUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(TrailerEntry.TABLE_NAME, projection, TrailerEntry.COLUMN_MOVIE_ID + "=?", new String[]{movie_id}, null, null, null);
                break;
            case REVIEWS_MOVIE:
                movie_id = ReviewEntry.getMovieIdFromMovieReviewUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(ReviewEntry.TABLE_NAME, projection, ReviewEntry.COLUMN_MOVIE_ID + "=?", new String[]{movie_id}, null, null, null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIES_SORT_BY:
                return MovieEntry.CONTENT_TYPE;
            case MOVIES_FAVORITE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_DETAIL:
                return MovieEntry.CONTENT_ITEM_TYPE;
            case TRAILER:
                return TrailerEntry.CONTENT_TYPE;
            case TRAILERS_MOVIE:
                return TrailerEntry.CONTENT_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_TYPE;
            case REVIEWS_MOVIE:
                return ReviewEntry.CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }


    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id;

        switch (match) {
            case MOVIE:
                _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TRAILER:
                _id = db.insert(TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TrailerEntry.buildMovieTrailerUri(_id); //This won't work but we leave it for now
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case REVIEW:
                _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReviewEntry.buildMovieReviewUri(_id); //This won't work but we leave it for now
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;
        // this makes delete all rows return the number of rows deleted but there is no null where clause here
        if (null == selection) selection = "1";
        switch (match)

        {
            case MOVIE:
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_FAVORITE + "=?", new String[]{MovieEntry.FAVORITE_ON_VALUE});
                break;
            case TRAILER:
                //delete from trailer where movie_id in (select _id from movie where favorite=0);
                rowsDeleted = db.delete(TrailerEntry.TABLE_NAME, TrailerEntry.COLUMN_MOVIE_ID + "in (select " + MovieEntry._ID + " from " + MovieEntry.TABLE_NAME + " where " + MovieEntry.COLUMN_FAVORITE + "=" + MovieEntry.FAVORITE_OFF_VALUE + ")", null);
                break;
            case REVIEW:
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME, ReviewEntry.COLUMN_MOVIE_ID + "in (select " + MovieEntry._ID + " from " + MovieEntry.TABLE_NAME + " where " + MovieEntry.COLUMN_FAVORITE + "=" + MovieEntry.FAVORITE_OFF_VALUE + ")", null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        //This app won't use the update but since we have to implement the method, we will let the class calling the content provider specifying thedifferent parameters.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(TrailerEntry.TABLE_NAME, values, selection,selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsUpdated;
    }
}
