package com.wernerraubenheimer.popularmovies;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.icu.text.DisplayContext;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.wernerraubenheimer.popularmovies.data.MovieContract;
import com.wernerraubenheimer.popularmovies.data.MovieProvider;
//import com.wernerraubenheimer.popularmovies.data.MovieProvider;


/**
 * A placeholder fragment containing a simple view.
 */
public class DetailActivityFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = DetailActivity.class.getSimpleName();

    private static final String MOVIE_SHARE_HASHTAG = " #PopularMovies";
    private ShareActionProvider mShareActionProvider;
    private String mShareMovieTitle;
    private String mMovieID;

    private static final int MOVIE_LOADER_ID = 1;
    private static final int REVIEW_LOADER_ID = 2;
    private static final int TRAILER_LOADER_ID = 3;

    TextView titleView;
    TextView releaseDateView;
    TextView averageScoreView;
    TextView popularityView;
    TextView overviewHeading;
    ImageView imageView;
    ImageView starView;

    RecyclerView mReviewRecyclerView;
    RecyclerView mTrailerRecyclerView;
    TextView reviewsHeading;

    ReviewAdapter mReviewAdapter;
    TrailerAdapter mTrailerAdapter;
    TextView trailersHeading;

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

    private String[] REVIEW_COLUMNS = {
            MovieContract.ReviewEntry._ID,
            MovieContract.ReviewEntry.COLUMN_REVIEW_ID,
            MovieContract.ReviewEntry.COLUMN_MOVIE_ID,
            MovieContract.ReviewEntry.COLUMN_AUTHOR,
            MovieContract.ReviewEntry.COLUMN_CONTENT,
            MovieContract.ReviewEntry.COLUMN_URL
    };

    static final int COL_REVIEW_ID = 0;
    static final int COL_REVIEW_STRING_ID = 1;
    static final int COL_REVIEW_MOVIE_ID = 2;
    static final int COL_REVIEW_AUTHOR = 3;
    static final int COL_REVIEW_CONTENT = 4;
    static final int COL_REVIEW_URL = 5;

    static int isFavourite;

    public DetailActivityFragment() {
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        titleView = (TextView) rootView.findViewById(R.id.originalTitle);
        overviewHeading = (TextView)rootView.findViewById(R.id.overView_heading);
        releaseDateView = (TextView) rootView.findViewById(R.id.releaseDateView);
        averageScoreView = (TextView) rootView.findViewById(R.id.averageScoreRating);
        popularityView = (TextView) rootView.findViewById(R.id.popularityRating);
        imageView = (ImageView) rootView.findViewById(R.id.posterImageView);
        starView = (ImageView) rootView.findViewById(R.id.star_view);

        reviewsHeading = (TextView)rootView.findViewById(R.id.reviews_heading);
        reviewsHeading.setTypeface(null,Typeface.BOLD);
//        reviewsHeading.setPaintFlags(reviewsHeading.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        trailersHeading = (TextView)rootView.findViewById(R.id.trailers_heading);
        trailersHeading.setTypeface(null, Typeface.BOLD);
//        trailersHeading.setPaintFlags(trailersHeading.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);

        mReviewRecyclerView = (RecyclerView)rootView.findViewById(R.id.rv_reviews);
        mTrailerRecyclerView = (RecyclerView)rootView.findViewById(R.id.rv_trailers);

        return rootView;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER_ID, null, this);
        getLoaderManager().initLoader(REVIEW_LOADER_ID, null, this);
        getLoaderManager().initLoader(TRAILER_LOADER_ID, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        // Inflate the menu; this adds items to the action bar if it is present
//        inflater.inflate(R.menu.menu_detail, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set / change the share intent
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent
        if (!getLoaderManager().hasRunningLoaders()) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mShareMovieTitle + MOVIE_SHARE_HASHTAG);
        return shareIntent;
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
    public Loader<Cursor> onCreateLoader(int loaderID, Bundle args) {

        switch (loaderID) {
            case MOVIE_LOADER_ID: {
                Intent intent = getActivity().getIntent();
                Uri movieUri = intent.getData();
                mMovieID = Long.toString(ContentUris.parseId(movieUri));

                return new CursorLoader(
                        getActivity(),
                        intent.getData(),
                        MOVIE_COLUMNS,
                        null,
                        null,
                        null
                );
            }
            case REVIEW_LOADER_ID: {
                if(mMovieID != null) {
                    FetchReviewsTask reviewsTask = new FetchReviewsTask(getActivity());
                    reviewsTask.execute(mMovieID);

                    return new CursorLoader(
                            getActivity(),
                            MovieContract.ReviewEntry.CONTENT_URI,
                            null,
                            MovieContract.ReviewEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{mMovieID},
                            null
                    );
                } else {
                    return null;
                }
            }
            case TRAILER_LOADER_ID: {
                if(mMovieID != null) {
                    FetchTrailersTask trailersTask = new FetchTrailersTask(getActivity());
                    trailersTask.execute(mMovieID);

                    return new CursorLoader(
                            getActivity(),
                            MovieContract.TrailerEntry.CONTENT_URI,
                            null,
                            MovieContract.TrailerEntry.COLUMN_MOVIE_ID + " = ?",
                            new String[]{mMovieID},
                            null
                    );
                } else {
                    return null;
                }
            }
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> cursorLoader, final Cursor cursor) {

        int loaderID = cursorLoader.getId();

        switch (loaderID) {
            case MOVIE_LOADER_ID: {
                if (!cursor.moveToFirst()) {
                    break;
                }

                mShareMovieTitle = cursor.getString(COL_MOVIE_TITLE);
                titleView.setText(mShareMovieTitle);

                releaseDateView.setText("Release date: \n" + cursor.getString(COL_MOVIE_RELEASE_DATE));
                averageScoreView.setText("Vote average: \n" + String.format("%.2f", cursor.getDouble(COL_MOVIE_VOTE_AVERAGE)));
                popularityView.setText("Popularity \n" + cursor.getDouble(COL_MOVIE_POPULARITY));
                String overviewString = cursor.getString(COL_MOVIE_OVERVIEW);

                if(overviewString.length() > 5) {
                    TextView overView = (TextView) getActivity().findViewById(R.id.overView);
                    overView.setText(overviewString);
                    overviewHeading.setVisibility(View.VISIBLE);
                    overviewHeading.setTypeface(null, Typeface.BOLD);
//                    overviewHeading.setPaintFlags(overviewHeading.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
                    overView.setVisibility(View.VISIBLE);

                }
                //Check if it is a Valid URL - http://stackoverflow.com/questions/4905075/how-to-check-if-url-is-valid-in-android
                if (Patterns.WEB_URL.matcher(cursor.getString(COL_MOVIE_POSTER_PATH)).matches()) {
                    Picasso.with(getActivity()).load(cursor.getString(COL_MOVIE_POSTER_PATH)).into(imageView);
                }

                isFavourite = cursor.getInt(COL_MOVIE_FAVOURITE);
                updateStar(starView, isFavourite);

                starView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        isFavourite = isFavourite > 0 ? 0 : 1;
                        ContentValues updateValue = new ContentValues();
                        updateValue.put(MovieContract.MovieEntry.COLUMN_FAVOURITE, isFavourite);

                        getActivity().getContentResolver().update(
                                MovieContract.MovieEntry.CONTENT_URI,
                                updateValue,
                                MovieContract.MovieEntry._ID + " = ? ",
                                new String[]{Long.toString(cursor.getLong(COL_MOVIE_ID))}
                        );

                        updateStar(starView, isFavourite);
                    }
                });
            }
            break;
            case REVIEW_LOADER_ID: {
                if(cursor.getCount() <= 0) break;
                reviewsHeading.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                RecyclerView.ItemDecoration itemDecoration = new
                        DividerItemDecoration(getActivity(), layoutManager.getOrientation());
                mReviewAdapter = new ReviewAdapter(cursor); /** The cursor returns correctly **/
                mReviewRecyclerView.setLayoutManager(layoutManager);
                mReviewRecyclerView.addItemDecoration(itemDecoration);
                mReviewRecyclerView.setAdapter(mReviewAdapter);
                mReviewRecyclerView.setVisibility(View.VISIBLE);
            }
            break;
            case TRAILER_LOADER_ID: {
                if(cursor.getCount() <= 0) break;
                trailersHeading.setVisibility(View.VISIBLE);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                RecyclerView.ItemDecoration itemDecoration = new
                        DividerItemDecoration(getActivity(), layoutManager.getOrientation());
                mTrailerAdapter = new TrailerAdapter(cursor);
                mTrailerRecyclerView.setLayoutManager(layoutManager);
                mTrailerRecyclerView.addItemDecoration(itemDecoration);
                mTrailerRecyclerView.setAdapter(mTrailerAdapter);
                mTrailerRecyclerView.setVisibility(View.VISIBLE);
            }
            break;
        }

    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        int loaderID = loader.getId();
        switch (loaderID) {
            case MOVIE_LOADER_ID: {
                getLoaderManager().destroyLoader(MOVIE_LOADER_ID);
                break;
            }
            case REVIEW_LOADER_ID: {
                getLoaderManager().destroyLoader(REVIEW_LOADER_ID);
                break;
            }
        }
    }

    /*
     * Update with the latest image
     */
    private void updateStar(ImageView starView, int favourite) {
        if ( isFavourite > 0) {
            starView.setImageResource(R.drawable.star_favourite);
        } else {
            starView.setImageResource(R.drawable.star_not_favourrite);
        }
    }
}
