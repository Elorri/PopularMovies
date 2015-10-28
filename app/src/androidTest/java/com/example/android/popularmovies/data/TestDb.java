package com.example.android.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.test.AndroidTestCase;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

import java.util.HashSet;


/**
 * Created by Elorri on 26/10/2015.
 */
public class TestDb extends AndroidTestCase {


    @Override
    protected void setUp() throws Exception {
        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
    }

    public void testCreateDb() throws Throwable {
        // Build a HashSet of all of the table names we wish to look for
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieEntry.TABLE_NAME);
        tableNameHashSet.add(TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(ReviewEntry.TABLE_NAME);

        mContext.deleteDatabase(MoviesDbHelper.DATABASE_NAME);
        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("No tables found in the db", c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while (c.moveToNext());

        // if this fails, it means that your database doesn't contain both the location entry
        // and weather entry tables
        assertTrue("Some tables are missing", tableNameHashSet.isEmpty());

        // now, do our tables contain the correct columns?
        checkColumnsCorrect(c, db, MovieEntry.class.getSimpleName());
        checkColumnsCorrect(c, db, TrailerEntry.class.getSimpleName());
        checkColumnsCorrect(c, db, ReviewEntry.class.getSimpleName());

        db.close();
    }

    public void testMovieTable() {
        // First step: Get reference to writable database
        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Second Step: Create ContentValues of what you want to insert
        ContentValues movieValues =  TestUtilities.createMovieValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long rowId = db.insert(MovieEntry.TABLE_NAME, null, movieValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        Cursor cursor = db.query(
                MovieEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed",
                cursor, movieValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from query", cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
    }

    public void testTrailerTable() {
        testMovieTable(); //this test will add a movie in the db

        // First step: Get reference to writable database
        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());


        // Second Step: Create ContentValues of what you want to insert
        ContentValues trailerValues =  TestUtilities.createTrailerValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long rowId = db.insert(TrailerEntry.TABLE_NAME, null, trailerValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        Cursor cursor = db.query(
                TrailerEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed",
                cursor, trailerValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from query", cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
    }


    public void testReviewTable() {
        testMovieTable(); //this test will add a movie in the db

        // First step: Get reference to writable database
        SQLiteDatabase db = new MoviesDbHelper(this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // Second Step: Create ContentValues of what you want to insert
        ContentValues reviewValues =  TestUtilities.createReviewValues();

        // Third Step: Insert ContentValues into database and get a row ID back
        long rowId = db.insert(ReviewEntry.TABLE_NAME, null, reviewValues);

        // Verify we got a row back.
        assertTrue(rowId != -1);

        // Fourth Step: Query the database and receive a Cursor back
        Cursor cursor = db.query(
                ReviewEntry.TABLE_NAME,  // Table to Query
                null, // all columns
                null, // Columns for the "where" clause
                null, // Values for the "where" clause
                null, // columns to group by
                null, // columns to filter by row groups
                null // sort order
        );

        // Move the cursor to a valid database row and check to see if we got any records back
        // from the query
        assertTrue( "Error: No Records returned from query", cursor.moveToFirst() );

        // Fifth Step: Validate data in resulting Cursor with the original ContentValues
        // (you can use the validateCurrentRecord function in TestUtilities to validate the
        // query if you like)
        TestUtilities.validateCurrentRecord("Error: Query Validation Failed",
                cursor, reviewValues);

        // Move the cursor to demonstrate that there is only one record in the database
        assertFalse( "Error: More than one record returned from query", cursor.moveToNext() );

        // Sixth Step: Close Cursor and Database
        cursor.close();
        db.close();
    }




    private void checkColumnsCorrect(Cursor c, SQLiteDatabase db, String tableContractName) {
        if (tableContractName.equals(MovieEntry.class.getSimpleName())) {
            c = db.rawQuery("PRAGMA table_info(" + MovieEntry.TABLE_NAME + ")", null);

            assertTrue("Table exist, but can't find columns", c.moveToFirst());

            // Build a HashSet of all of the column names we want to look for
            final HashSet<String> movieColumnHashSet = new HashSet<String>();
            movieColumnHashSet.add(MovieEntry._ID);
            movieColumnHashSet.add(MovieEntry.COLUMN_TITLE);
            movieColumnHashSet.add(MovieEntry.COLUMN_DURATION);
            movieColumnHashSet.add(MovieEntry.COLUMN_RELEASE_DATE);
            movieColumnHashSet.add(MovieEntry.COLUMN_POSTER_PATH);
            movieColumnHashSet.add(MovieEntry.COLUMN_PLOT_SYNOPSIS);
            movieColumnHashSet.add(MovieEntry.COLUMN_RATE);
            movieColumnHashSet.add(MovieEntry.COLUMN_POPULARITY);
            movieColumnHashSet.add(MovieEntry.COLUMN_FAVORITE);


            int columnNameIndex = c.getColumnIndex("name");
            do {
                String columnName = c.getString(columnNameIndex);
                movieColumnHashSet.remove(columnName);
            } while (c.moveToNext());

            // if this fails, it means that your database doesn't contain all of the required location
            // entry columns
            assertTrue("Some columns are missing", movieColumnHashSet.isEmpty());
        } else if (tableContractName.equals(TrailerEntry.class.getSimpleName())){
            c = db.rawQuery("PRAGMA table_info(" + TrailerEntry.TABLE_NAME + ")", null);

            assertTrue("Table exist, but can't find columns", c.moveToFirst());

            // Build a HashSet of all of the column names we want to look for
            final HashSet<String> trailerColumnHashSet = new HashSet<String>();
            trailerColumnHashSet.add(TrailerEntry._ID);
            trailerColumnHashSet.add(TrailerEntry.COLUMN_KEY);
            trailerColumnHashSet.add(TrailerEntry.COLUMN_NAME);
            trailerColumnHashSet.add(TrailerEntry.COLUMN_TYPE);
            trailerColumnHashSet.add(TrailerEntry.COLUMN_MOVIE_ID);



            int columnNameIndex = c.getColumnIndex("name");
            do {
                String columnName = c.getString(columnNameIndex);
                trailerColumnHashSet.remove(columnName);
            } while (c.moveToNext());

            // if this fails, it means that your database doesn't contain all of the required location
            // entry columns
            assertTrue("Some columns are missing", trailerColumnHashSet.isEmpty());
        } else if (tableContractName.equals(ReviewEntry.class.getSimpleName())){
            c = db.rawQuery("PRAGMA table_info(" + ReviewEntry.TABLE_NAME + ")", null);

            assertTrue("Table exist, but can't find columns", c.moveToFirst());

            // Build a HashSet of all of the column names we want to look for
            final HashSet<String> reviewColumnHashSet = new HashSet<String>();
            reviewColumnHashSet.add(ReviewEntry._ID);
            reviewColumnHashSet.add(ReviewEntry.COLUMN_AUTHOR);
            reviewColumnHashSet.add(ReviewEntry.COLUMN_CONTENT);
            reviewColumnHashSet.add(ReviewEntry.COLUMN_MOVIE_ID);

            int columnNameIndex = c.getColumnIndex("name");
            do {
                String columnName = c.getString(columnNameIndex);
                reviewColumnHashSet.remove(columnName);
            } while (c.moveToNext());

            // if this fails, it means that your database doesn't contain all of the required location
            // entry columns
            assertTrue("Some columns are missing", reviewColumnHashSet.isEmpty());
        }else{
            fail("Error");
        }
    }



}
