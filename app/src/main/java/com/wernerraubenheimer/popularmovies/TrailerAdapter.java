package com.wernerraubenheimer.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.wernerraubenheimer.popularmovies.data.MovieContract;

/**
 * Created by wernerr on 8/9/2017.
 */

public class TrailerAdapter extends RecyclerView.Adapter<TrailerAdapter.TrailerViewHolder> {

    private Cursor mTrailerCursor;

    public TrailerAdapter(Cursor trailerCursor) {
        mTrailerCursor = trailerCursor;
    }

    @Override
    public TrailerViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediatelyToParent = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediatelyToParent);
        TrailerViewHolder trailerViewHolder = new TrailerViewHolder(view);

        return trailerViewHolder;
    }

    @Override
    public void onBindViewHolder(TrailerViewHolder holder, int position) {
        if(mTrailerCursor.moveToPosition(position)) {
            String nameString = mTrailerCursor.getString(mTrailerCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_NAME));
            String typeString = mTrailerCursor.getString(mTrailerCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_TYPE));
            String trailerUrl = mTrailerCursor.getString(mTrailerCursor.getColumnIndex(MovieContract.TrailerEntry.COLUMN_URL));

            holder.bind(nameString, typeString, trailerUrl);
        }
    }

    @Override
    public int getItemCount() {
        if(mTrailerCursor == null) {
            return 0;
        } else {
            return mTrailerCursor.getCount();
        }
    }

    class TrailerViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvName = null;
        private TextView tvType = null;
        private Button btWatchVideo = null;
        private String mTrailerUrl = null;

        public TrailerViewHolder(View itemView) {
            super(itemView);
            tvName = (TextView)itemView.findViewById(R.id.tv_name);
            tvType = (TextView)itemView.findViewById(R.id.tv_type);
            btWatchVideo = (Button)itemView.findViewById(R.id.bt_watch_video);
            btWatchVideo.setOnClickListener(this);
        }

        void bind(String nameString, String typeString, String urlString) {

            tvName.setText(nameString);
            tvType.setText(typeString);
            mTrailerUrl = urlString;
        }

        @Override
        public void onClick(View view) {
            Intent trailerIntent = new Intent(Intent.ACTION_VIEW);
            trailerIntent.setData(Uri.parse(mTrailerUrl));
            view.getContext().startActivity(trailerIntent);
        }
    }
}
