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
import android.util.Log;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Created by Elorri on 27/10/2015.
 */
public class TestProvider extends AndroidTestCase {
    private static final Long MOVIE_ID = 135399l;
    public static final String SORT_BY_VALUE = "popularity.desc";
    private static final int BULK_INSERT_RECORDS_TO_INSERT = 2;
    private static final String LOG_TAG = "PopularMovies";


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
            TrailerEntry._ID,
            TrailerEntry.COLUMN_KEY,
            TrailerEntry.COLUMN_NAME,
            TrailerEntry.COLUMN_TYPE,
            TrailerEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to TRAILER_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    static final int TRAILER_ID = 0;
    static final int COL_KEY = 1;
    static final int COL_NAME = 2;
    static final int COL_TYPE = 3;
    static final int COL_MOVIE_ID_T = 4;


    private static final String[] REVIEWS_COLUMNS = {
            ReviewEntry._ID,
            ReviewEntry.COLUMN_AUTHOR,
            ReviewEntry.COLUMN_CONTENT,
            ReviewEntry.COLUMN_MOVIE_ID
    };

    // These indices are tied to REVIEWS_COLUMNS.  If MOVIE_COLUMNS changes, these
// must change.
    static final int REVIEWS_ID = 0;
    static final int COL_AUTHOR = 1;
    static final int COL_CONTENT = 2;
    static final int COL_MOVIE_ID_R = 3;


    /*
   This helper function deletes all records from database tables using the database
   functions only.  This is designed to be used to reset the state of the database until the
   delete functionality is available in the ContentProvider.
   I have chosen to make my Provider delete fonction delete only non favorite reccords, hence I will keep using this deleteAllRecordsFromDB fonction to clear the db.
 */
    public void deleteAllRecordsFromDB() {
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        db.delete(TrailerEntry.TABLE_NAME, null, null);
        db.delete(ReviewEntry.TABLE_NAME, null, null);
        db.delete(MovieEntry.TABLE_NAME, null, null);
        db.close();
    }

    /*
        Student: Refactor this function to use the deleteAllRecordsFromProvider functionality once
        you have implemented delete functionality there.
     */
    public void deleteAllRecords() {
        deleteAllRecordsFromDB();
    }

    // Since we want each test to start with a clean slate, run deleteAllRecords
    // in setUp (called by the test runner before each test).
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecords();
    }


    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();
        ComponentName componentName = new ComponentName(mContext.getPackageName(), MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: with MovieProvider registered authority: ", providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: MovieProvider not registered at " + mContext.getPackageName(), false);
        }
    }

    public void testGetType() {
        //content://com.example.android.popularmovies/movie/popularity.desc/ (directory)
        //content://com.example.android.popularmovies/movie/rate.desc/ (directory)
        String type = mContext.getContentResolver().getType(MovieEntry.buildMoviesSortByUri("popularity.desc"));
        assertEquals("Error: ", MovieEntry.CONTENT_TYPE, type);

        //content://com.example.android.popularmovies/movie/favorite/popularity.desc/ (directory)
        type = mContext.getContentResolver().getType(MovieEntry.buildMoviesFavoriteUri());
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

        ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
        long rowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue("Unable to Insert MovieEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildMoviesSortByUri(SORT_BY_VALUE),
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testMovieSortByQuery", cursor, movieValues);
    }

public void testInsertFavoriteQuery(){
    // insert our test records into the database
    MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
    SQLiteDatabase db = dbHelper.getWritableDatabase();

    //Note that COLUMN_FAVORITE equals 1 in createMovieValuesFavorite() otherwise this test won't pass.
    ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
    long rowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
    assertTrue("Unable to Insert MovieEntry into the Database", rowId != -1);

    // Test the sort_by content provider query
//    Cursor cursor = mContext.getContentResolver().query(
//            MovieEntry.buildMoviesFavoriteUri(),
//            null,
//            null,
//            null,
//            null
//    );

    Cursor cursor=db.query(MovieEntry.TABLE_NAME, null,
            MovieEntry._ID+"=? and "+MovieEntry.COLUMN_FAVORITE+"=?",
            new String[]{movieValues.get(MovieEntry._ID).toString(), MovieEntry.FAVORITE_ON_VALUE}, null,
            null,
            null);

    // Make sure we get the correct cursor out of the database
    TestUtilities.validateCursor("testInsertFavoriteQuery", cursor, movieValues);
db.close();
}
    public void testMovieFavoriteQuery() {
        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        //Note that COLUMN_FAVORITE equals 1 in createMovieValuesFavorite() otherwise this test won't pass.
        ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
        long rowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
        assertTrue("Unable to Insert MovieEntry into the Database", rowId != -1);
        db.close();

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.buildMoviesFavoriteUri(),
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

        ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
        long rowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);
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
        testMovieInsert();

        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues trailerValues = TestUtilities.createTrailerValuesFavorite();
        long rowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);
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
        testMovieInsert();

        // insert our test records into the database
        MoviesDbHelper dbHelper = new MoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues reviewValues = TestUtilities.createReviewValuesFavorite();
        long rowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);
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


    public void testMovieInsert() {
        ContentValues movieValues = TestUtilities.createMovieValuesSameIdFavorite();

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
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor(" Error :", movieCursor, movieValues);


    }


    public void testMovieExistingInsert() {
        // We add a Movie favorite
        testMovieInsert();

        Log.e("PopularMovies", "Movie Table before 2nd insert");
        testShowMovieTable();

        //We try to add the same movie, but this one is not set as favorite (movies we get when
        // we sync)
        ContentValues movieValues = TestUtilities.createMovieValuesSameId();

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
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        Log.e("PopularMovies", "Movie Table after 2nd insert");
        testShowMovieTable();

        //The cursor should have a favorite set to 1
        TestUtilities.validateCursor(" Error :", movieCursor, TestUtilities.createMovieValuesSameIdFavorite());

    }

    public void testReviewInsert() {
        testMovieInsert();

        ContentValues reviewValues = TestUtilities.createReviewValuesFavorite();

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(ReviewEntry.CONTENT_URI, true, tco);
        Uri reviewUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, reviewValues);
        assertTrue(reviewUri != null);

        // Did our content observer get called?
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("Error:", reviewCursor, reviewValues);
    }

    public void testTrailerInsert() {
        testMovieInsert();

        ContentValues trailerValues = TestUtilities.createTrailerValuesFavorite();
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
                TrailerEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("Error:", trailerCursor, trailerValues);
    }

    // Our Provider only delete non favorite record
    public void testDeleteMovieRecord() {

        //Add 1 record which is not a favorite
        ContentValues movieValues = TestUtilities.createMovieValuesSameId();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);

        //delete the record
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        //Query the table
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check if the record has been deleted.
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());
        cursor.close();
    }

    public void testDeleteMovieFavoriteRecord() {

        //Add 1 record which is not a favorite
        ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);

        //delete the record
        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );

        //Query the table
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check if the record favorite has not been deleted as expected.
        assertEquals("Error: Records not deleted from Movie table during delete", 1, cursor.getCount());
        cursor.close();
    }

    public void testDeleteTrailerRecord() {
        //Add 1 record which is not a favorite
        ContentValues movieValues = TestUtilities.createMovieValuesSameId();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);


        //Add 1 record which is not a favorite
        ContentValues trailerValues = TestUtilities.createTrailerValues();
        Uri trailerUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI, trailerValues);
        String rowIdStr = TestUtilities.parseId(trailerUri);
        //Check if the record has been correctly added
        assertTrue(!rowIdStr.equals("-1"));

        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

    }


    public void testDeleteTrailerFavoriteRecord() {
        //Add 1 record which is not a favorite
        ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);


        //Add 1 record which is not a favorite
        ContentValues trailerValues = TestUtilities.createTrailerValuesFavorite();
        Uri trailerUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI, trailerValues);
        String rowIdStr = TestUtilities.parseId(trailerUri);
        //Check if the record has been correctly added
        assertTrue(!rowIdStr.equals("-1"));

        mContext.getContentResolver().delete(
                TrailerEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check that the record is not deleted because it is linked to a favorite movie
        assertEquals("Error: Records not deleted from Trailer table during delete", 1, cursor.getCount());
        cursor.close();

    }


    public void testDeleteReviewRecord() {
        //Add 1 record which is not a favorite
        ContentValues movieValues = TestUtilities.createMovieValuesSameId();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);


        //Add 1 record which is not a favorite
        ContentValues reviewValues = TestUtilities.createReviewValues();
        Uri reviewUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, reviewValues);
        String rowIdStr = TestUtilities.parseId(reviewUri);
        //Check if the record has been correctly added
        assertTrue(!rowIdStr.equals("-1"));

        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());
        cursor.close();

    }


    public void testDeleteReviewFavoriteRecord() {
        //Add 1 record which is not a favorite
        ContentValues movieValues = TestUtilities.createMovieValuesFavorite();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);


        //Add 1 record which is not a favorite
        ContentValues reviewValues = TestUtilities.createReviewValuesFavorite();
        Uri reviewUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, reviewValues);
        String rowIdStr = TestUtilities.parseId(reviewUri);
        //Check if the record has been correctly added
        assertTrue(!rowIdStr.equals("-1"));

        mContext.getContentResolver().delete(
                ReviewEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check that the record is not deleted because it is linked to a favorite movie
        assertEquals("Error: Records not deleted from Review table during delete", 1, cursor.getCount());
        cursor.close();

    }

    public void testDeleteMovieReviewTrailerRecordOnCascade() {
        //Add 1 movie non favorite
        ContentValues movieValues = TestUtilities.createMovieValuesSameId();
        Uri movieUri = mContext.getContentResolver().insert(MovieEntry.CONTENT_URI, movieValues);
        long rowId = ContentUris.parseId(movieUri);
        //Check if the record has been correctly added
        assertTrue(rowId != -1);


        //Add 1 review linked to the non favorite movie
        ContentValues reviewValues = TestUtilities.createReviewValues();
        Uri reviewUri = mContext.getContentResolver().insert(ReviewEntry.CONTENT_URI, reviewValues);
        String rowIdStr = TestUtilities.parseId(reviewUri);
        //Check if the record has been correctly added
        assertTrue(!rowIdStr.equals("-1"));


        //Add 1 trailer linked to the non favorite movie
        ContentValues trailerValues = TestUtilities.createTrailerValues();
        Uri trailerUri = mContext.getContentResolver().insert(TrailerEntry.CONTENT_URI, trailerValues);
        rowIdStr = TestUtilities.parseId(trailerUri);
        //Check if the record has been correctly added
        assertTrue(!rowIdStr.equals("-1"));

        mContext.getContentResolver().delete(
                MovieEntry.CONTENT_URI,
                null,
                null
        );
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check that the record is not deleted because it is linked to a favorite movie
        assertEquals("Error: Records not deleted from Movie table during delete", 0, cursor.getCount());

        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check that the record is not deleted because it is linked to a favorite movie
        assertEquals("Error: Records not deleted from Review table during delete", 0, cursor.getCount());


        cursor = mContext.getContentResolver().query(
                ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        //Check that the record is not deleted because it is linked to a favorite movie
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, cursor.getCount());
        cursor.close();
    }


    public void testMovieBulkInsert() {

        // Now we can bulkInsert some weather.  In fact, we only implement BulkInsert for weather
        // entries.  With ContentProviders, you really only have to implement the features you
        // use, after all.
        ContentValues[] bulkInsertContentValues = TestUtilities.createBulkInsertMoviesValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieEntry.CONTENT_URI, true, tco);

        int insertCount = mContext.getContentResolver().bulkInsert(MovieEntry.CONTENT_URI, bulkInsertContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        assertEquals(insertCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                MovieEntry._ID + " DESC" // sort order == by _ID ASCENDING
        );

        // we should have as many records in the database as we've inserted
        assertEquals(cursor.getCount(), BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        cursor.moveToFirst();
        for (int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, cursor.moveToNext()) {
            TestUtilities.validateCurrentRecord("Error " + i, cursor, bulkInsertContentValues[i]);
        }
        cursor.close();
    }


    //Useful method to help import the data in a local db for testing. Android studio free version doesn't allow to visualize db file.
    public void testShowMovieTable() {

        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                MovieEntry.CONTENT_URI,
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
                MovieEntry.buildMoviesSortByUri(MovieEntry.FAVORITE_RATE),
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
                ReviewEntry.CONTENT_URI,
                REVIEWS_COLUMNS,
                null,
                null,
                null
        );
        int i = 0;
        while (cursor.moveToNext()) {
            Log.e(LOG_TAG, i + ("$" + cursor.getString(cursor.getColumnIndex(ReviewEntry._ID)) + "|" + cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_AUTHOR)) + "|" + cursor.getString(cursor.getColumnIndex(ReviewEntry.COLUMN_CONTENT)) + "|" + cursor.getInt(cursor.getColumnIndex(ReviewEntry.COLUMN_MOVIE_ID))));
            i++;
        }
        Log.e(LOG_TAG, i + "record displayed");
    }


    public void testShowTrailerTable() {
        // Test the sort_by content provider query
        Cursor cursor = mContext.getContentResolver().query(
                TrailerEntry.CONTENT_URI,
                TRAILER_COLUMNS,
                null,
                null,
                null
        );
        int i = 0;
        while (cursor.moveToNext()) {
            Log.d(LOG_TAG, i + ("$" + cursor.getString(cursor.getColumnIndex(TrailerEntry._ID))
                    + "|" + cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_KEY)) + "|" + cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_NAME)) + "|" + cursor.getString(cursor.getColumnIndex(TrailerEntry.COLUMN_TYPE)) + "|" + cursor.getInt(cursor.getColumnIndex(TrailerEntry.COLUMN_MOVIE_ID))));
            i++;
        }
        Log.d(LOG_TAG, i + "record displayed");
    }


}
