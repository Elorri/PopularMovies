package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.test.AndroidTestCase;

import java.util.Map;
import java.util.Set;

/**
 * Created by Elorri on 26/10/2015.
 */
public class TestUtilities extends AndroidTestCase {

    private static final Long MOVIE_ID = 135399l;


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
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, 1);
        return movieValues;
    }

    public static ContentValues createTrailerValues() {
        ContentValues trailerValues = new ContentValues();
        trailerValues.put(MovieContract.TrailerEntry._ID, "559198cac3a3685710000b58");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "FRDdRto_3SA");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Trailers From Hell");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Featurette");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        return trailerValues;
    }

    public static ContentValues createReviewValues() {
        ContentValues reviewValues = new ContentValues();
        reviewValues.put(MovieContract.ReviewEntry._ID, "55660928c3a3687ad7001db");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "Phileas Fogg5");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Fabulous action movie. Lots of interesting characters. They don''t make many movies like this. The whole movie from start to finish was entertaining I''m looking forward to seeing it again. I definitely recommend seeing it.");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, MOVIE_ID);
        return reviewValues;

    }


}
