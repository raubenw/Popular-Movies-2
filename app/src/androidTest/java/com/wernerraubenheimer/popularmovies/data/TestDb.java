package com.wernerraubenheimer.popularmovies.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.test.AndroidTestCase;

import com.wernerraubenheimer.popularmovies.Movie;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Created by wernerr on 2015/09/05.
 */
public class TestDb extends AndroidTestCase {

    public static final String LOG_TAG = TestDb.class.getSimpleName();

    // Since we want each test to start with a clean slate
    void deleteTheDatabase() {
        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
    }

    /*
        This function gets called before each test is executed to delete the database.  This makes
        sure that we always have a clean test.
     */
    public void setUp() {
        deleteTheDatabase();
    }

    public void testCreateDb() throws Throwable {
        // build a HashSet of all of the table names we wish to look for
        // Note that there will be another table in the DB that stores the
        // Android metadata (db version information)
        final HashSet<String> tableNameHashSet = new HashSet<String>();
        tableNameHashSet.add(MovieContract.MovieEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.TrailerEntry.TABLE_NAME);
        tableNameHashSet.add(MovieContract.ReviewEntry.TABLE_NAME);

//        mContext.deleteDatabase(MovieDbHelper.DATABASE_NAME);
//        deleteTheDatabase();
        SQLiteDatabase db = new MovieDbHelper(
                this.mContext).getWritableDatabase();
        assertEquals(true, db.isOpen());

        // have we created the tables we want?
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);

        assertTrue("Error: This means that the database has not been created correctly",
                c.moveToFirst());

        // verify that the tables have been created
        do {
            tableNameHashSet.remove(c.getString(0));
        } while( c.moveToNext() );

        // if this fails, it means that your database doesn't contain the movie entry, trailer entry
        // and review entry tables
        assertTrue("Error: Your database was created without the movie entry, trailer entry and review entry tables",
                tableNameHashSet.isEmpty());

        // (1) now, do our table movie contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.MovieEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> movieColumnHashSet = new HashSet<String>();
        movieColumnHashSet.add(MovieContract.MovieEntry._ID);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_TITLE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_OVERVIEW);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_RELEASE_DATE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_POPULARITY);
        movieColumnHashSet.add(MovieContract.MovieEntry.COLUMN_FAVOURITE);

        int columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required movie
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required movie entry columns",
                movieColumnHashSet.isEmpty());

        // (2) now, do our table trailer contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.TrailerEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> trailerColumnHashSet = new HashSet<String>();
        trailerColumnHashSet.add(MovieContract.TrailerEntry._ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TRAILER_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_MOVIE_ID);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_ISO_639_1);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_ISO_3166_1);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_KEY);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_NAME);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SITE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_SIZE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_TYPE);
        trailerColumnHashSet.add(MovieContract.TrailerEntry.COLUMN_URL);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required trailer
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required trailer entry columns",
                movieColumnHashSet.isEmpty());

        // (3) now, do our table review contain the correct columns?
        c = db.rawQuery("PRAGMA table_info(" + MovieContract.ReviewEntry.TABLE_NAME + ")",
                null);

        assertTrue("Error: This means that we were unable to query the database for table information.",
                c.moveToFirst());

        // Build a HashSet of all of the column names we want to look for
        final HashSet<String> reviewColumnHashSet = new HashSet<String>();
        reviewColumnHashSet.add(MovieContract.ReviewEntry._ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_REVIEW_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_AUTHOR);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_CONTENT);
        reviewColumnHashSet.add(MovieContract.ReviewEntry.COLUMN_URL);

        columnNameIndex = c.getColumnIndex("name");
        do {
            String columnName = c.getString(columnNameIndex);
            movieColumnHashSet.remove(columnName);
        } while(c.moveToNext());

        // if this fails, it means that your database doesn't contain all of the required review
        // entry columns
        assertTrue("Error: The database doesn't contain all of the required review entry columns",
                movieColumnHashSet.isEmpty());

        // Close the cursor and the database
        c.close();
        db.close();
    }

    /*
        Test that we can insert and query the movie database for the movie table.
        Use the validateCurrentRecord function from within TestUtilities.
    */
    public void testMovieTable() {

        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createMovieValues();

        // Insert ContentValues into database and get a row ID back
        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Check if we get a valid row back
        assertTrue(rowId != -1);

        // Query the database and receive a Cursor back
        Cursor cur = db.query(MovieContract.MovieEntry.TABLE_NAME, null, null, null, null,null, null);

        // Move the cursor to a valid database row
        assertTrue("Error getting values out of Movie", cur.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // Use the validateCurrentRecord function in TestUtilities to validate the query
        TestUtilities.validateCurrentRecord("Error: 'movie' cursor and ContentValues don't match", cur, testValues);

        //Are there anymore records in 'movie'
        assertFalse("More than one row added", cur.moveToNext());

        // Finally, close the cursor and database
        cur.close();
        db.close();
    }

    /*
        Test that we can insert and query the database for the review table.
        Use TestUtilities and use the "createReviewValues" function.
        Use the validateCurrentRecord function from within TestUtilities.
     */
    public void testReviewTable() {

        // First insert the movie, and then use the movieRowId to insert the review.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieRowId = insertMovie();

        // Create ContentValues of what you want to insert
        // Use the createReviewValues TestUtilities function
        ContentValues reviewTestValues = TestUtilities.createReviewValues(movieRowId);

        // Insert ContentValues into database and get a row ID back
        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, reviewTestValues);
        assertTrue("Error inserting into 'review' ", reviewRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(MovieContract.ReviewEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error creating cursor", cursor != null);

        // Move the cursor to a valid database row
        assertTrue("Error getting data out of 'review' ", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // Use the validateCurrentRecord function in TestUtilities to validate the query
        TestUtilities.validateCurrentRecord("Error: 'review' cursor and ContentValues don't match", cursor, reviewTestValues);

        //Are there anymore records in 'review'
        assertFalse("More than one row added", cursor.moveToNext());

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

    /*
        Test that we can insert and query the database for the trailer table.
        Use TestUtilities and use the "createTrailerValues" function.
        Use the validateCurrentRecord function from within TestUtilities.
     */
    public void testTrailerTable() {

        // First insert the movie, and then use the movieRowId to insert the trailer.
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long movieRowId = insertMovie();

        // Create ContentValues of what you want to insert
        // Use the createTrailerValues TestUtilities function
        ContentValues trailerTestValues = TestUtilities.createTrailerValues(movieRowId);

        // Insert ContentValues into database and get a row ID back
        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, trailerTestValues);
        assertTrue("Error inserting into 'trailer' ", trailerRowId != -1);

        // Query the database and receive a Cursor back
        Cursor cursor = db.query(MovieContract.TrailerEntry.TABLE_NAME, null, null, null, null, null, null);
        assertTrue("Error creating cursor", cursor != null);

        // Move the cursor to a valid database row
        assertTrue("Error getting data out of 'trailer' ", cursor.moveToFirst());

        // Validate data in resulting Cursor with the original ContentValues
        // Use the validateCurrentRecord function in TestUtilities to validate the query
        TestUtilities.validateCurrentRecord("Error: 'trailer' cursor and ContentValues don't match", cursor, trailerTestValues);

        //Are there anymore records in 'trailer'
        assertFalse("More than one row added", cursor.moveToNext());

        // Finally, close the cursor and database
        cursor.close();
        db.close();
    }

    /*
        Helper method for the testMovieTable
        Moved code from testMovieTable to here so that you can call this code from
        testTrailerTable and testReviewTable.
     */
    public long insertMovie() {
        // First step: Get reference to writable database
        MovieDbHelper dbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        // Create ContentValues of what you want to insert
        ContentValues testValues = TestUtilities.createMovieValues();

        // Insert ContentValues into database and get a row ID back
        long rowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        // Finally, close the database
        db.close();

        return rowId;
    }
}
