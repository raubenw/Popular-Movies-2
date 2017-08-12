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

public class ReviewAdapter extends RecyclerView.Adapter<ReviewAdapter.ReviewViewHolder> {

    private Cursor mReviewCursor;

    public ReviewAdapter(Cursor reviewCursor) {
        mReviewCursor = reviewCursor;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.list_item_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean attachImmediatelyToParent = false;

        View view = inflater.inflate(layoutIdForListItem, viewGroup, attachImmediatelyToParent);
        ReviewViewHolder reviewViewHolder = new ReviewViewHolder(view);

        return reviewViewHolder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        if(mReviewCursor.moveToPosition(position)) {
            String authorString = mReviewCursor.getString(mReviewCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_AUTHOR));
            String contentString = mReviewCursor.getString(mReviewCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_CONTENT));
            String reviewUrl = mReviewCursor.getString(mReviewCursor.getColumnIndex(MovieContract.ReviewEntry.COLUMN_URL));

            holder.bind(authorString, contentString, reviewUrl);
        }
    }

    @Override
    public int getItemCount() {
        if(mReviewCursor == null) {
            return 0;
        } else {
            return mReviewCursor.getCount();
        }
    }

    class ReviewViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvAuthor = null;
        private TextView tvContent = null;
        private Button btReadMore = null;
        private String mReviewUrl = null;

        public ReviewViewHolder(View itemView) {
            super(itemView);
            tvAuthor = (TextView)itemView.findViewById(R.id.tv_author);
            tvContent = (TextView)itemView.findViewById(R.id.tv_content);
            btReadMore = (Button)itemView.findViewById(R.id.bt_readmore);
            btReadMore.setOnClickListener(this);
        }

        void bind(String authorString, String contentString, String urlString) {

            tvAuthor.setText(authorString);
            tvContent.setText(contentString.substring(0, 50));
            mReviewUrl = urlString;
        }

        @Override
        public void onClick(View view) {
            Intent reviewIntent = new Intent(Intent.ACTION_VIEW);
            reviewIntent.setData(Uri.parse(mReviewUrl));
            view.getContext().startActivity(reviewIntent);
        }
    }
}
