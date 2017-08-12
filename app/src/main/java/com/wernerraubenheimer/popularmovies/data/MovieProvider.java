package com.wernerraubenheimer.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.content.pm.Signature;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

import com.wernerraubenheimer.popularmovies.Movie;

/**
 * Created by wernerr on 2015/09/12.
 */
public class MovieProvider extends ContentProvider {

    // Database helper
    MovieDbHelper mOpenHelper;

    // Uri's
    static final UriMatcher mUriMatcher = buildUriMatcher();

    static final int MOVIE = 100;
    static final int A_MOVIE = 101;
    static final int MOVIE_WITH_ORIGINAL_TITLE = 102;

    static final int TRAILER = 200;
    static final int MOVIE_TRAILERS = 201;
    static final int A_MOVIE_TRAILER = 202;

    static final int REVIEW = 300;
    static final int MOVIE_REVIEWS = 301;
    static final int A_MOVIE_REVIEW = 302;

    // Query builders
    private static final SQLiteQueryBuilder sMovieTrailersQueryBuilder;
    private static final SQLiteQueryBuilder sReviewByMovieQueryBuilder;

    /**
     *  Movie Table
     */
    // movie.orginal_title = ?
    private static final String sMovieOriginalTitle =
            MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry.COLUMN_TITLE + " = ? ";

    // movie.favourite = ?
    private static final String sMovieFavourite =
            MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry.COLUMN_FAVOURITE + " = ? ";

    // movie.id = ?
    private static final String sMovieId =
            MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry._ID + " = ? ";

    /**
     *  Trailer Table
     */
    static {
        sMovieTrailersQueryBuilder = new SQLiteQueryBuilder();

        // This is an inner join that looks like
        // trailer INNER JOIN movie ON trailer.movie_id = movie._id
        sMovieTrailersQueryBuilder.setTables(
                        MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.TrailerEntry.TABLE_NAME + " ON " +
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = " +
                        MovieContract.TrailerEntry.TABLE_NAME + "." + MovieContract.TrailerEntry.COLUMN_MOVIE_ID);

    }

    private static final String sMovieWithTrailer =
            MovieContract.TrailerEntry.TABLE_NAME + "." +
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ? AND " +
            MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry._ID + " = ? ";

    /**
     *  Review Table
     */
    static {
        sReviewByMovieQueryBuilder = new SQLiteQueryBuilder();

        // This is an inner join that looks like
        // review INNER JOIN movie ON review.movie_id = movie._id
        sReviewByMovieQueryBuilder.setTables(
                        MovieContract.MovieEntry.TABLE_NAME + " INNER JOIN " +
                        MovieContract.ReviewEntry.TABLE_NAME + " ON " +
                        MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID + " = " +
                        MovieContract.ReviewEntry.TABLE_NAME + "." + MovieContract.ReviewEntry.COLUMN_MOVIE_ID);
    }

    // review._id = ? AND movie.id = ?
    private static final String sMovieWithReview =
            MovieContract.ReviewEntry.TABLE_NAME + "." +
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? AND " +
            MovieContract.MovieEntry.TABLE_NAME + "." +
            MovieContract.MovieEntry._ID + " = ? ";


    @Override
    public boolean onCreate() {
        mOpenHelper = new MovieDbHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        /** movie table **/
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_MOVIE,
                          MOVIE);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_MOVIE + "/#",
                          A_MOVIE);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_MOVIE + "/*",
                          MOVIE_WITH_ORIGINAL_TITLE);


        /** trailer table **/
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_TRAILER,
                          TRAILER);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_TRAILER + "/" + MovieContract.PATH_MOVIE + "/#",
                          MOVIE_TRAILERS);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_TRAILER + "/*/" + MovieContract.PATH_MOVIE + "/#",
                          A_MOVIE_TRAILER);

        /** review table **/
        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_REVIEW,
                          REVIEW);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_REVIEW + "/" + MovieContract.PATH_MOVIE + "/#",
                          MOVIE_REVIEWS);

        uriMatcher.addURI(MovieContract.CONTENT_AUTHORITY,
                          MovieContract.PATH_REVIEW + "/*/" + MovieContract.PATH_MOVIE + "/#",
                          A_MOVIE_REVIEW);

        return uriMatcher;
    }

    @Override
    public String getType(Uri uri) {
        //Use the Uri Matcher to determine what kind of Uri this is
        final int match = mUriMatcher.match(uri);

        switch (match) {
            case MOVIE:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case A_MOVIE:
                return MovieContract.MovieEntry.CONTENT_ITEM_TYPE;

            case MOVIE_WITH_ORIGINAL_TITLE:
                return MovieContract.MovieEntry.CONTENT_TYPE;

            case TRAILER:
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            case MOVIE_TRAILERS:
                return MovieContract.TrailerEntry.CONTENT_TYPE;

            case A_MOVIE_TRAILER:
                return MovieContract.TrailerEntry.CONTENT_ITEM_TYPE;

            case REVIEW:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case MOVIE_REVIEWS:
                return MovieContract.ReviewEntry.CONTENT_TYPE;

            case A_MOVIE_REVIEW:
                return MovieContract.ReviewEntry.CONTENT_ITEM_TYPE;

            default:
                throw new UnsupportedOperationException("Unknown uri :" + uri);
        }

    }

    private Cursor getMovies (String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getMovieByOriginalTitle (Uri uri, String[] projection, String sortOrder) {

        String originalTitle = MovieContract.MovieEntry.getOriginalTitleFromUri(uri);

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                sMovieOriginalTitle,
                new String[]{originalTitle},
                null,
                null,
                sortOrder);
    }

    public Cursor getAMovie(Uri uri, String[] projection, String sortOrder) {

        long movieId = ContentUris.parseId(uri);
        String selection = MovieContract.MovieEntry._ID + " = ? ";
        String[] selectionArgs = new String[]{Long.toString(movieId)};

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.MovieEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
    }

    private Cursor getTrailers(String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.TrailerEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieTrailers(Uri trailerUri, String[] projection, String sortOrder)  {

        String movieStringId = MovieContract.TrailerEntry.getMovieIdFromUri(trailerUri);

        return sMovieTrailersQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sMovieId,
                new String[]{movieStringId},
                null, // group by clause
                null, // having by clause
                sortOrder
        );
    }

    private Cursor getAMovieTrailer(Uri trailerUri, String[] projection, String sortOrder) {

        String trailerStringId = MovieContract.TrailerEntry.getTrailerIdFromUri(trailerUri);
        String movieStringId = MovieContract.TrailerEntry.getMovieIdFromUri(trailerUri);

        Cursor cursor = sMovieTrailersQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sMovieWithTrailer,
                new String[]{trailerStringId, movieStringId},
                null,
                null,
                sortOrder
        );

        return cursor;
    }

    private Cursor getReviews(String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        return mOpenHelper.getReadableDatabase().query(
                MovieContract.ReviewEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder
        );
    }

    private Cursor getMovieReviews(Uri reviewUri, String[] projection, String sortOrder) {

        String movieStringId = MovieContract.ReviewEntry.getMovieIdFromUri(reviewUri);

        return sReviewByMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sMovieId,
                new String[]{movieStringId},
                null,
                null,
                sortOrder
        );
    }

    private Cursor getAMovieReview(Uri reviewUri, String[] projection, String sortOrder) {

        String reviewStringId = MovieContract.ReviewEntry.getReviewIdFromUri(reviewUri);
        String movieStringId = MovieContract.ReviewEntry.getMovieIdFromUri(reviewUri);

        return sReviewByMovieQueryBuilder.query(
                mOpenHelper.getReadableDatabase(),
                projection,
                sMovieWithReview,
                new String[]{reviewStringId, movieStringId},
                null,
                null,
                sortOrder
        );
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        // Here's the switch statement that, given a URI, will determine what kind of request it is,
        // and query the database accordingly.

        Cursor retCursor;

        switch (mUriMatcher.match(uri)) {

            // "movies"
            case MOVIE: {
                retCursor = getMovies (projection, selection, selectionArgs, sortOrder);
                break;
            }
            case A_MOVIE: {
                retCursor = getAMovie(uri, projection, sortOrder);
                break;
            }
            case MOVIE_WITH_ORIGINAL_TITLE: {
                retCursor = getMovieByOriginalTitle (uri, projection, sortOrder);
                break;
            }
            // "trailers"
            case TRAILER:{
                retCursor = getTrailers(projection, selection, selectionArgs, sortOrder);
                break;
            }
            case MOVIE_TRAILERS: {
                retCursor = getMovieTrailers(uri, projection, sortOrder);
                break;
            }
            case A_MOVIE_TRAILER: {
                retCursor = getAMovieTrailer(uri, projection, sortOrder);
                break;
            }
            // "reviews"
            case REVIEW: {
                retCursor = getReviews(projection, selection, selectionArgs, sortOrder);
                break;
            }
            case MOVIE_REVIEWS: {
                retCursor = getMovieReviews(uri, projection, sortOrder);
                break;
            }
            case A_MOVIE_REVIEW: {
                retCursor = getAMovieReview(uri, projection, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        Uri returnUri;
        long _id;

        switch (match) {
            case MOVIE:
                _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.MovieEntry.buildMovieUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into table movie " + uri);
                break;
            case TRAILER:
                _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.TrailerEntry.buildTrailerUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into table trailer " + uri);
                break;
            case REVIEW:
                _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, values);
                if (_id > 0)
                    returnUri = MovieContract.ReviewEntry.buildReviewUri(_id);
                else
                    throw new android.database.SQLException("Failed to insert row into table review " + uri);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int matcher = mUriMatcher.match(uri);
        int rowsDeleted = -1;

        if (selection == null)
            selection = "1";

        switch (matcher) {
            case MOVIE:
                rowsDeleted = db.delete(MovieContract.MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case TRAILER:
                rowsDeleted = db.delete(MovieContract.TrailerEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case REVIEW:
                rowsDeleted = db.delete(MovieContract.ReviewEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        //if (rowsDeleted == 0)
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int matcher = mUriMatcher.match(uri);
        int rowsUpdated = -1;

        switch (matcher) {
            case MOVIE:
                rowsUpdated = db.update(MovieContract.MovieEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case TRAILER:
                rowsUpdated = db.update(MovieContract.TrailerEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case REVIEW:
                rowsUpdated = db.update(MovieContract.ReviewEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri " + uri);
        }

        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }


    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = mUriMatcher.match(uri);
        int returnCount;

        switch (match) {
            case MOVIE:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.MovieEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case TRAILER:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.TrailerEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case REVIEW:
                db.beginTransaction();
                returnCount = 0;
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(MovieContract.ReviewEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}