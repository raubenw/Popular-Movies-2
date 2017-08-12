package com.wernerraubenheimer.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.wernerraubenheimer.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.wernerraubenheimer.popularmovies.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

/**
 * Created by wernerr on 2015/09/25.
 */
public class FetchReviewsTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchReviewsTask.class.getSimpleName();

    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String REVIEWS_PATH = "reviews";
    private final String PARAM_API_KEY = "api_key";

    private final Context mContext;

    private String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_URL
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_REVIEW_ID = 2;
    static final int COL_AUTHOR = 3;
    static final int COL_CONTENT = 4;
    static final int COL_URL = 5;

    public FetchReviewsTask(Context context) {
        mContext = context;
    }

    /**
     * Helper method to determine if movie exists already in database
     * @param reviewID
     * @return boolean
     */
    private boolean checkIsReview(String reviewID) {

        String selection = MovieContract.ReviewEntry.COLUMN_REVIEW_ID + " = ? ";
        String[] selectionArgs = {reviewID};

        Cursor reviewCursor = mContext.getContentResolver().query(
                MovieContract.ReviewEntry.CONTENT_URI,
                new String[]{REVIEW_COLUMNS[COL_REVIEW_ID]},
                selection,
                selectionArgs,
                null
        );

        if (reviewCursor.moveToFirst())
            return true;
        else
            return false;
    }

    /**
     * Take the String representing the complete movie list in JSON Format and
     * pull out the data o construct the Strings needed for the wireframes.
     */
    private void getReviewDataFromJson(String reviewJsonStr, String movieId) throws JSONException {

        // Values returned by API
        final String TMDB_REVIEW_ID = "id";
        final String TMDB_REVIEW_AUTHOR = "author";
        final String TMDB_REVIEW_CONTENT = "content";
        final String TMDB_REVIEW_URL = "url";
        final String TMDB_REVIEW_LIST = "results";

        try {
            JSONObject reviewJson = new JSONObject(reviewJsonStr);
            JSONArray reviewJSONArray = reviewJson.getJSONArray(TMDB_REVIEW_LIST);

            Vector<ContentValues> contentValuesVector = new Vector<>(reviewJSONArray.length());

            for (int i = 0; i < reviewJSONArray.length(); i++) {
                JSONObject reviewJSONObject = reviewJSONArray.getJSONObject(i);

                String reviewId = reviewJSONObject.getString(TMDB_REVIEW_ID);
                //if the movie is already in the DB don't add again.
                if (checkIsReview(reviewId) ) continue;

                String author = reviewJSONObject.getString(TMDB_REVIEW_AUTHOR);
                String content = reviewJSONObject.getString(TMDB_REVIEW_CONTENT);
                String url = reviewJSONObject.getString(TMDB_REVIEW_URL);


                ContentValues reviewContentValues = new ContentValues();
                reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_MOVIE_ID, movieId);
                reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_REVIEW_ID, reviewId);
                reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_AUTHOR, author);
                reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_CONTENT, content);
                reviewContentValues.put(MovieContract.ReviewEntry.COLUMN_URL, url);

                contentValuesVector.add(reviewContentValues);
            }

            int inserted = 0;
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValuesArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.ReviewEntry.CONTENT_URI, contentValuesArray);
            }
            Log.d(LOG_TAG, "Inserted + " + inserted + " reviews.");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(String... params) {

        String movieId = params[0];
        //Build the URL string using the URIBuilder class
        Uri uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(REVIEWS_PATH)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIES_API_KEY)
                .build();


        //These two needs to be declared outside the try/catch block
        //so that they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the raw JSON string
        String reviewJsonStr = null;

        String line = null;
        try {
            URL url = new URL(uriBuilder.toString());
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();

            if (inputStream == null) {
                return  null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                return null;
            }

            reviewJsonStr = buffer.toString();
            getReviewDataFromJson(reviewJsonStr, movieId);

        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error trying to parse the JSON String");
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error connecting to URL");
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error opening the URL Connection");
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error trying to close reader");
                }
            }
        }
        return null;
    }
}