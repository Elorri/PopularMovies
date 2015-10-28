package com.example.android.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;
/**
 * Created by Elorri on 27/10/2015.
 */
public class TestProvider extends AndroidTestCase {
    private static final Long MOVIE_ID = 135399l;
    public static final String SORT_BY_VALUE="popularity.desc";



    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(),MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: with MovieProvider registered authority: ", providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at "+ mContext.getPackageName(),false);
        }
    }

    public void testGetType() {
        //content://com.example.android.popularmovies/movie/popularity.desc/ (directory)
        //content://com.example.android.popularmovies/movie/rate.desc/ (directory)
        String type = mContext.getContentResolver().getType(MovieEntry.buildMovieSortByUri("popularity.desc"));
        assertEquals("Error: ", MovieEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/movie/favorite/popularity.desc/ (directory)
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieFavoriteUri());
        assertEquals("Error: ", MovieEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/movie/135399 (item)
        type = mContext.getContentResolver().getType(MovieEntry.buildMovieDetailUri(135399L));
        assertEquals("Error: ", MovieEntry.CONTENT_ITEM_TYPE, type);

        //content://com.example.android.popularmovies/trailer/135399/ (directory)
        type = mContext.getContentResolver().getType(TrailerEntry.buildMovieTrailerUri(135399L));
        assertEquals("Error: ", TrailerEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/review/135399/ (directory)
        type = mContext.getContentResolver().getType(ReviewEntry.buildMovieReviewUri(135399L));
        assertEquals("Error: ", ReviewEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/movie/ (directory)
        type = mContext.getContentResolver().getType(MovieEntry.CONTENT_URI);
        assertEquals("Error: ", MovieEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/trailer/ (directory)
        type = mContext.getContentResolver().getType(TrailerEntry.CONTENT_URI);
        assertEquals("Error: ", TrailerEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/review/ (directory)
        type = mContext.getContentResolver().getType(ReviewEntry.CONTENT_URI);
        assertEquals("Error: ", ReviewEntry.CONTENT_TYPE, type);
    }


    public void testMovieSortByQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        long rowId = db.insert(MovieEntry.TABLE_NAME, null,movieValues );
        assertTrue("Unable to Insert MovieEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieSortByUri(SORT_BY_VALUE),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testMovieSortByQuery", cursor, movieValues);
    }


    public void testMovieFavoriteQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Note that COLUMN_FAVORITE equals 1 in createMovieValues() otherwise this test won't pass.
        ContentValues movieValues = TestUtilities.createMovieValues();
        long rowId = db.insert(MovieEntry.TABLE_NAME, null,movieValues );
        assertTrue("Unable to Insert MovieEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieFavoriteUri(),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testMovieFavoriteQuery", cursor, movieValues);
    }

    public void testMovieDetailQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues movieValues = TestUtilities.createMovieValues();
        long rowId = db.insert(MovieEntry.TABLE_NAME, null,movieValues );
        assertTrue("Unable to Insert MovieEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieDetailUri(MOVIE_ID),
                null,
                null,
                null,
                null
        );
        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testMovieDetailQuery", cursor, movieValues);
    }

    public void testTrailerMovieQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues trailerValues = TestUtilities.createTrailerValues();
        long rowId = db.insert(TrailerEntry.TABLE_NAME, null,trailerValues );
        assertTrue("Unable to Insert TrailerEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                TrailerEntry.buildMovieTrailerUri(MOVIE_ID),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testTrailerMovieQuery", cursor, trailerValues);
    }

    public void testReviewMovieQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues reviewValues = TestUtilities.createReviewValues();
        long rowId = db.insert(ReviewEntry.TABLE_NAME, null,reviewValues );
        assertTrue("Unable to Insert ReviewEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.buildMovieReviewUri(MOVIE_ID),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testReviewMovieQuery", cursor, reviewValues);
    }


    public void testMovieInsertProvider() {
        ContentValues movieValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);

        // Did our content observer get called? If this fails, your insert method
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long rowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Data's inserted.  Now pull some out to stare at it and verify it made the round trip.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieEntry.buildMovieFavoriteUri(),
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor(" Error :",  movieCursor, movieValues);


    }

    public void testReviewInsertProvider() {

        ContentValues reviewValues = TestUtilities.createReviewValues();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);
        Uri reviewUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewUri != null);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.buildMovieReviewUri(MOVIE_ID),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("Error:", reviewCursor, reviewValues);
    }

    public void testTrailerInsertProvider() {
        ContentValues trailerValues = TestUtilities.createTrailerValues();
        // The TestContentObserver is a one-shot class
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(TrailerEntry.CONTENT_URI, true, tco);
        Uri trailerUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI, trailerValues);
        assertTrue(trailerUri != null);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = mContext.getContentResolver().query(
                TrailerEntry.buildMovieTrailerUri(MOVIE_ID),  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("Error:", trailerCursor, trailerValues);
    }
}
