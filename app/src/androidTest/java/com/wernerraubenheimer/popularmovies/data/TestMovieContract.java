package com.wernerraubenheimer.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.wernerraubenheimer.popularmovies.Movie;

/**
 * Created by wernerr on 2015/09/12.
 */
public class TestMovieContract extends AndroidTestCase {

    // 'movie' table variables
    private static final long TEST_MOVIE_ID = 12345;
    private static final String TEST_MOVIE_ORIGINAL_TITLE = "/Mad Max: Fury Road";

    // 'trailer' table variables
    private static final long TEST_TRAILER_ID = 67890;
    private static final String TEST_STRING_TRAILER_ID = "234ad24234sf23";

    // 'review' table variables
    private static final long TEST_REVIEW_ID = 24680;
    private static final String TEST_STRING_REVIEW_ID = "988iie234lls1231";

    public void testBuildMovieUris() {

        // Test buildMovieUri
        Uri movieUri = MovieContract.MovieEntry.buildMovieUri(TEST_MOVIE_ID);

        assertNotNull("Error: buildMovieUri not well formed. ", movieUri);

        assertEquals("Error: Movie id Uri doesn't match expected result",
                movieUri.toString(), "content://com.wernerraubenheimer.popularmovies/movie/" + TEST_MOVIE_ID);

        // Test buildMovieTitleUri
        movieUri = MovieContract.MovieEntry.buildMovieTitleUri(TEST_MOVIE_ORIGINAL_TITLE);

        assertNotNull("Error: Null Uri returned. buildMovieTitleUri not well formed", movieUri);

        assertEquals("Error: Movie title not properly appended to the end of the Uri",
                TEST_MOVIE_ORIGINAL_TITLE, movieUri.getLastPathSegment());

        assertEquals("Error: Movie title Uri doesn't match our expected result",
                movieUri.toString(), "content://com.wernerraubenheimer.popularmovies/movie/%2FMad%20Max%3A%20Fury%20Road");
    }

    /**
     *  Tests for the MovieContract.TrailerEntry class
     */

    // (1) Test MovieContract.TrailerEntry.buildTrailerUri
    //     Used when you need to recover 1 trailer record from the Trailer table eg. trailer._ID = ?
    public void testBuildTrailerUri() {

        Uri trailerUri = MovieContract.TrailerEntry.buildTrailerUri(TEST_TRAILER_ID);

        assertNotNull("Error: buildTrailerUri not well formed. ", trailerUri);

        assertEquals("Error: Trailer id Uri doesn't match expected result ",
                trailerUri.toString(), "content://com.wernerraubenheimer.popularmovies/trailer/" + TEST_TRAILER_ID);
    }

    // (2) Test MovieContract.TrailerEntry.buildTrailerMovieUri
    //     Used when you need to recover multiple records from the Trailer table eg. select * where movie._ID = ?
    public void testbuildMovieTrailersUri() {
        Uri trailerUri = MovieContract.TrailerEntry.buildMovieTrailersUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned. buildTrailerMovieUri not well formed. ", trailerUri);

        assertEquals("Error: Movie id not properly appended to the end of the Uri",
                Long.toString(TEST_MOVIE_ID), trailerUri.getLastPathSegment());

        assertEquals("Error: Trailer & Movie Uri doesn't match our expected result",
                trailerUri.toString(), "content://com.wernerraubenheimer.popularmovies/trailer/movie/" + TEST_MOVIE_ID);

    }

    // (3) Test MovieContract.TrailerEntry.buildTrailerMovieWithTrailerIdUri
    //     Used when you need to recover 1 record from the Trailer table eg. movie._ID = ? and trailer_id =?
    public void testBuildTrailerUris() {

        Uri trailerUri = MovieContract.TrailerEntry.buildAMovieTrailerUri(TEST_STRING_TRAILER_ID, TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned buildTrailerMovieWithTrailerIdUri not well formed", trailerUri);

        String someValue = trailerUri.getPathSegments().get(1);
        assertEquals("Error: Trailer id (string id) not properly appended as the first path segment of the Uri",
                TEST_STRING_TRAILER_ID, someValue);

        assertEquals("Error: Trailer id not found using getTrailerIdFromUri",
                TEST_STRING_TRAILER_ID, MovieContract.TrailerEntry.getTrailerIdFromUri(trailerUri));

        assertEquals("Error: Movie id not properly appended as the fourth path segment of the Uri using .get(3)",
                Long.toString(TEST_MOVIE_ID), trailerUri.getPathSegments().get(3));

        assertEquals("Error: Movie id not properly appended as the last path segment of the Uri using .getLastPathSegment",
                Long.toString(TEST_MOVIE_ID), trailerUri.getLastPathSegment());

        assertEquals("Error: Movie id not found using getMovieIdFromUri",
                Long.toString(TEST_MOVIE_ID), MovieContract.TrailerEntry.getMovieIdFromUri(trailerUri));

    }

    /**
     *  Tests for the MovieContract.ReviewEntry class
     */

    // (1) Test MovieContract.ReviewEntry.buildReviewUri
    //     Used when you need to recover 1 review record from the Review table eg. review._ID = ?
    public void testBuildReviewUri() {

        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUri(TEST_REVIEW_ID);

        assertNotNull("Error: buildReviewUri not well formed. ", reviewUri);

        assertEquals("Error: Review id Uri doesn't match expected result ",
                reviewUri.toString(), "content://com.wernerraubenheimer.popularmovies/review/" + TEST_REVIEW_ID);
    }

    // (2) Test MovieContract.ReviewEntry.buildReviewMovieUri
    //     Used when you need to recover multiple records from the Review table eg. select * where movie._ID = ?
    public void testbuildReviewMovieUri() {
        Uri reviewUri = MovieContract.ReviewEntry.buildReviewUri(TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned. buildReviewMovieUri not well formed. ", reviewUri);

        assertEquals("Error: Movie id not properly appended to the end of the Uri",
                Long.toString(TEST_MOVIE_ID), reviewUri.getLastPathSegment());

        assertEquals("Error: Review & Movie Uri doesn't match our expected result",
                reviewUri.toString(), "content://com.wernerraubenheimer.popularmovies/review/" + TEST_MOVIE_ID);

    }

    // (3) Test MovieContract.ReviewEntry.buildReviewMovieWithReviewIdUri
    //     Used when you need to recover 1 record from the Review table eg. movie._ID = ? and review_id =?
    public void testBuildReviewUris() {

        Uri reviewUri = MovieContract.ReviewEntry.buildAMovieReviewUri(TEST_STRING_REVIEW_ID, TEST_MOVIE_ID);

        assertNotNull("Error: Null Uri returned buildReviewMovieWithReviewIdUri not well formed", reviewUri);

        String someValue = reviewUri.getPathSegments().get(1);
        assertEquals("Error: Review id (string id) not properly appended as the first path segment of the Uri",
                TEST_STRING_REVIEW_ID, someValue);

        assertEquals("Error: Review id not found using getReviewIdFromUri",
                TEST_STRING_REVIEW_ID, MovieContract.ReviewEntry.getReviewIdFromUri(reviewUri));

        assertEquals("Error: Movie id not properly appended as the fourth path segment of the Uri using .get(3)",
                Long.toString(TEST_MOVIE_ID), reviewUri.getPathSegments().get(3));

        assertEquals("Error: Movie id not properly appended as the last path segment of the Uri using .getLastPathSegment",
                Long.toString(TEST_MOVIE_ID), reviewUri.getLastPathSegment());

        assertEquals("Error: Movie id not found using getMovieIdFromUri",
                Long.toString(TEST_MOVIE_ID), MovieContract.ReviewEntry.getMovieIdFromUri(reviewUri));

    }

}
