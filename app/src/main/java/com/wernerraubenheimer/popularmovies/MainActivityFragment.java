package com.wernerraubenheimer.popularmovies;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;

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
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment  implements LoaderManager.LoaderCallbacks<Cursor> {

    private final String LOG_TAG = MainActivityFragment.class.getSimpleName();

    private ArrayList<Movie> movieList;
    private Movie[] movieResults = {new Movie()};
    private final String PARCELABLE_STATE_MOVIES = "movies";
    private final String STATE_SORT_BY = "sort_by";
    SharedPreferences shared_preferences;
    SharedPreferences.Editor shared_preferences_editor;
    private String sort_by_preference;
    private String show_movies_preference;


    private GridViewAdapter mMovieAdapter;
    private static final int MOVIE_LOADER_ID = 1;
    private static final int CODE_PREFERENCES = 1;

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

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Toast.makeText(getActivity(), "In onCreate()",Toast.LENGTH_LONG).show();
//        if (savedInstanceState == null || !savedInstanceState.containsKey(PARCELABLE_STATE_MOVIES)) {
//            updateMovies();
//            movieList = new ArrayList<>(Arrays.asList(movieResults));
//        } else {
//            movieList = savedInstanceState.getParcelableArrayList(PARCELABLE_STATE_MOVIES);
//            sort_by_preference = savedInstanceState.getString(STATE_SORT_BY);
//        }
//        mMovieAdapter = new GridViewAdapter(getActivity(), movieList);
        setHasOptionsMenu(true);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(PARCELABLE_STATE_MOVIES, movieList);
        outState.putString(STATE_SORT_BY, sort_by_preference);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.action_settings){
            Intent settingsIntent = new Intent(getActivity(), SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        mMovieAdapter = new GridViewAdapter(getActivity(), null, 0);

        GridView movie_grid_view = (GridView)rootView.findViewById(R.id.grid_view_movie);
        movie_grid_view.setAdapter(mMovieAdapter);

        movie_grid_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Cursor movieCursor = (Cursor)adapterView.getItemAtPosition(position);
                if (movieCursor != null) {
                    String title = movieCursor.getString(COL_MOVIE_TITLE);
                    String _id = movieCursor.getString(COL_MOVIE_ID); /** test 7 aug **/
                    Intent detailIntent = new Intent(getActivity(), DetailActivity.class)
//                            .setData(MovieContract.MovieEntry.buildMovieTitleUri(title));
                               .setData(MovieContract.MovieEntry.buildMovieUri(Long.parseLong(_id)));
                    startActivity(detailIntent);
                }
            }
        });

        return rootView;
    }

//    private void updateMovies() {
//
//        shared_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
//        sort_by_preference = shared_preferences.getString(getString(R.string.pref_sort_by_key),
//                getString(R.string.pref_sort_by_default_value));
//        show_movies_preference = shared_preferences.getString(getString(R.string.pref_show_which_key),
//                getString(R.string.pref_show_by_default_value));
//
//        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
//        fetchMoviesTask.execute(sort_by_preference, show_movies_preference);
//    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
//        updateMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        getLoaderManager().restartLoader(MOVIE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String selection;
        String sortOrder;

        shared_preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sort_by_preference = shared_preferences.getString(getString(R.string.pref_sort_by_key),
                getString(R.string.pref_sort_by_default_value));
        show_movies_preference = shared_preferences.getString(getString(R.string.pref_show_which_key),
                getString(R.string.pref_show_by_default_value));

        if (sort_by_preference.equals(getString(R.string.pref_sort_by_default_value))) {
            sortOrder = MovieContract.MovieEntry.COLUMN_POPULARITY + " DESC ";
        } else {
            sortOrder = MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE + " DESC ";
        }

        if (show_movies_preference.equals(getString(R.string.pref_show_by_default_value))) {
            selection = MovieContract.MovieEntry.COLUMN_FAVOURITE + " = 0 ";

        } else {
            selection = MovieContract.MovieEntry.COLUMN_FAVOURITE + " > 0 ";
        }

        FetchMoviesTask fetchMoviesTask = new FetchMoviesTask(getActivity());
        fetchMoviesTask.execute(sort_by_preference, show_movies_preference);

        return new CursorLoader(
                getActivity(),
                MovieContract.MovieEntry.CONTENT_URI,
                MOVIE_COLUMNS,
                selection,
                null,
                sortOrder
        );

    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
        mMovieAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> cursorLoader) {
        mMovieAdapter.swapCursor(null);
    }
}
