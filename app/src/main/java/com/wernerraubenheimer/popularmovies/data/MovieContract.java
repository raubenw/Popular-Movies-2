package com.wernerraubenheimer.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import java.util.Date;
import android.text.format.Time;
import android.util.Log;

/**
 * Created by wernerr on 2015/09/05.
 */
public class MovieContract {

    // The "Content authority" is a name for the entire content provider, similar to the
    // relationship between a domain name and its website.  A convenient string to use for the
    // content authority is the package name for the app, which is guaranteed to be unique on the
    // device.
    public static final String CONTENT_AUTHORITY = "com.wernerraubenheimer.popularmovies";

    // Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
    // the content provider.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    // Possible paths (appended to base content URI for possible URI's)
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    /**
       Inner class that defines the table contents of the movie table
    */
    public static final class MovieEntry implements BaseColumns {

        public static final String TABLE_NAME ="movie";
        public static final String COLUMN_TITLE = "original_title";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";
        public static final String COLUMN_VOTING_AVERAGE = "vote_average";
        public static final String COLUMN_POPULARITY = "popularity";
        public static final String COLUMN_FAVOURITE = "favourite";


        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static Uri buildMovieUri(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieTitleUri(String originalTitle) {
            return CONTENT_URI.buildUpon().appendPath(originalTitle).build();
        }

        public static String getOriginalTitleFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /*
     *   Inner class that defines the table contents of the trailer table
     */
    public static final class TrailerEntry implements BaseColumns {

        public static final String TABLE_NAME = "trailer";
        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ISO_639_1 = "iso_639_1";
        public static final String COLUMN_ISO_3166_1 = "iso_3166_1";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_URL = "url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static Uri buildTrailerUri(long trailerId) {
            return ContentUris.withAppendedId(CONTENT_URI, trailerId);
        }

        public static Uri buildMovieTrailersUri(long movieId) {
            Uri returnUri = CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId)).build();
            return returnUri;
        }

        public static Uri buildAMovieTrailerUri(String trailerId, long movieId) {
            Uri returnUri = CONTENT_URI.buildUpon()
                    .appendPath(trailerId)
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId))
                    .build();
            return returnUri;
        }

        public static String getTrailerIdFromUri(Uri uri) {
            String someSegment = uri.getFragment();
            String returnSegment = uri.getPathSegments().get(1);
            return returnSegment;
        }

        public static String getMovieIdFromUri(Uri uri) {
            String returnSegment = uri.getLastPathSegment();
            return returnSegment;
        }
    }

    /*
     *  Inner class that defines the table contents of the review table
     */
    public static final class ReviewEntry implements BaseColumns {

        public static final String TABLE_NAME = "review";
        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";
        public static final String COLUMN_URL = "url";

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;


        public static Uri buildReviewUri(long reviewId) {
            return ContentUris.withAppendedId(CONTENT_URI, reviewId);
        }

        public static Uri buildMovieReviewsUri(long movieId) {
            Uri returnUri = CONTENT_URI.buildUpon()
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId)).build();
            return returnUri;
        }

        public static Uri buildAMovieReviewUri(String reviewId, long movieId) {
            Uri returnUri = CONTENT_URI.buildUpon()
                    .appendPath(reviewId)
                    .appendPath(PATH_MOVIE)
                    .appendPath(Long.toString(movieId))
                    .build();
            return returnUri;
        }

        public static String getReviewIdFromUri(Uri uri) {
            String returnSegment = uri.getPathSegments().get(1);
            return returnSegment;
        }

        public static String getMovieIdFromUri(Uri uri) {
            String returnSegment = uri.getLastPathSegment();
            return returnSegment;
        }

    }

}

