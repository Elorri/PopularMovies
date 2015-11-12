package com.example.android.popularmovies.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MergeCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.android.popularmovies.DetailAdapter;
import com.example.android.popularmovies.Utility;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Created by Elorri on 27/10/2015.
 */
public class MovieProvider extends ContentProvider {

    private static final String[] MOVIE_COLUMNS = {
            MovieEntry._ID,
            MovieEntry.COLUMN_TITLE,
            MovieEntry.COLUMN_DURATION,
            MovieEntry.COLUMN_RELEASE_DATE,
            MovieEntry.COLUMN_POSTER_PATH,
            MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieEntry.COLUMN_RATE,
            MovieEntry.COLUMN_POPULARITY,
            MovieEntry.COLUMN_FAVORITE
    };

    public static final int COL_ID = 0;
    public static final int COL_TITLE = 1;
    public static final int COL_DURATION = 2;
    public static final int COL_RELEASE_DATE = 3;
    public static final int COL_POSTER_PATH = 4;
    public static final int COL_PLOT_SYNOPSIS = 5;
    public static final int COL_RATE = 6;
    public static final int COL_POPULARITY = 7;
    public  static final int COL_FAVORITE = 8;

    private static final String[] TRAILER_COLUMNS = {
            TrailerEntry._ID,
            TrailerEntry.COLUMN_KEY,
            TrailerEntry.COLUMN_NAME,
            TrailerEntry.COLUMN_TYPE,
            TrailerEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to TRAILER_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    public static final int TRAILER_ID = 0;
    public static final int COL_KEY = 1;
    public static final int COL_NAME = 2;
    public static final int COL_TYPE = 3;
    public static final int COL_MOVIE_ID_T = 4;


    private static final String[] REVIEWS_COLUMNS = {
            ReviewEntry._ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT,
            ReviewEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to REVIEWS_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    public  static final int REVIEWS_ID = 0;
    public static final int COL_AUTHOR = 1;
    public  static final int COL_CONTENT = 2;
    public  static final int COL_MOVIE_ID_R = 3;


    static final int MOVIE = 100;
    //will match content://com.example.android.popularmovies/movie/ (directory)
    static final int MOVIES_SORT_BY = 101;
    //will match content://com.example.android.popularmovies/movie/popularity.desc (directory)
    //will match content://com.example.android.popularmovies/movie/rate.desc (directory)
    static final int MOVIES_FAVORITE = 102;
    //will match content://com.example.android.popularmovies/movie/favorite/popularity.desc (directory)
    static final int MOVIE_DETAIL = 103;
    //will match content://com.example.android.popularmovies/movie/135399  (item)
    static final int TRAILER = 200;
    //will match content://com.example.android.popularmovies/trailer/ (directory)
    static final int TRAILERS_MOVIE = 201;
    //will match content://com.example.android.popularmovies/trailer/135399/ (directory)
    static final int TRAILER_DETAIL = 202;
    //will match content://com.example.android.popularmovies/trailer/559198cac3a3685710000b58 (item)
    static final int REVIEW = 300;
    //will match content://com.example.android.popularmovies/review/ (directory)
    static final int REVIEWS_MOVIE = 301;
    //will match content://com.example.android.popularmovies/review/135399/ (directory)
    static final int REVIEW_DETAIL = 302;
    //will match content://com.example.android.popularmovies/review/75660928c3a3687ad7002db (item)
    static final int TRAILERS_REVIEWS_MOVIE = 400;
    //will match content://com.example.android.popularmovies/trailer.review/135399/ (item)


    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private static final String LOG_TAG = MovieProvider.class.getSimpleName();
    private MoviesDbHelper mOpenHelper;

    //This will be helpful to determine our DetailAdapter item type
    public static int CURSOR_TYPE_COUNT = 3;

    public interface Callback {
        void onDetailCursorMerged(int[] mCursorsCount);
    }

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
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "/*", TRAILER_DETAIL);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW, REVIEW);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/#", REVIEWS_MOVIE);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_REVIEW + "/*", REVIEW_DETAIL);
        matcher.addURI(MovieContract.CONTENT_AUTHORITY, MovieContract.PATH_TRAILER + "" +
                "." + MovieContract.PATH_REVIEW + "/*", TRAILERS_REVIEWS_MOVIE);

        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : UriMatcher :  object created");
        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new MoviesDbHelper(getContext());
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : MoviesDbHelper :  object created");
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : MoviesProvider.mOpenHelper :  change state");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor;
        String movieId;
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case MOVIES_SORT_BY:
                sortOrder = MovieEntry.getSortOrderFromMovieSortByUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, null, null, null, null, sortOrder + " DESC");
                break;
            case MOVIES_FAVORITE:
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, MovieEntry.COLUMN_FAVORITE + "=?", new String[]{MovieEntry.FAVORITE_ON_VALUE}, null, null, MovieEntry.COLUMN_POPULARITY + " DESC");
                break;
            case MOVIE_DETAIL:
                movieId = MovieEntry.getMovieIdFromMovieDetailUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(MovieEntry.TABLE_NAME, projection, MovieEntry._ID + "=?", new String[]{movieId}, null, null, null);
                break;
            case TRAILER:
                cursor = mOpenHelper.getReadableDatabase().query(TrailerEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case TRAILERS_MOVIE:
                movieId = TrailerEntry.getMovieIdFromMovieTrailerUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(TrailerEntry.TABLE_NAME, projection, TrailerEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId}, null, null, TrailerEntry.COLUMN_NAME + " desc");
                break;
            case REVIEW:
                cursor = mOpenHelper.getReadableDatabase().query(ReviewEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case REVIEWS_MOVIE:
                movieId = ReviewEntry.getMovieIdFromMovieReviewUri(uri);
                cursor = mOpenHelper.getReadableDatabase().query(ReviewEntry.TABLE_NAME, projection, ReviewEntry.COLUMN_MOVIE_ID + "=?", new String[]{movieId}, null, null, null);
                break;
            case TRAILERS_REVIEWS_MOVIE:
                movieId = MovieEntry.getMovieIdFromMovieTrailersReviewsUri(uri);

                Cursor[] cursors = new Cursor[CURSOR_TYPE_COUNT];
                int[] cursorsCount = new int[CURSOR_TYPE_COUNT];
                cursors[0] = query(
                        MovieEntry.buildMovieDetailUri(Long.valueOf(movieId)),
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null);
                cursorsCount[0]=cursors[0].getCount();
                cursors[1] = query(
                        TrailerEntry.buildMovieTrailerUri(Long.valueOf(movieId)),
                        TRAILER_COLUMNS,
                        null,
                        null,
                        null);
                cursorsCount[1]=cursors[1].getCount();
                cursors[2] = query(
                        ReviewEntry.buildMovieReviewUri(Long.valueOf(movieId)),
                        REVIEWS_COLUMNS,
                        null,
                        null,
                        null);
                cursorsCount[2]=cursors[2].getCount();
                 cursor = new MergeCursor(cursors);

                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                        .thread() + " : query cursor :  object created");
                Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                        .thread() + " : query cursorCount[] :  object created");

                 DetailAdapter.getInstance(getContext(), null,0, null ).onDetailCursorMerged(cursorsCount);

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
            case TRAILER_DETAIL:
                return TrailerEntry.CONTENT_ITEM_TYPE;
            case REVIEW:
                return ReviewEntry.CONTENT_TYPE;
            case REVIEWS_MOVIE:
                return ReviewEntry.CONTENT_TYPE;
            case REVIEW_DETAIL:
                return ReviewEntry.CONTENT_ITEM_TYPE;
            case TRAILERS_REVIEWS_MOVIE:
                return MovieEntry.CONTENT_ITEM_TYPE;
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
                values = createCorrectContentValue(db, values);
                _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieEntry.buildMovieDetailUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case TRAILER:
                _id = db.insert(TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = TrailerEntry.buildTrailerDetailUri(Long.toString(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            case REVIEW:
                _id = db.insert(ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = ReviewEntry.buildReviewDetailUri(Long.toString(_id));
                else
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : insert uri :  object created");
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
                //delete from movie where favorite=0;
                //db.execSQL("delete from movie where favorite=0;");
                //rowsDeleted=0;
                rowsDeleted = db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_FAVORITE + "=?", new String[]{MovieEntry.FAVORITE_OFF_VALUE});
                break;
            case TRAILER:
                //db.execSQL("delete from trailer where movie_id in (select _id from movie where favorite=0);");
                //rowsDeleted=0;
                //delete from trailer where movie_id in (select _id from movie where favorite=0);
                rowsDeleted = db.delete(TrailerEntry.TABLE_NAME, TrailerEntry.COLUMN_MOVIE_ID + " in (select " + MovieEntry._ID + " from " + MovieEntry.TABLE_NAME + " where " + MovieEntry.COLUMN_FAVORITE + "=" + MovieEntry.FAVORITE_OFF_VALUE + ")", null);
                break;
            case REVIEW:
                //db.execSQL("delete from review where movie_id in (select _id from movie where favorite=0);");
                //rowsDeleted=0;
                //delete from review where movie_id in (select _id from movie where favorite=0);
                rowsDeleted = db.delete(ReviewEntry.TABLE_NAME, ReviewEntry.COLUMN_MOVIE_ID + " in (select " + MovieEntry._ID + " from " + MovieEntry.TABLE_NAME + " where " + MovieEntry.COLUMN_FAVORITE + "=" + MovieEntry.FAVORITE_OFF_VALUE + ")", null);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : delete nb rowsDeleted :  object created");
        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // The only update our app will do is switch the favorite to 1 or 0. Since only the
        // class which call the ContentProvider knows the value selectionArgs, we let this class
        // fill all arguments.
        //This app won't use others updates but since we have to implement the method, we will
        // let the class calling the content provider specifying the different parameters.
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case MOVIE:
                rowsUpdated = db.update(MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
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
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : delete nb rowsUpdated :  object created");
        return rowsUpdated;
    }


    //This bulk insert is better than the default, because we use only one transaction for all inserts.
    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case MOVIE:
                returnCount = insertInBulk(MovieEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILER:
                returnCount = insertInBulk(TrailerEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                returnCount = insertInBulk(ReviewEntry.TABLE_NAME, values);
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }

    private int insertInBulk(String tableName, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        db.beginTransaction();
        int returnCount = 0;
        try {
            for (ContentValues value : values) {
                value = createCorrectContentValue(db, value);
                long _id = db.insert(tableName, null, value);
                if (_id != -1) {
                    returnCount++;
                }
            }
            db.setTransactionSuccessful();
            Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                    .thread() + " : delete nb rowsInserted :  object created");
            return returnCount;
        } finally {
            db.endTransaction();
        }
    }

    private ContentValues createCorrectContentValue(SQLiteDatabase writableDb, ContentValues
            value) {
        //Check if the movie already exist and is a favorite
        Cursor cursor = writableDb.query(MovieEntry.TABLE_NAME, new String[]{MovieEntry
                        .COLUMN_FAVORITE},
                MovieEntry._ID + "=? and " + MovieEntry.COLUMN_FAVORITE + "=?",
                new String[]{value.get(MovieEntry._ID).toString(), MovieEntry
                        .FAVORITE_ON_VALUE}, null,
                null,
                null);
        if (cursor.moveToFirst()) {//Set favorite on on the record we want to insert
            value.put(MovieEntry.COLUMN_FAVORITE, MovieEntry.FAVORITE_ON_VALUE);
            Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                    .thread() + " : ContentValues change to favorite :  state change");
        }
        return value;
    }

    // You do not need to call this method. This is a method specifically to assist the testing
    // framework in running smoothly. You can read more at:
    // http://developer.android.com/reference/android/content/ContentProvider.html#shutdown()
    @Override
    @TargetApi(11)
    public void shutdown() {
        mOpenHelper.close();
        super.shutdown();
    }


}
