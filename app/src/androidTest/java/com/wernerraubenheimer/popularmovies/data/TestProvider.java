package com.wernerraubenheimer.popularmovies.data;

import android.content.ComponentName;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.TriggerEvent;
import android.net.Uri;
import android.os.Build;
import android.test.AndroidTestCase;
import android.util.Log;

import com.wernerraubenheimer.popularmovies.Movie;

import junit.framework.Test;

/**
 * Created by wernerr on 2015/09/12.
 */
public class TestProvider extends AndroidTestCase {

    final String LOG_TAG = TestProvider.class.getSimpleName();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        deleteAllRecordsFromProvider();
    }

    /*
           This helper function deletes all records from both database tables using the ContentProvider.
           It also queries the ContentProvider to make sure that the database has been successfully
           deleted, so it cannot be used until the Query and Delete functions have been written
           in the ContentProvider.
        */
    public void deleteAllRecordsFromProvider() {

        mContext.getContentResolver().delete(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null
        );

        mContext.getContentResolver().delete(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null
        );

        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Review table during delete", 0, reviewCursor.getCount());
        reviewCursor.close();

        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Trailer table during delete", 0, trailerCursor.getCount());
        trailerCursor.close();

        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        assertEquals("Error: Records not deleted from Movie table during delete", 0, movieCursor.getCount());
        movieCursor.close();
    }

    /*
        This test checks to make sure that the content provider is registered correctly.
     */
    public void testProviderRegistry() {
        PackageManager pm = mContext.getPackageManager();

        // We define the component name based on the package name from the context and the
        // MovieProvider class.
        ComponentName componentName = new ComponentName(mContext.getPackageName(),
                MovieProvider.class.getName());
        try {
            // Fetch the provider info using the component name from the PackageManager
            // This throws an exception if the provider isn't registered.
            ProviderInfo providerInfo = pm.getProviderInfo(componentName, 0);

            // Make sure that the registered authority matches the authority from the Contract.
            assertEquals("Error: MovieProvider registered with authority: " + providerInfo.authority +
                            " instead of authority: " + MovieContract.CONTENT_AUTHORITY,
                    providerInfo.authority, MovieContract.CONTENT_AUTHORITY);
        } catch (PackageManager.NameNotFoundException e) {
            // I guess the provider isn't registered correctly.
            assertTrue("Error: Movie Provider not registered at " + mContext.getPackageName(),
                    false);
        }
    }

    /*
        This test doesn't touch the database.  It verifies that the ContentProvider returns
        the correct type for each type of URI that it can handle.
     */
    public void testGetType() {

        long testMovieId = 12345;
        String testReviewId = "12sdf24ll564s";
        String testTrailerId = "902awld234ladfoi342";

        // content://com.werneraubenheimer.popularmovies/review
        // vnd.android.cursor.dir/com.wernerraubenheimer.popularmovies/review
        String reviewContentResolverType = mContext.getContentResolver().getType(MovieContract.ReviewEntry.CONTENT_URI);
        String reviewEntryContentType = MovieContract.ReviewEntry.CONTENT_TYPE;
        assertEquals("Error: The ReviewEntry CONTENT_URI should return ReviewEntry.CONTENT_TYPE",
               reviewEntryContentType , reviewContentResolverType);

        // content://com.werneraubenheimer.popularmovies/review
        // vnd.android.cursor.dir/com.wernerraubenheimer.popularmovies/review
        reviewContentResolverType = mContext.getContentResolver().getType(MovieContract.ReviewEntry.buildMovieReviewsUri(testMovieId));
        reviewEntryContentType = MovieContract.ReviewEntry.CONTENT_TYPE;
        assertEquals("Error: The ReviewEntry CONTENT_URI with movie_id should return ReviewEntry.CONTENT_TYPE",
                reviewEntryContentType, reviewContentResolverType);

        // content://com.werneraubenheimer.popularmovies/review
        // vnd.android.cursor.item/com.wernerraubenheimer.popularmovies/review
        reviewContentResolverType = mContext.getContentResolver().getType(MovieContract.ReviewEntry.buildAMovieReviewUri(testReviewId, testMovieId));
        reviewEntryContentType = MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;
        assertEquals("Error: The ReviewEntry CONTENT_URI with review_id and movie_id should return ReviewEntry.CONTENT_ITEM_TYPE",
                reviewEntryContentType, reviewContentResolverType);

        // content://com.werneraubenheimer.popularmovies/trailer
        // vnd.android.cursor.dir/com.wernerraubenheimer.popularmovies/trailer
        String trailerContentResolverType = mContext.getContentResolver().getType(MovieContract.TrailerEntry.CONTENT_URI);
        String trailerEntryContentType = MovieContract.TrailerEntry.CONTENT_TYPE;
        assertEquals("Error: The TrailerEntry CONTENT_URI should return TrailerEntry.CONTENT_TYPE",
                trailerEntryContentType , trailerContentResolverType);

        // content://com.werneraubenheimer.popularmovies/trailer
        // vnd.android.cursor.dir/com.wernerraubenheimer.popularmovies/trailer
        trailerContentResolverType = mContext.getContentResolver().getType(MovieContract.TrailerEntry.buildMovieTrailersUri(testMovieId));
        trailerEntryContentType = MovieContract.TrailerEntry.CONTENT_TYPE;
        assertEquals("Error: The TrailerEntry CONTENT_URI with movie_id should return TraielrEntry.CONTENT_TYPE",
                trailerEntryContentType, trailerContentResolverType);

        // content://com.werneraubenheimer.popularmovies/trailer
        // vnd.android.cursor.item/com.wernerraubenheimer.popularmovies/trailer
        trailerContentResolverType = mContext.getContentResolver().getType(MovieContract.TrailerEntry.buildAMovieTrailerUri(testTrailerId, testMovieId));
        trailerEntryContentType = MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;
        assertEquals("Error: The TrailerEntry CONTENT_URI with trailer_id and movie_id should return TrailerEntry.CONTENT_ITEM_TYPE",
                trailerEntryContentType, trailerContentResolverType);

        // content://com.wernerraubenheimer.popularmovies/movies
        // vnd.android.cursor.dir/com.wernerraubenheimer.popularmovies/movie
        String movieType = mContext.getContentResolver().getType(MovieContract.MovieEntry.CONTENT_URI);
        assertEquals("Error: The MovieEntry CONTENT_URI should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, movieType);

        // content://com.wernerraubenheimer.popularmovies/movies
        // vnd.android.cursor.dir/com.wernerraubenheimer.popularmovies/movie
        String originalTitle = "Mad Max: Fury Road";
        movieType = mContext.getContentResolver().getType(MovieContract.MovieEntry.buildMovieTitleUri(originalTitle));
        assertEquals("Error: The MovieEntry CONTENT_URI with original title should return MovieEntry.CONTENT_TYPE",
                MovieContract.MovieEntry.CONTENT_TYPE, movieType);
    }

    /*
        This test uses the database directly to insert and then uses the ContentProvider to
        read out the data.
    */
    public void testBasicMovieQueries() {

        MovieDbHelper movieDbHelper = new MovieDbHelper(mContext);
        SQLiteDatabase db = movieDbHelper.getWritableDatabase();

        ContentValues testMovieValues = TestUtilities.createMovieValues();
        long movieRowId = TestUtilities.insertMovieValues(mContext);
        assertTrue("Unable to Insert MovieEntry into the Database", movieRowId != -1);

        ContentValues testTrailerValues = TestUtilities.createTrailerValues(movieRowId);
        long trailerRowId = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, testTrailerValues);
        assertTrue("Unable to Insert TrailerEntry into the Database", trailerRowId != -1);

        ContentValues testReviewValues = TestUtilities.createReviewValues(movieRowId);
        long reviewRowId = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, testReviewValues);
        assertTrue("Unable to Insert ReviewEntry into the Database", reviewRowId != -1);

        // Test the basic content provider query
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );

        // Make sure we get the correct cursor out of the database
        TestUtilities.validateCursor("testBasicMovieQueries, movie query", movieCursor, testMovieValues);
        TestUtilities.validateCursor("testBasicMovieQueries, trailer query", trailerCursor, testTrailerValues);
        TestUtilities.validateCursor("testBasicMovieQueries, review query", reviewCursor, testReviewValues);

        // Has the NotificationUri been set correctly? --- we can only test this easily against API
        // level 19 or greater because getNotificationUri was added in API level 19.
        if ( Build.VERSION.SDK_INT >= 19 ) {
            assertEquals("Error: Movie Query did not properly set NotificationUri",
                    movieCursor.getNotificationUri(), MovieContract.MovieEntry.CONTENT_URI);
            assertEquals("Error: Trailer Query did not properly set NotificationUri",
                    trailerCursor.getNotificationUri(), MovieContract.TrailerEntry.CONTENT_URI);
            assertEquals("Error: Review Query did not properly set NotificationUri",
                    reviewCursor.getNotificationUri(), MovieContract.ReviewEntry.CONTENT_URI);
        }

    }

    public void testUpdateMovieTrailerReview() {

        // Create a new map of values, where column names are the keys
        ContentValues movieValues = TestUtilities.createMovieValues();

        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, movieValues);
        long movieRowId = ContentUris.parseId(movieUri);
        assertTrue("Unable to insert movie", movieRowId != -1);

        ContentValues updatedMovieValues = new ContentValues(movieValues);
        updatedMovieValues.put(MovieContract.MovieEntry._ID, movieRowId);
        updatedMovieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Werner's Movie App");

        ContentValues trailerValues = TestUtilities.createTrailerValues(movieRowId);

        Uri trailerUri = mContext.getContentResolver().insert(MovieContract.TrailerEntry.CONTENT_URI, trailerValues);
        long trailerRowId = ContentUris.parseId(trailerUri);
        assertTrue("Unable to insert trailer", trailerRowId != -1);

        ContentValues updatedTrailerValues = new ContentValues(trailerValues);
        updatedTrailerValues.put(MovieContract.TrailerEntry._ID, trailerRowId);
        updatedTrailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Werner Excellente Trailer");

        ContentValues reviewValues = TestUtilities.createReviewValues(movieRowId);

        Uri reviewUri = mContext.getContentResolver().insert(MovieContract.ReviewEntry.CONTENT_URI, reviewValues);
        long reviewRowId = ContentUris.parseId(reviewUri);
        assertTrue("Unable to insert review", reviewRowId != -1);

        ContentValues updatedReviewValues = new ContentValues(reviewValues);
        updatedReviewValues.put(MovieContract.ReviewEntry._ID, reviewRowId);
        updatedReviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "Werner's Awesome Review");

        // Create cursors with observer to make sure that the content provider is notifying
        // the observers as expected
        Cursor movieCursor = mContext.getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
        Cursor trailerCursor = mContext.getContentResolver().query(MovieContract.TrailerEntry.CONTENT_URI, null, null, null, null);
        Cursor reviewCursor = mContext.getContentResolver().query(MovieContract.ReviewEntry.CONTENT_URI, null, null, null, null);

        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        movieCursor.registerContentObserver(tco);
        trailerCursor.registerContentObserver(tco);
        reviewCursor.registerContentObserver(tco);

        int movieCount = mContext.getContentResolver().update(
                MovieContract.MovieEntry.CONTENT_URI,
                updatedMovieValues,
                MovieContract.MovieEntry._ID + " = ?",
                new String[] { Long.toString(movieRowId)});
        assertEquals("Error: Failed to update movie row", movieCount, 1);

        int trailerCount = mContext.getContentResolver().update(
                MovieContract.TrailerEntry.CONTENT_URI,
                updatedTrailerValues,
                MovieContract.TrailerEntry._ID + " = ?",
                new String[]{Long.toString(trailerRowId)});
        assertEquals("Error: Failed to update trailer row", trailerCount, 1);

        int reviewCount = mContext.getContentResolver().update(
                MovieContract.ReviewEntry.CONTENT_URI,
                updatedReviewValues,
                MovieContract.ReviewEntry._ID + " = ?",
                new String[]{Long.toString(reviewRowId)});
        assertEquals("Error: Failed to update review row", reviewCount, 1);

        // Test to make sure our observer is called.  If not, we throw an assertion.
        //
        // If your code is failing here, it means that your content provider
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();

        movieCursor.unregisterContentObserver(tco);
        movieCursor.close();

        trailerCursor.unregisterContentObserver(tco);
        trailerCursor.close();

        reviewCursor.unregisterContentObserver(tco);
        reviewCursor.close();

        // A cursor is your primary interface to the query results.
         movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null,   // projection
                MovieContract.MovieEntry._ID + " = " + movieRowId,
                null,   // Values for the "where" clause
                null    // sort order
        );

        TestUtilities.validateCursor("testUpdateMovie.  Error validating movie entry update.",
                movieCursor, updatedMovieValues);

        movieCursor.close();

        trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null,
                MovieContract.TrailerEntry._ID + " = " + trailerRowId,
                null,
                null
        );

        TestUtilities.validateCursor("testUpdateTrailer. Error validating trailer entry update.",
                trailerCursor, updatedTrailerValues);

        trailerCursor.close();

        reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null,
                MovieContract.ReviewEntry._ID + " = " + reviewRowId,
                null,
                null
        );

        TestUtilities.validateCursor("testUpdateReview. Error validating review entry update.",
                reviewCursor, updatedReviewValues);

        reviewCursor.close();
    }


    public void testInsertReadProvider() {

        /** movie table **/
        ContentValues testMovieValues = TestUtilities.createMovieValues();

        // Register a content observer for our insert.  This time, directly with the content resolver
        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testMovieValues);

        // Did the content observer get called?  If this fails, your insert movie
        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        long movieRowId = ContentUris.parseId(movieUri);

        // Verify we got a row back.
        assertTrue(movieRowId != -1);

        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
        // the round trip.

        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
                movieCursor, testMovieValues);

        // Fantastic.  Now that we have a movie, add a trailer!
        ContentValues testTrailerValues = TestUtilities.createTrailerValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.CONTENT_URI, true, tco);

        Uri trailerInsertUri = mContext.getContentResolver()
                .insert(MovieContract.TrailerEntry.CONTENT_URI, testTrailerValues);
        assertTrue(trailerInsertUri != null);

        // Did our content observer get called? If this fails, your insert trailer
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating TrailerEntry insert.",
                trailerCursor, testTrailerValues);

        // Add the movie values in with the trailer data so that we can make
        // sure that the join worked and we actually get all the values back

        /**
         * remove _id van die movies content values
         */

        testTrailerValues.putAll(testMovieValues);
        testTrailerValues.remove("_id");
//        testTrailerValues.remove("id");

        String[] trailerProjection = {
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE,
                MovieContract.MovieEntry.COLUMN_POPULARITY,
                MovieContract.MovieEntry.COLUMN_FAVOURITE,
                MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
                MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
                MovieContract.TrailerEntry.COLUMN_ISO_639_1,
                MovieContract.TrailerEntry.COLUMN_ISO_3166_1,
                MovieContract.TrailerEntry.COLUMN_KEY,
                MovieContract.TrailerEntry.COLUMN_NAME,
                MovieContract.TrailerEntry.COLUMN_SITE,
                MovieContract.TrailerEntry.COLUMN_SIZE,
                MovieContract.TrailerEntry.COLUMN_TYPE,
                MovieContract.TrailerEntry.COLUMN_URL
        };

        // Get the joined Trailer and Movie data
        trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.buildMovieTrailersUri(TestUtilities.TEST_MOVIE_ID),
                trailerProjection, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Trailer and Movie Data.",
                trailerCursor, testTrailerValues);


        // Get the joined Trailer data for a specific trailer id and movie id
        trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.buildAMovieTrailerUri(TestUtilities.TEST_TRAILER_ID, TestUtilities.TEST_MOVIE_ID),
                trailerProjection,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Trailer and Movie data for a specific Trailer & Movie.",
                trailerCursor, testTrailerValues);


//        ContentValues testValues = TestUtilities.createMovieValues();
//
//        // Register a content observer for our insert.  This time, directly with the content resolver
//        TestUtilities.TestContentObserver tco = TestUtilities.getTestContentObserver();
//        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, tco);
//        Uri movieUri = mContext.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, testValues);
//
//        // Did our content observer get called?  If this fails, your movie
//        // isn't calling getContext().getContentResolver().notifyChange(uri, null);
//        tco.waitForNotificationOrFail();
//        mContext.getContentResolver().unregisterContentObserver(tco);
//
//        long movieRowId = ContentUris.parseId(movieUri);
//
//        // Verify we got a row back.
//        assertTrue(movieRowId != -1);
//
//        // Data's inserted.  IN THEORY.  Now pull some out to stare at it and verify it made
//        // the round trip.
//
//        // A cursor is your primary interface to the query results.
//        Cursor cursor = mContext.getContentResolver().query(
//                MovieContract.MovieEntry.CONTENT_URI,
//                null, // leaving "columns" null just returns all the columns.
//                null, // cols for "where" clause
//                null, // values for "where" clause
//                null  // sort order
//        );
//
//        TestUtilities.validateCursor("testInsertReadProvider. Error validating MovieEntry.",
//                cursor, testValues);

        // Fantastic.  Now that we have a movie and a trailer, add a review!
        ContentValues testReviewValues = TestUtilities.createReviewValues(movieRowId);
        // The TestContentObserver is a one-shot class
        tco = TestUtilities.getTestContentObserver();

        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, tco);

        Uri reviewInsertUri = mContext.getContentResolver()
                .insert(MovieContract.ReviewEntry.CONTENT_URI, testReviewValues);
        assertTrue(reviewInsertUri != null);

        // Did our content observer get called? If this fails, your insert trailer
        // in your ContentProvider isn't calling
        // getContext().getContentResolver().notifyChange(uri, null);
        tco.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(tco);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,  // Table to Query
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null // columns to group by
        );

        TestUtilities.validateCursor("testInsertReadProvider. Error validating ReviewEntry insert.",
                reviewCursor, testReviewValues);

        // Add the movie values in with the review data so that we can make
        // sure that the join worked and we actually get all the values back

        /**
         * remove _id van die movies content values
         */

        testReviewValues.putAll(testMovieValues);
        testReviewValues.remove("_id");
//        testReviewValues.remove("id");

        String[] reviewProjection = {
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE,
                MovieContract.MovieEntry.COLUMN_POPULARITY,
                MovieContract.MovieEntry.COLUMN_FAVOURITE,
                MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
                MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
                MovieContract.ReviewEntry.COLUMN_AUTHOR,
                MovieContract.ReviewEntry.COLUMN_CONTENT,
                MovieContract.ReviewEntry.COLUMN_URL
        };

        // Get the joined Review and Movie data
        reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.buildMovieReviewsUri(TestUtilities.TEST_MOVIE_ID),
                reviewProjection, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Review and Movie Data.",
                reviewCursor, testReviewValues);


        // Get the joined Trailer data for a specific trailer id and movie id
        reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.buildAMovieReviewUri(TestUtilities.TEST_REVIEW_ID, TestUtilities.TEST_MOVIE_ID),
                reviewProjection,
                null,
                null,
                null
        );
        TestUtilities.validateCursor("testInsertReadProvider.  Error validating joined Review and Movie data for a specific Review & Movie.",
                reviewCursor, testReviewValues);


    }

    public void testDeleteRecords() {
        testInsertReadProvider();

        // Register a content observer for our favourites delete.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        deleteAllRecordsFromProvider();

//        // Students: If either of these fail, you most-likely are not calling the
//        // getContext().getContentResolver().notifyChange(uri, null); in the ContentProvider
//        // delete.  (only if the insertReadProvider is succeeding)
        movieObserver.waitForNotificationOrFail();

        mContext.getContentResolver().unregisterContentObserver(movieObserver);
    }



    static private final int BULK_INSERT_RECORDS_TO_INSERT = 10;

    static ContentValues[] createBulkInsertMovieValues() {

        ContentValues[] returnContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++) {
            ContentValues movieValues = new ContentValues();

            movieValues.put(MovieContract.MovieEntry._ID, i + 500);
            movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, "Werner's #" + i + " Movie");
            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, "/path1/");
            movieValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, "Description ");
            movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, "2015-01-01");
            movieValues.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, 1);
            movieValues.put(MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE, 22);
            movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, 23);

            returnContentValues[i] = movieValues;
        }
        return returnContentValues;
    }

    static ContentValues[] createBulkInsertTrailerValues() {

        ContentValues[] movieContentValues = createBulkInsertMovieValues();
        ContentValues[] returnTrailerContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        int movieId = movieContentValues[4].getAsInteger(MovieContract.MovieEntry._ID);

        for (int j = 0; j < BULK_INSERT_RECORDS_TO_INSERT; j++) {
            ContentValues trailerValues = new ContentValues();
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_ISO_639_1, "en" + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_ISO_3166_1, "US" + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_KEY, "Rt2LHkSwdPQ" + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_NAME, "Official Trailer #2" + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SITE, "YouTube" + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, 1080 + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, "Trailer" + j);
            trailerValues.put(MovieContract.TrailerEntry.COLUMN_URL, "http://www.google.com/" + j);

            returnTrailerContentValues[j] = trailerValues;
        }

        return returnTrailerContentValues;
    }

    static ContentValues[] createBulkInsertReviewValues() {

        ContentValues[] movieContentValues = createBulkInsertMovieValues();
        ContentValues[] returnReviewContentValues = new ContentValues[BULK_INSERT_RECORDS_TO_INSERT];

        int movieId = movieContentValues[7].getAsInteger(MovieContract.MovieEntry._ID);

        for (int j = 0; j < BULK_INSERT_RECORDS_TO_INSERT; j++) {
            ContentValues reviewValues = new ContentValues();
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, j);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, "decovision " + j);
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, "People seem to think that if you enjoy a Christopher Nolan movie, you are a fanboy.");
            reviewValues.put(MovieContract.ReviewEntry.COLUMN_URL, "https://www.themoviedb.org/review/55f43b7dc3a3686d03003292");

            returnReviewContentValues[j] = reviewValues;
        }

        return returnReviewContentValues;
    }

    // Student: Uncomment this test after you have completed writing the BulkInsert functionality
    // in your provider.  Note that this test will work with the built-in (default) provider
    // implementation, which just inserts records one-at-a-time, so really do implement the
    // BulkInsert ContentProvider function.
    public void testBulkInsert() {

        deleteAllRecordsFromProvider();

        /** MOVIE TABLE **/
        ContentValues[] bulkInsertMovieContentValues = createBulkInsertMovieValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver movieObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.MovieEntry.CONTENT_URI, true, movieObserver);

        int insertMovieCount = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, bulkInsertMovieContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(movieObserver);

        assertEquals("BulkInsert Movies: Cursor count not equal to contentvalues",
                insertMovieCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor movieCursor = mContext.getContentResolver().query(
                MovieContract.MovieEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        int movieCursorCount = movieCursor.getCount();

        // we should have as many records in the database as we've inserted
        assertEquals("Error: unequal amount of movie rows ", movieCursorCount, BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        movieCursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, movieCursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating MovieEntry " + i,
                    movieCursor, bulkInsertMovieContentValues[i]);
        }
        movieCursor.close();

        /** TRAILER TABLE **/
        ContentValues[] bulkInsertTrailerContentValues = createBulkInsertTrailerValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver trailerObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.TrailerEntry.CONTENT_URI, true, trailerObserver);

        int insertTrailerCount = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, bulkInsertTrailerContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);

        assertEquals("BulkInsert Trailer: Cursor count not equal to contentvalues",
                insertTrailerCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        int trailerCursorCount = trailerCursor.getCount();

        // we should have as many records in the database as we've inserted
        assertEquals("Error: unequal amount of trailer rows ", trailerCursorCount, BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        trailerCursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, trailerCursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating TrailerEntry " + i,
                    trailerCursor, bulkInsertTrailerContentValues[i]);
        }
        trailerCursor.close();

        /** REVIEW TABLE **/
        ContentValues[] bulkInsertReviewContentValues = createBulkInsertReviewValues();

        // Register a content observer for our bulk insert.
        TestUtilities.TestContentObserver reviewObserver = TestUtilities.getTestContentObserver();
        mContext.getContentResolver().registerContentObserver(MovieContract.ReviewEntry.CONTENT_URI, true, reviewObserver);

        int insertReviewCount = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, bulkInsertReviewContentValues);

        // Students:  If this fails, it means that you most-likely are not calling the
        // getContext().getContentResolver().notifyChange(uri, null); in your BulkInsert
        // ContentProvider method.
        movieObserver.waitForNotificationOrFail();
        mContext.getContentResolver().unregisterContentObserver(trailerObserver);

        assertEquals("BulkInsert Review: Cursor count not equal to contentvalues",
                insertReviewCount, BULK_INSERT_RECORDS_TO_INSERT);

        // A cursor is your primary interface to the query results.
        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                null, // leaving "columns" null just returns all the columns.
                null, // cols for "where" clause
                null, // values for "where" clause
                null  // sort order == by DATE ASCENDING
        );

        int reviewCursorCount = reviewCursor.getCount();

        // we should have as many records in the database as we've inserted
        assertEquals("Error: unequal amount of review rows ", reviewCursorCount, BULK_INSERT_RECORDS_TO_INSERT);

        // and let's make sure they match the ones we created
        reviewCursor.moveToFirst();
        for ( int i = 0; i < BULK_INSERT_RECORDS_TO_INSERT; i++, reviewCursor.moveToNext() ) {
            TestUtilities.validateCurrentRecord("testBulkInsert.  Error validating ReviewEntry " + i,
                    reviewCursor, bulkInsertReviewContentValues[i]);
        }
        reviewCursor.close();
    }
}