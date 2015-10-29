package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.example.android.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;

/**
 * Created by Elorri on 26/10/2015.
 */
public class TestUtilities extends AndroidTestCase {

    private static final Long MOVIE_ID_FAVORITE = 135399l;
    private static final Long MOVIE_ID = 135400l;


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
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, "43.57727");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        return movieValues;
    }

    public static ContentValues createTrailerValuesFavorite() {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry._ID, "559198cac3a3685710000b58");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "FRDdRto_3SA");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Trailers From Hell");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Featurette");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, MOVIE_ID_FAVORITE);
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


    public static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Jurassic World");
        movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, 120);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, 189978989);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/jjBgi2r5cRt36xF6iNUEhzscEcb.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, "Twenty-two years after the events of Jurassic Park, Isla Nublar now features a fully functioning dinosaur theme park, Jurassic World, as originally envisioned by John Hammond.");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, 6.9);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, "43.57727");
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 0);
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

}
