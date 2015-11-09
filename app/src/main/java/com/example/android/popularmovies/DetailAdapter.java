package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieProvider;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * {@link DetailAdapter} exposes a list of trailer
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class DetailAdapter extends CursorAdapter implements CompoundButton.OnCheckedChangeListener {


    private static final int ITEM_DESC = 0;
    private static final int ITEM_TRAILER_LABEL = 1;
    private static final int ITEM_TRAILER = 2;
    private static final int ITEM_REVIEW_LABEL = 3;
    private static final int ITEM_REVIEW = 4;

    private int next_item_type;


    private long mId;
    private String mTitleValue;
    private int mDurationValue;
    private String mReleaseDateValue;
    private String mPosterPathValue;
    private String mPlotSynopsisValue;
    private Double mRateValue;
    private String mPopularityValue;
    private boolean mFavoriteValue;
    private Context mContext;
    private int mDescItemCount;
    private int mTrailerItemCount;
    private int mReviewItemCount;

    private int VIEW_TYPE_COUNT = MovieProvider.CURSOR_TYPE_COUNT + 2; //plus 2 labels


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        private TextView titleTextView;
        private ImageView posterimageImageView;
        private TextView plotsynopsisTextView;
        private TextView voteaverageTextView;
        private TextView releasedateTextView;
        private TextView durationTextView;
        private SwitchCompat favoriteSwitchCompat;

        private ImageView trailerImgView;
        private TextView trailerTitleView;

        private TextView reviewAuthorTextView;
        private TextView reviewContentTextView;


        public ViewHolder(View view, int viewType) {
            titleTextView = (TextView) view.findViewById(R.id.title);
            posterimageImageView = (ImageView) view.findViewById(R.id.posterImage);
            plotsynopsisTextView = (TextView) view.findViewById(R.id.overview);
            voteaverageTextView = (TextView) view.findViewById(R.id.voteAverage);
            releasedateTextView = (TextView) view.findViewById(R.id.releaseYear);
            durationTextView = (TextView) view.findViewById(R.id.duration);
            favoriteSwitchCompat = (SwitchCompat) view.findViewById(R.id.favorite);

            trailerImgView = (ImageView) view.findViewById(R.id.trailer_img);
            trailerTitleView = (TextView) view.findViewById(R.id.trailer_title);

            reviewAuthorTextView = (TextView) view.findViewById(R.id.review_author);
            reviewContentTextView = (TextView) view.findViewById(R.id.review_content);

        }
    }


    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext = context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case ITEM_DESC: {
                layoutId = R.layout.detail_desc_item;
                break;
            }
            case ITEM_TRAILER_LABEL:
                layoutId = R.layout.detail_label_trailer_item;
                break;
            case ITEM_TRAILER: {
                layoutId = R.layout.detail_trailer_item;
                break;
            }
            case ITEM_REVIEW_LABEL:
                layoutId = R.layout.detail_label_review_item;
                break;
            case ITEM_REVIEW: {
                layoutId = R.layout.detail_review_item;
                break;
            }
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, viewType);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case ITEM_DESC: {
                mId = cursor.getLong(MovieProvider.COL_ID);
                mTitleValue = cursor.getString(MovieProvider.COL_TITLE);
                mDurationValue = cursor.getInt(MovieProvider.COL_DURATION);
                mReleaseDateValue = cursor.getString(MovieProvider.COL_RELEASE_DATE).split("-")[0];//To extract the
                // year  from the date
                mPosterPathValue = cursor.getString(MovieProvider.COL_POSTER_PATH);
                mPlotSynopsisValue = cursor.getString(MovieProvider.COL_PLOT_SYNOPSIS);
                mRateValue = cursor.getDouble(MovieProvider.COL_RATE);
                mPopularityValue = cursor.getString(MovieProvider.COL_POPULARITY);
                mFavoriteValue = Utility.isFavorite(cursor.getInt(MovieProvider.COL_FAVORITE));

                viewHolder.titleTextView.setText(mTitleValue);
                viewHolder.durationTextView.setText(mDurationValue + " " + context.getString(R
                        .string.min));
                viewHolder.releasedateTextView.setText(mReleaseDateValue);
                Picasso.with(context).load(TmdbAccess.constructPosterImageURL(mPosterPathValue)
                        .toString()).into(viewHolder.posterimageImageView);
                viewHolder.plotsynopsisTextView.setText(mPlotSynopsisValue);
                viewHolder.voteaverageTextView.setText(mRateValue + context.getString(R.string
                        .rateMax));
                viewHolder.favoriteSwitchCompat.setChecked(mFavoriteValue);
                viewHolder.favoriteSwitchCompat.setOnCheckedChangeListener(this);
                break;
            }

            case ITEM_TRAILER_LABEL:
            case ITEM_TRAILER: {
                URL thumbnailTrailerURL = Utility.constructYoutubeThumbnailTrailerURL(cursor.getString
                        (MovieProvider.COL_KEY));
                Picasso.with(context).load(thumbnailTrailerURL.toString()).into(viewHolder.trailerImgView);
                viewHolder.trailerTitleView.setText(cursor.getString(MovieProvider.COL_NAME));
                break;
            }
            case ITEM_REVIEW_LABEL:
            case ITEM_REVIEW: {
                viewHolder.reviewAuthorTextView.setText(cursor.getString(MovieProvider.COL_AUTHOR));
                viewHolder.reviewContentTextView.setText(cursor.getString(MovieProvider.COL_CONTENT));
                break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        mDescItemCount = MovieProvider.mCursorsCount[0];
        mTrailerItemCount = MovieProvider.mCursorsCount[1];
        mReviewItemCount = MovieProvider.mCursorsCount[2];

        if (position < mDescItemCount)
            return ITEM_DESC;
        else if (position == mDescItemCount)
            return ITEM_TRAILER_LABEL;
        else if (position > mDescItemCount && position < (mDescItemCount + mTrailerItemCount))
            return ITEM_TRAILER;
        else if (position == (mDescItemCount + mTrailerItemCount))
            return ITEM_REVIEW_LABEL;
        else if ((position > (mDescItemCount + mTrailerItemCount)) && (position <
                (mDescItemCount + mTrailerItemCount + mReviewItemCount)))
            return ITEM_REVIEW;
        else return -1;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        ContentValues movieValues = new ContentValues();
        movieValues.put(MovieContract.MovieEntry._ID, mId);
        movieValues.put(MovieContract.MovieEntry.COLUMN_TITLE, mTitleValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_DURATION, mDurationValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RELEASE_DATE, mReleaseDateValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, mPosterPathValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_PLOT_SYNOPSIS, mPlotSynopsisValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, mRateValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_POPULARITY, mPopularityValue);
        movieValues.put(MovieContract.MovieEntry.COLUMN_FAVORITE, Utility.getDbFavoriteValue(isChecked));

        mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                movieValues, MovieContract.MovieEntry
                        ._ID + "=?", new String[]{Long.toString(mId)});
    }
}
