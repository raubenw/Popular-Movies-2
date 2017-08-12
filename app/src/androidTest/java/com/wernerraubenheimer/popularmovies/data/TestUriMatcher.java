package com.wernerraubenheimer.popularmovies.data;

import android.content.UriMatcher;
import android.net.Uri;
import android.test.AndroidTestCase;

/**
 * Created by wernerr on 2015/09/12.
 */
public class TestUriMatcher extends AndroidTestCase {

    // content://com.wernerraubenheimer.popularmovies.data/movie"
    private static final String ORIGINAL_TITLE_QUERY = "Mad Max: Fury Road";
    private static final Uri TEST_MOVIES_DIR = MovieContract.MovieEntry.CONTENT_URI;
    private static final Uri TEST_MOVIES_WITH_ORIGINAL_TITLE_DIR = MovieContract.MovieEntry.buildMovieTitleUri(ORIGINAL_TITLE_QUERY);
    private static final int MOVIE_ID = 12345;

    // content://com.wernerraubenheimer.popularmovies.data/trailer"
    private static final int TRAILER_PRIME_ID = 9876;
    private static final String TRAILER_ID = "234lj523yiu3423g";
    private static final Uri TEST_TRAILER_DIR = MovieContract.TrailerEntry.CONTENT_URI;
    private static final Uri TEST_TRAILER_ID_ITEM = MovieContract.TrailerEntry.buildTrailerUri(TRAILER_PRIME_ID);
    private static final Uri TEST_MOVIE_TRAILERS_DIR = MovieContract.TrailerEntry.buildMovieTrailersUri(MOVIE_ID);
    private static final Uri TEST_A_MOVIE_TRAILER_ITEM = MovieContract.TrailerEntry.buildAMovieTrailerUri(TRAILER_ID, MOVIE_ID);

    // content://com.wernerraubenheimer.popularmovies.data/review"
    private static final int REVIEW_PRIME_ID = 1357;
    private static final String REVIEW_ID = "4lj523423g23yiu3";
    private static final Uri TEST_REVIEW_DIR = MovieContract.ReviewEntry.CONTENT_URI;
    private static final Uri TEST_REVIEW_ID_ITEM = MovieContract.ReviewEntry.buildReviewUri(REVIEW_PRIME_ID);
    private static final Uri TEST_REVIEW_MOVIE_DIR = MovieContract.ReviewEntry.buildMovieReviewsUri(MOVIE_ID);
    private static final Uri TEST_REVIEW_MOVIE_REVIEW_DIR = MovieContract.ReviewEntry.buildAMovieReviewUri(REVIEW_ID, MOVIE_ID);


    public void testUriMatcherMovieTable() {

        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The MOVIES URI was matched incorrectly.",
                     testMatcher.match(TEST_MOVIES_DIR),
                     MovieProvider.MOVIE);

        assertEquals("Error: The MOVIES WITH ORIGINAL TITLE URI was matched incorrectly.",
                     testMatcher.match(TEST_MOVIES_WITH_ORIGINAL_TITLE_DIR),
                     MovieProvider.MOVIE_WITH_ORIGINAL_TITLE);

    }

    public void testUriMatcherTrailerTable() {

        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_TRAILER_DIR),
                MovieProvider.TRAILER);

        assertEquals("Error: The TRAILER WITH MOVIE ID URI was matched incorrectly",
                testMatcher.match(TEST_MOVIE_TRAILERS_DIR),
                MovieProvider.MOVIE_TRAILERS);

        assertEquals("Error: The TRAILER WITH TRAILER ID AND MOVIE ID URI was matched incorrectly",
                testMatcher.match(TEST_A_MOVIE_TRAILER_ITEM),
                MovieProvider.A_MOVIE_TRAILER);

    }

    public void testUriMatcherReviewTable() {

        UriMatcher testMatcher = MovieProvider.buildUriMatcher();

        assertEquals("Error: The TRAILER URI was matched incorrectly.",
                testMatcher.match(TEST_REVIEW_DIR),
                MovieProvider.REVIEW);

        assertEquals("Error: The REVIEW WITH MOVIE ID URI was matched incorrectly",
                testMatcher.match(TEST_REVIEW_MOVIE_DIR),
                MovieProvider.MOVIE_REVIEWS);

        assertEquals("Error: The REVIEW WITH REVIEW ID AND MOVIE ID URI was matched incorrectly",
                testMatcher.match(TEST_REVIEW_MOVIE_REVIEW_DIR),
                MovieProvider.A_MOVIE_REVIEW);

    }

}
