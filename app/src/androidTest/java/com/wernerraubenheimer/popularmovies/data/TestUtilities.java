package com.wernerraubenheimer.popularmovies.data;

/**
 * Created by wernerr on 2015/09/24.
 */

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.test.AndroidTestCase;

import com.wernerraubenheimer.popularmovies.utils.PollingCheck;

import java.util.Map;
import java.util.Set;


/*
    Students: These are functions and some test data to make it easier to test your database and
    Content Provider.  Note that you'll want your WeatherContract class to exactly match the one
    in our solution to use these as-given.
 */
public class TestUtilities extends AndroidTestCase {

    public static final long TEST_MOVIE_ID = 12345;
    public static final String TEST_TRAILER_ID = "5795006f92514142390035ae";
    public static final String TEST_REVIEW_ID = "55f43b7dc3a3686d03003292";

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

    static long insertMovieValues(Context context) {
        MovieDbHelper dbHelper = new MovieDbHelper(context);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues testValues = TestUtilities.createMovieValues();

        long movieRowId;
        movieRowId = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, testValues);

        //Verify we got a row back
        assertTrue("Error: Failure to insert Movie Values", movieRowId != -1);

        return movieRowId;
    }

    static ContentValues createMovieValues() {
        ContentValues movieValues = new ContentValues();

        movieValues.put(MovieContract.MovieEntry._ID, TEST_MOVIE_ID);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Mad Max");
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/kqjL17yufvn9OVLyXYpvtyrFfak.jpg");
        movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "An apocalyptic story set in the furthest reaches of our planet");
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-05-15");
        movieValues.put(MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE, 25.49);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 116.23);
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);

        return movieValues;
    }

    /*
     *  Create ContentValues for the 'trailer' table
     *  @param movieRowId the primary key in the 'movie' table
     */
    static ContentValues createTrailerValues(long movieRowId) {

        ContentValues trailerValues = new ContentValues();

        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, TEST_TRAILER_ID);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieRowId);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_ISO_639_1, "en");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_ISO_3166_1, "US");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "Rt2LHkSwdPQ");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Official Trailer #2");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE, "YouTube");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, 1080);
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Trailer");
        trailerValues.put(MovieContract.TrailerEntry.COLUMN_URL, "http://www.google.com/");

        return trailerValues;
    }

    /*
     *  Create ContentValues for the 'review' table
     *  @param movieRowId the primary key in the 'movie' table
     */
    static ContentValues createReviewValues(long movieRowId) {

        ContentValues reviewValues = new ContentValues();

        reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, TEST_REVIEW_ID);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieRowId);
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "decovision");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "People seem to think that if you enjoy a Christopher Nolan movie, you are a fanboy.");
        reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, "https://www.themoviedb.org/review/55f43b7dc3a3686d03003292");

        return reviewValues;
    }

    /*
        Students: The functions we provide inside of TestProvider use this utility class to test
        the ContentObserver callbacks using the PollingCheck class that we grabbed from the Android
        CTS tests.
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

    static TestContentObserver getTestContentObserver() {
        return TestContentObserver.getTestContentObserver();
    }
}