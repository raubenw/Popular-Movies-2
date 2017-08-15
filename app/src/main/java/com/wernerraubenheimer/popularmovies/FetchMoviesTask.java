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

/**
 * Created by wernerr on 2015/09/25.
 */
public class FetchMoviesTask extends AsyncTask<String, Void, Void> {

        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        private final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie";
        private final String PARAM_SORT_BY = "sort_by";
        private final String PARAM_API_KEY = "api_key";

        private final Context mContext;
        private GridViewAdapter mMovieAdapter;

        private String[] MOVIE_COLUMNS = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_TITLE,
                MovieContract.MovieEntry.COLUMN_POSTER_PATH,
                MovieContract.MovieEntry.COLUMN_OVERVIEW,
                MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
                MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE,
                MovieContract.MovieEntry.COLUMN_POPULARITY,
                MovieContract.MovieEntry.COLUMN_FAVOURITE
        };

        static final int COL_MOVIE_ID = 0;
        static final int COL_MOVIE_TITLE = 1;
        static final int COL_MOVIE_POSTER_PATH = 2;
        static final int COL_MOVIE_OVERVIEW = 3;
        static final int COL_MOVIE_RELEASE_DATE = 4;
        static final int COL_MOVIE_VOTE_AVERAGE = 5;
        static final int COL_MOVIE_POPULARITY = 6;
        static final int COL_MOVIE_FAVOURITE = 7;

        public FetchMoviesTask(Context context) {
            mContext = context;
        }

    /**
     * Helper method to determine if movie exists already in database
     * @param movieID
     * @return boolean
     */
    private boolean checkIsMovie(String movieID) {

        String selection = MovieContract.MovieEntry._ID + " = ? ";
        String[] selectionArgs = {movieID};

            Cursor movieCursor = mContext.getContentResolver().query(
                    MovieContract.MovieEntry.CONTENT_URI,
                    new String[]{MOVIE_COLUMNS[COL_MOVIE_ID]},
                    selection,
                    selectionArgs,
                    null
            );

            if (movieCursor.moveToFirst())
                return true;
            else
                return false;
        }

        /**
         * Take the String representing the complete movie list in JSON Format and
         * pull out the data o construct the Strings needed for the wireframes.
         */
        private void getMovieDataFromJson(String movieJsonStr) throws JSONException {

            // Values returned by API
            final String TMDB_MOVIE_ID = "id";
            final String TMDB_MOVIE_LIST = "results";
            final String TMDB_ORIGINAL_TITLE = "original_title";
            final String TMDB_POSTER_PATH = "poster_path";
            final String TMDB_OVERVIEW = "overview";
            final String TMDB_VOTE_AVERAGE = "vote_average";
            final String TMDB_POPULARITY = "popularity";
            final String TMDB_RELEASE_DATE = "release_date";
            final String TMDB_POSTER_URL = "http://image.tmdb.org/t/p/w185";

            try {
                JSONObject movieJson = new JSONObject(movieJsonStr);
                JSONArray movieJSONArray = movieJson.getJSONArray(TMDB_MOVIE_LIST);

                Vector<ContentValues> contentValuesVector = new Vector<>(movieJSONArray.length());

                for (int i = 0; i < movieJSONArray.length(); i++) {
                    JSONObject movieJSONObject = movieJSONArray.getJSONObject(i);

                    String movieId = movieJSONObject.getString(TMDB_MOVIE_ID);
                    //if the movie is already in the DB don't add again.
                    if (checkIsMovie(movieId) ) continue;

                    String title = movieJSONObject.getString(TMDB_ORIGINAL_TITLE);
                    String releaseDate = movieJSONObject.getString(TMDB_RELEASE_DATE);
                    String posterPath = TMDB_POSTER_URL + movieJSONObject.getString(TMDB_POSTER_PATH);
                    String overview = movieJSONObject.getString(TMDB_OVERVIEW);
                    double voterAverage = Math.round(movieJSONObject.getDouble(TMDB_VOTE_AVERAGE) * 100) / 100;
                    double popularity = Math.round(movieJSONObject.getDouble(TMDB_POPULARITY) * 100) / 100;


                    ContentValues movieContentValues = new ContentValues();
                    movieContentValues.put(MovieContract.MovieEntry._ID, movieId);
                    movieContentValues.put(MovieContract.MovieEntry.COLUMN_TITLE, title);
                    movieContentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, posterPath);
                    movieContentValues.put(MovieContract.MovieEntry.COLUMN_OVERVIEW, overview);
                    movieContentValues.put(MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE, voterAverage);
                    movieContentValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, popularity);
                    movieContentValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, releaseDate);

                    contentValuesVector.add(movieContentValues);
                }

                int inserted = 0;
                if (contentValuesVector.size() > 0) {
                    ContentValues[] contentValuesArray = new ContentValues[contentValuesVector.size()];
                    contentValuesVector.toArray(contentValuesArray);
                    inserted = mContext.getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, contentValuesArray);
                }
                Log.d(LOG_TAG, "Inserted + " + inserted + " movies.");

            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }

        }

        @Override
        protected Void doInBackground(String... params) {

            final String SORT_BY_TOP_RATED =  mContext.getString(R.string.value_sort_by_top_rated);
            final String SORT_BY_POPULAR = mContext.getString(R.string.value_default_sort_by);
            String sortPath="";
            //Build the URL string using the URIBuilder class
            if (params[0].equals(SORT_BY_POPULAR)) {
                sortPath = mContext.getString(R.string.sort_by_popular);
            } else if (params[0].equals(SORT_BY_TOP_RATED)) {
                sortPath = mContext.getString(R.string.sort_by_top_rated);
            }
            Uri uriBuilder = Uri.parse(MOVIE_BASE_URL).buildUpon()
                    .appendPath(sortPath)
                    .appendQueryParameter(PARAM_API_KEY, BuildConfig.MOVIES_API_KEY)
                    .build();


            //These two needs to be declared outside the try/catch block
            //so that they can be closed in the finally block
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            //Will contain the raw JSON string
            String movieJsonStr = null;

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

                movieJsonStr = buffer.toString();
                getMovieDataFromJson(movieJsonStr);

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