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
public class FetchTrailersTask extends AsyncTask<String, Void, Void> {

    private final String LOG_TAG = FetchTrailersTask.class.getSimpleName();

    /** http://api.themoviedb.org/3/movie/157336/videos?api_key=c5bb61548479c734a743969fdc45339e **/
    private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/";
    private final String TRAILERS_PATH = "videos";
    private final String PARAM_API_KEY = "api_key";

    private final Context mContext;

    private String[] TRAILER_COLUMNS = {
            MovieContract.TrailerEntry._ID,
            MovieContract.TrailerEntry.COLUMN_MOVIE_ID,
            MovieContract.TrailerEntry.COLUMN_TRAILER_ID,
            MovieContract.TrailerEntry.COLUMN_ISO_639_1,
            MovieContract.TrailerEntry.COLUMN_ISO_3166_1,
            MovieContract.TrailerEntry.COLUMN_KEY,
            MovieContract.TrailerEntry.COLUMN_NAME,
            MovieContract.TrailerEntry.COLUMN_SITE,
            MovieContract.TrailerEntry.COLUMN_SIZE,
            MovieContract.TrailerEntry.COLUMN_TYPE,
            MovieContract.TrailerEntry.COLUMN_URL
    };

    static final int COL_ID = 0;
    static final int COL_MOVIE_ID = 1;
    static final int COL_TRAILER_ID = 2;
    static final int COL_ISO_639_1 = 3;
    static final int COL_ISO_3166_1 = 4;
    static final int COL_KEY = 5;
    static final int COL_NAME = 6;
    static final int COL_SITE = 7;
    static final int COL_SIZE = 8;
    static final int COL_TYPE = 9;
    static final int COL_URL = 10;

    public FetchTrailersTask(Context context) {
        mContext = context;
    }

    /**
     * Helper method to determine if movie exists already in database
     * @param trailerID
     * @return boolean
     */
    private boolean checkIsTrailer(String trailerID) {

        String selection = MovieContract.TrailerEntry.COLUMN_TRAILER_ID + " = ? ";
        String[] selectionArgs = {trailerID};

        Cursor trailerCursor = mContext.getContentResolver().query(
                MovieContract.TrailerEntry.CONTENT_URI,
                new String[]{TRAILER_COLUMNS[COL_TRAILER_ID]},
                selection,
                selectionArgs,
                null
        );

        if (trailerCursor.moveToFirst())
            return true;
        else
            return false;
    }

    /**
     * Take the String representing the complete trailer by movie list in JSON Format and
     * pull out the data o construct the Strings needed for the wireframes.
     */
    private void getTrailerDataFromJson(String trailerJsonStr, String movieId) throws JSONException {

        // Values returned by API
        final String TMDB_TRAILER_ID = "id";
        final String TMDB_TRAILER_ISO_639_1 = "iso_639_1";
        final String TMDB_TRAILER_ISO_3166_1 = "iso_3166_1";
        final String TMDB_TRAILER_KEY = "key";
        final String TMDB_TRAILER_NAME = "name";
        final String TMDB_TRAILER_SITE = "site";
        final String TMDB_TRAILER_SIZE = "size";
        final String TMDB_TRAILER_TYPE = "type";
        final String TMDB_TRAILER_URL = "https://www.youtube.com/watch?v=";
        final String TMDB_TRAILER_LIST = "results";

        try {
            JSONObject trailerJson = new JSONObject(trailerJsonStr);
            JSONArray trailerJSONArray = trailerJson.getJSONArray(TMDB_TRAILER_LIST);

            Vector<ContentValues> contentValuesVector = new Vector<>(trailerJSONArray.length());

            for (int i = 0; i < trailerJSONArray.length(); i++) {
                JSONObject trailerJSONObject = trailerJSONArray.getJSONObject(i);

                String trailerId = trailerJson.getString(TMDB_TRAILER_ID);
                //if the trailer is already in the DB don't add again.
                if (checkIsTrailer(trailerId) ) continue;

                String iso_639_1 = trailerJSONObject.getString(TMDB_TRAILER_ISO_639_1);
                String iso_3166_1 = trailerJSONObject.getString(TMDB_TRAILER_ISO_3166_1);
                String key = trailerJSONObject.getString(TMDB_TRAILER_KEY);
                String name = trailerJSONObject.getString(TMDB_TRAILER_NAME);
                String site = trailerJSONObject.getString(TMDB_TRAILER_SITE);
                String size = trailerJSONObject.getString(TMDB_TRAILER_SIZE);
                String type = trailerJSONObject.getString(TMDB_TRAILER_TYPE);
                String url = TMDB_TRAILER_URL + key;


                ContentValues trailerContentValues = new ContentValues();
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_MOVIE_ID, movieId);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_TRAILER_ID, trailerId);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_ISO_639_1, iso_639_1);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_ISO_3166_1, iso_3166_1);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_KEY, key);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_NAME, name);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_SITE, site);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_SIZE, size);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_TYPE, type);
                trailerContentValues.put(MovieContract.TrailerEntry.COLUMN_URL, url);

                contentValuesVector.add(trailerContentValues);
            }

            int inserted = 0;
            if (contentValuesVector.size() > 0) {
                ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                contentValuesVector.toArray(contentValuesArray);
                inserted = mContext.getContentResolver().bulkInsert(MovieContract.TrailerEntry.CONTENT_URI, contentValuesArray);
            }
            Log.d(LOG_TAG, "Inserted + " + inserted + " trailers.");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }

    }

    @Override
    protected Void doInBackground(String... params) {

        String movieId = params[0];
        //Build the URL string using the URIBuilder class
        /**http://api.themoviedb.org/3/movie/157336/videos?api_key=c5bb61548479c734a743969fdc45339e **/
        Uri uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(TRAILERS_PATH)
                .appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIES_API_KEY)
                .build();


        //These two needs to be declared outside the try/catch block
        //so that they can be closed in the finally block
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        //Will contain the raw JSON string
        String trailerJsonStr = null;

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

            trailerJsonStr = buffer.toString();
            getTrailerDataFromJson(trailerJsonStr, movieId);

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