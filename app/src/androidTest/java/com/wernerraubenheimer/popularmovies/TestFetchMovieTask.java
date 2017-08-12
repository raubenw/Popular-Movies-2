package com.wernerraubenheimer.popularmovies;

/**
 * Created by wernerr on 2015/09/25.
 */
import android.annotation.TargetApi;
import android.database.Cursor;
import android.test.AndroidTestCase;

import com.wernerraubenheimer.popularmovies.data.MovieContract;
//import com.wernerraubenheimer.popularmovies.data.MovieProvider;

import static com.wernerraubenheimer.popularmovies.MainActivityFragment.*;

public class TestFetchMovieTask extends AndroidTestCase{

//    static final String FAV_ORIGINAL_TITLE = "Straight Outta Compton";
//    static final String FAV_POSTER_PATH = "/X7S1RtotXOZNV7OlgCfh5VKZSB.jpg";
//    static final String FAV_OVERVIEW = "Corey Hawkins and Jason Mitchell as Ice Cube, Dr. Dre and Eazy-E";
//    static final String FAV_RELEASE_DATE = "2015-08-14";
//
//    /*
//        Students: uncomment testAddLocation after you have written the AddLocation function.
//        This test will only run on API level 11 and higher because of a requirement in the
//        content provider.
//     */
//    @TargetApi(11)
//    public void testFavourite() {
//        // start from a clean state
//        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
//                MovieContract.MovieEntry.COLUMN_TITLE + " = ? ",
//                new String[]{FAV_ORIGINAL_TITLE});
//
//        FetchMoviesTask fmt = new FetchMoviesTask(getContext());
//        long favouritesId = fmt.addFavourite(FAV_ORIGINAL_TITLE, FAV_POSTER_PATH, FAV_OVERVIEW, FAV_RELEASE_DATE);
//
//        // does addLocation return a valid record ID?
//        assertFalse("Error: addFavourite returned an invalid ID on insert",
//                favouritesId == -1);
//
//        // test all this twice
//        for ( int i = 0; i < 2; i++ ) {
//
//            // does the ID point to our location?
//            Cursor favouriteCursor = getContext().getContentResolver().query(
//                    MovieContract.MovieEntry.CONTENT_URI,
//                    new String[]{
//                            MovieContract.MovieEntry._ID,
//                            MovieContract.MovieEntry.COLUMN_TITLE,
//                            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
//                            MovieContract.MovieEntry.COLUMN_OVERVIEW,
//                            MovieContract.MovieEntry.COLUMN_RELEASE_DATE
//                    },
//                    MovieContract.MovieEntry.COLUMN_TITLE + " = ?",
//                    new String[]{FAV_ORIGINAL_TITLE},
//                    null);
//
//
//            // these match the indices of the projection
//            if (favouriteCursor.moveToFirst()) {
//
//                long testFavouritesId = favouriteCursor.getLong(0);
//                String testTitle = favouriteCursor.getString(1);
//                String testPosterPath = favouriteCursor.getString(2);
//                String testOverview = favouriteCursor.getString(3);
//                String testReleaseDate = favouriteCursor.getString(4);
//
//
//                assertEquals("Error: the queried value of favouritesId does not match the returned value" +
//                        "from addFavourites", testFavouritesId, favouritesId);
//                assertEquals("Error: the queried value of original title is incorrect",
//                        testTitle, FAV_ORIGINAL_TITLE);
//                assertEquals("Error: the queried value of poster path is incorrect",
//                        testPosterPath, FAV_POSTER_PATH);
//                assertEquals("Error: the queried value of overview is incorrect",
//                        testOverview, FAV_OVERVIEW);
//                assertEquals("Error: the queried value of release date is incorrect",
//                        testReleaseDate, FAV_RELEASE_DATE);
//            } else {
//                fail("Error: the id you used to query returned an empty cursor");
//            }
//
//            // there should be no more records
//            assertFalse("Error: there should be only one record returned from a favourites query",
//                    favouriteCursor.moveToNext());
//
//            // add the location again
//            long newFavouriteId = fmt.addFavourite(FAV_ORIGINAL_TITLE, FAV_POSTER_PATH, FAV_OVERVIEW, FAV_RELEASE_DATE);
//
//            assertEquals("Error: inserting a location again should return the same ID",
//                    favouritesId, newFavouriteId);
//        }
//        // reset our state back to normal
//        getContext().getContentResolver().delete(MovieContract.MovieEntry.CONTENT_URI,
//                MovieContract.MovieEntry.COLUMN_TITLE + " = ?",
//                new String[]{FAV_ORIGINAL_TITLE});
//
//        // clean up the test so that other tests can use the content provider
//        getContext().getContentResolver().
//                acquireContentProviderClient(MovieContract.MovieEntry.CONTENT_URI).
//                getLocalContentProvider().shutdown();
//    }
}