package com.wernerraubenheimer.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.URLUtil;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.wernerraubenheimer.popularmovies.data.MovieContract;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Created by Werner on 2015/08/11.
 * I used example of Creating custom ArrayAdapter tutorial
 * Found other example code here:
 * http://javatechig.com/android/android-gridview-example-building-image-gallery-in-android
 *
 **/
public class GridViewAdapter extends CursorAdapter {

    private static final String LOG_TaG = GridViewAdapter.class.getSimpleName();

    private String[] MOVIE_COLUMNS = {
            MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_TITLE,
            MovieContract.MovieEntry.COLUMN_POSTER_PATH,
            MovieContract.MovieEntry.COLUMN_OVERVIEW,
            MovieContract.MovieEntry.COLUMN_RELEASE_DATE,
            MovieContract.MovieEntry.COLUMN_VOTING_AVERAGE,
            MovieContract.MovieEntry.COLUMN_FAVOURITE
    };

    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_TITLE = 1;
    static final int COL_MOVIE_POSTER_PATH = 2;
    static final int COL_MOVIE_OVERVIEW = 3;
    static final int COL_MOVIE_RELEASE_DATE = 4;
    static final int COL_MOVIE_VOTE_AVERAGE =5;
    static final int COL_MOVIE_FAVOURITE = 6;

    /**This is my own custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the List is the data I want
     * to populate into the lists
     *
     * @param context        The current context. Used to inflate the layout file.
     * @param cursor      A List of Movie objects to display in a list
     */
    public GridViewAdapter(Context context, Cursor cursor, int flags) {
      super(context, cursor, flags);
    }


//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        // Gets the AndroidFlavor object from the ArrayAdapter at the appropriate position
//        Movie movie = getItem(position);
//
//        // Adapters recycle views to AdapterViews.
//        // If this is a new View object we're getting, then inflate the layout.
//        // If not, this view already has the layout inflated from a previous call to getView,
//        // and we modify the View widgets as usual.
//        if (convertView == null) {
//            convertView = LayoutInflater.from(getContext()).inflate(R.layout.grid_item_movie, parent, false);
//        }
//
//        if (URLUtil.isValidUrl(movie.getPosterPath())) {
//            ImageView imageView = (ImageView)convertView.findViewById(R.id.grid_item_movie_imageview);
//            Picasso.with(parent.getContext()).load(movie.getPosterPath()).into(imageView);
//        }
//
//
//        return  convertView;
//
//    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid_item_movie, parent, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        String posterPath = cursor.getString(COL_MOVIE_POSTER_PATH);
        boolean isValid = URLUtil.isValidUrl(posterPath);
        if (URLUtil.isValidUrl(cursor.getString(COL_MOVIE_POSTER_PATH))) {
            ImageView imageView = (ImageView)view.findViewById(R.id.grid_item_movie_imageview);
            Picasso.with(context).load(cursor.getString(COL_MOVIE_POSTER_PATH)).into(imageView);
        }
    }

}
