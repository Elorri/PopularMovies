package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;
import android.util.Log;

import com.example.android.popularmovies.Utility;
import com.example.android.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Elorri on 26/10/2015.
 */
public class TestUtilities extends AndroidTestCase {

    private static final Long MOVIE_ID_FAVORITE = 135399l;
    private static final Long MOVIE_ID = 135400l;
    private static final String LOG_TAG = "PopularMovies";



    private static final String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_DURATION,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_RATE,
            MovieContract.MovieEntry.COLUMN_POPULARITY,
            MovieContract.MovieEntry.COLUMN_FAVORITE
    };

    static final int MOVIE_ID_IDX = 0;
    static final int COL_TITLE = 1;
    static final int COL_DURATION = 2;
    static final int COL_RELEASE_DATE = 3;
    static final int COL_POSTER_PATH = 4;
    static final int COL_PLOT_SYNOPSIS = 5;
    static final int COL_RATE = 6;
    static final int COL_POPULARITY = 7;
    static final int COL_FAVORITE = 8;


    private static final String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_TYPE,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to TRAILER_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    static final int TRAILER_ID = 0;
    static final int COL_KEY = 1;
    static final int COL_NAME = 2;
    static final int COL_TYPE = 3;
    static final int COL_MOVIE_ID_T = 4;


    private static final String[] REVIEWS_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to REVIEWS_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    static final int REVIEWS_ID = 0;
    static final int COL_AUTHOR = 1;
    static final int COL_CONTENT = 2;
    static final int COL_MOVIE_ID_R = 3;



    static void validateCursor(String error, Cursor valueCursor, ContentValues expectedValues) {
        assertTrue("Empty cursor returned. " + error, valueCursor.moveToFirst());
        validateCurrentRecord(error, valueCursor, expectedValues);
        valueCursor.close();
    }


    static void validateCurrentRecord(String error, Cursor valueCursor, ContentValues expectedValues) {
        Set<Map.Entry<String, Object>> valueSet = expectedValues.valueSet();
        for (Map.Entry<String, Object> entry : valueSet) {
            String columnName = entry.getKey();
            int idx = valueCursor.getColumnIndex(columnName);
            assertFalse("Column '" + columnName + "' not found. " + error, idx == -1);
            String expectedValue = entry.getValue().toString();
            assertEquals("Value '" + entry.getValue().toString() +
                    "' did not match the expected value '" +
                    expectedValue + "'. " + error, expectedValue, valueCursor.getString(idx));
        }
    }

    public static ContentValues createMovieValuesFavorite() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, MOVIE_ID_FAVORITE);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, 120);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, 189978989);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, 6.9);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, "43.5773");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        return movieValues;
    }

    public static ContentValues createTrailerValuesFavorite() {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry._ID, "559198cac3a3685710000b58");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "FRDdRto_3SA");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Trailers From Hell");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Featurette");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, 135400l);
        return trailerValues;
    }

    public static ContentValues createReviewValuesFavorite() {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry._ID, "55660928c3a3687ad7001db");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "Phileas Fogg5");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Fabulous action movie. Lots of interesting characters. They don''t make many movies like this. The whole movie from start to finish was entertaining I''m looking forward to seeing it again. I definitely recommend seeing it.");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, MOVIE_ID_FAVORITE);
        return reviewValues;
    }


    public static ContentValues createMovieValuesSameId() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, 120);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, 189978989);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, 6.9);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, "43.5773");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
        return movieValues;
    }

    public static ContentValues createMovieValuesSameIdFavorite() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, 120);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, 189978989);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, 6.9);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, "43.5773");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        return movieValues;
    }


    public static ContentValues createTrailerValues() {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry._ID, "659198cac3a3685710000b58");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "DRDdRto_3SA");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Trailers From Hell");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Featurette");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        return trailerValues;
    }

    public static ContentValues createReviewValues() {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry._ID, "75660928c3a3687ad7001db");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "Phileas Fogg5");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Fabulous action movie. Lots of interesting characters. They don''t make many movies like this. The whole movie from start to finish was entertaining I''m looking forward to seeing it again. I definitely recommend seeing it.");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        return reviewValues;
    }


    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }

    public static ContentValues[] createBulkInsertMoviesValues() {
        ContentValues[] values = new ContentValues[2];
        values[0] = createMovieValuesSameId();
        values[1] = createMovieValuesFavorite();
        return values;
    }


    /*
      The functions we provide inside of TestProvider use this utility class to test
      the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
      CTS Compatibility Test Suite tests.
      Note that this only tests that the onChange function is called; it does not test that the
      correct Uri is returned.
   */
    static class TestContentObserver extends ContentObserver {
        final HandlerThread mHT;
        boolean mContentChanged;

        static TestContentObserver getTestContentObserver() {
            HandlerThread ht = new HandlerThread("ContentObserverThread");
            ht.start();
            return new TestContentObserver(ht);
        }

        private TestContentObserver(HandlerThread ht) {
            super(new Handler(ht.getLooper()));
            mHT = ht;
        }

        // On earlier versions of Android, this onChange method is called
        @Override
        public void onChange(boolean selfChange) {
            onChange(selfChange, null);
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            mContentChanged = true;
        }

        public void waitForNotificationOrFail() {
            // Note: The PollingCheck class is taken from the Android CTS (Compatibility Test Suite).
            // It's useful to look at the Android CTS source for ideas on how to test your Android
            // applications.  The reason that PollingCheck works is that, by default, the JUnit
            // testing framework is not running on the main Android application thread.
            new PollingCheck(5000) {
                @Override
                protected boolean check() {
                    return mContentChanged;
                }
            }.run();
            mHT.quit();
        }
    }

    public static String parseId(Uri contentUri) {
        String last = contentUri.getLastPathSegment();
        if (last == null) return "-1";
        else return last;
    }


    public static void echo(String s) {
        int length = s.length();

        for (int i = 0; i < length; i += 3000) {
            if (i + 3000 < length)
                Log.e(LOG_TAG, s.substring(i, i + 3000));
            else
                Log.e(LOG_TAG, s.substring(i, length));
        }
    }


    public static void testGetShortString() {
        final String A_LONG_STRING = "azertyuiopqsdfghjklmwxcvbn";
        final String A_SHORT_STRING = "azerty";

        assertEquals("azertyuiopqsdfghj...", Utility.getShortString(A_LONG_STRING, 20));
        assertEquals("azerty", Utility.getShortString(A_SHORT_STRING, 20));
    }



    //Useful method to help import the data in a local db for testing. Android studio free version doesn't allow to visualize db file.
    public void testShowMovieTable() {

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );


        int i = 0;
        while (cursor.moveToNext()) {
            Log.e(LOG_TAG, i + ("$" + cursor.getInt(MOVIE_ID_IDX) + "|" + cursor.getString(COL_TITLE) + "|" + cursor.getInt(COL_DURATION) + "|" + cursor.getInt(COL_RELEASE_DATE) + "|" + cursor.getString(COL_POSTER_PATH) + "|" + cursor.getString(COL_PLOT_SYNOPSIS) + "|" + cursor.getDouble(COL_RATE) + "|" + cursor.getString(COL_POPULARITY)
                    + "|" + cursor.getInt(COL_FAVORITE)));
            i++;
        }
        Log.e(LOG_TAG, i + "record displayed");
    }

    //Useful method to help import the data in a local db for testing. Android studio free version doesn't allow to visualize db file.
    public void testShowMovieSortByQuery() {
        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.buildMoviesSortByUri(MovieContract.MovieEntry.FAVORITE_RATE),
                MOVIE_COLUMNS,
                null,
                null,
                null
        );
        int i = 0;
        while (cursor.moveToNext()) {
            Log.e(LOG_TAG, i + ("$" + cursor.getInt(MOVIE_ID_IDX) + "|" + cursor.getString(COL_TITLE) + "|" + cursor.getInt(COL_DURATION) + "|" + cursor.getInt(COL_RELEASE_DATE) + "|" + cursor.getString(COL_POSTER_PATH) + "|" + cursor.getString(COL_PLOT_SYNOPSIS) + "|" + cursor.getDouble(COL_RATE) + "|" + cursor.getString(COL_POPULARITY)
                    + "|" + cursor.getInt(COL_FAVORITE)));
            i++;
        }
        Log.e(LOG_TAG, i + "record displayed");
    }


    public void testShowReviewTable() {
        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                REVIEWS_COLUMNS,
                null,
                null,
                null
        );
        int i = 0;
        while (cursor.moveToNext()) {
            Log.e(LOG_TAG, i + ("$" + cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry._ID)) + "|" + cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR)) + "|" + cursor.getString(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT)) + "|" + cursor.getInt(cursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_MOVIE_ID))));
            i++;
        }
        Log.e(LOG_TAG, i + "record displayed");
    }


    public void testShowTrailerTable() {
        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                TRAILER_COLUMNS,
                null,
                null,
                null
        );
        int i = 0;
        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, i + ("$" + cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry._ID))
                    + "|" + cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_KEY)) + "|" + cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME)) + "|" + cursor.getString(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TYPE)) + "|" + cursor.getInt(cursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_MOVIE_ID))));
            i++;
        }
        Log.d(LOG_TAG, i + "record displayed");
    }


}
