package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieProvider;
import com.example.android.popularmovies.sync.TmdbSync;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * {@link DetailAdapter} exposes a list of trailer
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class DetailAdapter extends CursorAdapter implements CompoundButton
        .OnCheckedChangeListener, MovieProvider.Callback {

    private static final String LOG_TAG = DetailAdapter.class.getSimpleName();
    private static DetailAdapter instance = null;

    private static final int ITEM_DESC = 0;
    private static final int ITEM_TRAILER_LABEL = 1;
    private static final int ITEM_TRAILER = 2;
    private static final int ITEM_REVIEW_LABEL = 3;
    private static final int ITEM_REVIEW = 4;

    private final DetailFragment mDetailFragment;

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

    @Override
    public void onDetailCursorMerged(int[] mCursorsCount) {
        mDescItemCount = mCursorsCount[0];
        mTrailerItemCount = mCursorsCount[1];
        mReviewItemCount = mCursorsCount[2];
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : DetailAdapter.mDescItemCount :  change state");
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : DetailAdapter.mTrailerItemCount :  change state");
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : DetailAdapter.mReviewItemCount :  change state");
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : DetailAdapter.mDescItemCount  mTrailerItemCount mReviewItemCount :  change state " +
                "{" + mDescItemCount + "," + mTrailerItemCount + "," + mReviewItemCount + "}");
    }

    public static DetailAdapter getInstance(Context context, Cursor c, int flags, DetailFragment
            detailFragment) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : DetailAdapter :  object created");
        if (instance == null)
            instance = new DetailAdapter(context, c, flags, detailFragment);
        return instance;
    }


    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {


        private final int viewType;
        //set by the constructor
        private TextView titleTextView;
        private ImageView posterimageImageView;
        private TextView plotsynopsisTextView;
        private TextView voteaverageTextView;
        private TextView releasedateTextView;
        private TextView durationTextView;
        private SwitchCompat favoriteView;

        private LinearLayout trailerItemView;
        private ImageView trailerImgView;
        private TextView trailerTitleView;

        private TextView reviewAuthorTextView;
        private TextView reviewContentTextView;

        //set in the bindview method
        public Uri youtubeVideoURI;


        public ViewHolder(View view, int viewType) {
            switch (viewType) {
                case ITEM_DESC:
                    Log.d("PopularMovies", "ITEM_DESC");
                    Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                            .thread() + " : " +
                            "titleTextView  " +
                            "posterimageImageView  " +
                            "plotsynopsisTextView " +
                            "voteaverageTextView " +
                            "releasedateTextView " +
                            "durationTextView " +
                            "favoriteView " +
                            ": object created");
                    titleTextView = (TextView) view.findViewById(R.id.title);
                    posterimageImageView = (ImageView) view.findViewById(R.id.posterImage);
                    plotsynopsisTextView = (TextView) view.findViewById(R.id.overview);
                    voteaverageTextView = (TextView) view.findViewById(R.id.voteAverage);
                    releasedateTextView = (TextView) view.findViewById(R.id.releaseYear);
                    durationTextView = (TextView) view.findViewById(R.id.duration);
                    favoriteView = (SwitchCompat) view.findViewById(R.id.favorite);
                    break;
                case ITEM_TRAILER_LABEL:
                    Log.d("PopularMovies", "ITEM_TRAILER_LABEL");
                    Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                            .thread() + " : DetailAdapter " +
                            "trailerItemView  " +
                            "trailerImgView  " +
                            "trailerTitleView " +
                            ": object created");
                    trailerItemView = (LinearLayout) view.findViewById(R.id.trailer_item);
                    trailerImgView = (ImageView) view.findViewById(R.id.trailer_img);
                    trailerTitleView = (TextView) view.findViewById(R.id.trailer_title);
                    break;
                case ITEM_TRAILER:
                    Log.d("PopularMovies", "ITEM_TRAILER");
                    Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                            .thread() + " : DetailAdapter " +
                            "trailerItemView  " +
                            "trailerImgView  " +
                            "trailerTitleView " +
                            ": object created");
                    trailerItemView = (LinearLayout) view.findViewById(R.id.trailer_item);
                    trailerImgView = (ImageView) view.findViewById(R.id.trailer_img);
                    trailerTitleView = (TextView) view.findViewById(R.id.trailer_title);
                    break;
                case ITEM_REVIEW_LABEL:
                    Log.d("PopularMovies", "ITEM_REVIEW_LABEL");
                    Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                            .thread() + " : DetailAdapter " +
                            "reviewAuthorTextView  " +
                            "reviewContentTextView  " +
                            ": object created");
                    reviewAuthorTextView = (TextView) view.findViewById(R.id.review_author);
                    reviewContentTextView = (TextView) view.findViewById(R.id.review_content);
                    break;
                case ITEM_REVIEW:
                    Log.d("PopularMovies", "ITEM_REVIEW");
                    Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                            .thread() + " : DetailAdapter " +
                            "reviewAuthorTextView  " +
                            "reviewContentTextView  " +
                            ": object created");
                    reviewAuthorTextView = (TextView) view.findViewById(R.id.review_author);
                    reviewContentTextView = (TextView) view.findViewById(R.id.review_content);
                    break;
            }
            this.viewType = viewType;

        }
    }


    public DetailAdapter(Context context, Cursor c, int flags, DetailFragment detailFragment) {
        super(context, c, flags);
        mContext = context;
        this.mDetailFragment = detailFragment;
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                .thread() + " : DetailAdapter " +
                "mContext  " +
                "mDetailFragment  " +
                ": change state");
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("PopularMovies", "newView");
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case ITEM_DESC:
                Log.d("PopularMovies", "ITEM_DESC");
                layoutId = R.layout.detail_desc_item;
                break;
            case ITEM_TRAILER_LABEL:
                Log.d("PopularMovies", "ITEM_TRAILER_LABEL");
                layoutId = R.layout.detail_label_trailer_item;
                break;
            case ITEM_TRAILER:
                Log.d("PopularMovies", "ITEM_TRAILER");
                layoutId = R.layout.detail_trailer_item;
                break;
            case ITEM_REVIEW_LABEL:
                Log.d("PopularMovies", "ITEM_REVIEW_LABEL");
                layoutId = R.layout.detail_label_review_item;
                break;
            case ITEM_REVIEW:
                Log.d("PopularMovies", "ITEM_REVIEW");
                layoutId = R.layout.detail_review_item;
                break;
        }
        View view = LayoutInflater.from(context).inflate(layoutId, parent, false);
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : Cursor : " + cursor.getCount());
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : DetailAdapter item View :  object created");
        ViewHolder viewHolder = new ViewHolder(view, viewType);
        view.setTag(viewHolder);
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : DetailAdapter item View : change state");
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility.thread() + " : " +
                " : Cursor : Position : ItemType" + cursor.getCount() + " : " + cursor.getPosition() +
                " : " + viewType);
        switch (viewType) {
            case ITEM_DESC:
                Log.d("PopularMovies", "ITEM_DESC");
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
                Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                        .thread() + " : DetailAdapter " +
                        "mId  " +
                        "mTitleValue  " +
                        "mDurationValue " +
                        "mReleaseDateValue " +
                        "mPosterPathValue " +
                        "mPlotSynopsisValue " +
                        "mRateValue " +
                        "mPopularityValue " +
                        "mFavoriteValue " +
                        ": change state");


                viewHolder.titleTextView.setText(mTitleValue);
                viewHolder.durationTextView.setText(String.format(context
                        .getString(R.string.duration), String.valueOf(mDurationValue)));
                viewHolder.releasedateTextView.setText(mReleaseDateValue);
                Picasso.with(context).load(TmdbSync.buildPosterImageURL(mPosterPathValue)
                        .toString()).into(viewHolder.posterimageImageView);
                viewHolder.plotsynopsisTextView.setText(mPlotSynopsisValue);
                viewHolder.voteaverageTextView.setText(String.format(context
                        .getString(R.string.rateMax), String.valueOf(mRateValue)));
                viewHolder.favoriteView.setChecked(mFavoriteValue);
                Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                        .thread() + " : ViewHolder " +
                        "titleTextView  " +
                        "durationTextView  " +
                        "releasedateTextView " +
                        "posterimageImageView " +
                        "plotsynopsisTextView " +
                        "voteaverageTextView " +
                        "favoriteView " +
                        ": change state");

                viewHolder.favoriteView.setOnCheckedChangeListener(this);
                break;
            case ITEM_TRAILER_LABEL:
                Log.d("PopularMovies", "ITEM_TRAILER_LABEL");
                viewHolder.youtubeVideoURI = Utility.buildYoutubeVideoURI(cursor.getString
                        (MovieProvider.COL_KEY));
                Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                        .thread() + " : ViewHolder youtubeVideoURI : change state");
                mDetailFragment.onFirstTrailerUriKnown(viewHolder.youtubeVideoURI);
                viewHolder.trailerItemView.setTag(viewHolder);
                setItemTrailerView(viewHolder, cursor, context);
                break;
            case ITEM_TRAILER:
                Log.d("PopularMovies", "ITEM_TRAILER");
                viewHolder.youtubeVideoURI = Utility.buildYoutubeVideoURI(cursor.getString
                        (MovieProvider.COL_KEY));
                Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                        .thread() + " : ViewHolder youtubeVideoURI : change state");
                setItemTrailerView(viewHolder, cursor, context);
                break;
            case ITEM_REVIEW_LABEL:
                Log.d("PopularMovies", "ITEM_REVIEW_LABEL");
                setItemReview(viewHolder, cursor);
                break;
            case ITEM_REVIEW:
                Log.d("PopularMovies", "ITEM_REVIEW");
                setItemReview(viewHolder, cursor);
                break;
        }
    }


    private void setItemTrailerView(ViewHolder viewHolder, Cursor cursor, final Context context) {

        URL thumbnailTrailerURL = Utility.buildYoutubeThumbnailTrailerURL(cursor.getString
                (MovieProvider.COL_KEY));
        Picasso.with(context).load(thumbnailTrailerURL.toString()).into(viewHolder.trailerImgView);
        viewHolder.trailerTitleView.setText(cursor.getString(MovieProvider.COL_NAME));
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                .thread() + " : ViewHolder trailerImgView trailerTitleView : change state");
        viewHolder.trailerItemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openYoutube(v, context);
            }
        });
    }

    private void openYoutube(View v, Context context) {
        ViewHolder viewHolder = (ViewHolder) v.getTag();
        Intent intent = new Intent(Intent.ACTION_VIEW).setData(viewHolder.youtubeVideoURI);
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : Intent youtube :  object created");
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        } else {
            Log.d(LOG_TAG, "Couldn't call " + viewHolder.youtubeVideoURI.toString() + ", no receiving apps installed!");
        }
    }

    private void setItemReview(ViewHolder viewHolder, Cursor cursor) {
        viewHolder.reviewAuthorTextView.setText(cursor.getString(MovieProvider.COL_AUTHOR));
        viewHolder.reviewContentTextView.setText(cursor.getString(MovieProvider.COL_CONTENT));
        Log.e("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                .thread() + " : ViewHolder reviewAuthorTextView reviewContentTextView : change state");
    }


    @Override
    public int getItemViewType(int position) {
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + ": " + Utility
                .thread() + " : position " + position);
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
        movieValues.put(MovieEntry._ID, mId);
        movieValues.put(MovieEntry.COLUMN_TITLE, mTitleValue);
        movieValues.put(MovieEntry.COLUMN_DURATION, mDurationValue);
        movieValues.put(MovieEntry.COLUMN_RELEASE_DATE, mReleaseDateValue);
        movieValues.put(MovieEntry.COLUMN_POSTER_PATH, mPosterPathValue);
        movieValues.put(MovieEntry.COLUMN_PLOT_SYNOPSIS, mPlotSynopsisValue);
        movieValues.put(MovieEntry.COLUMN_RATE, mRateValue);
        movieValues.put(MovieEntry.COLUMN_POPULARITY, mPopularityValue);
        movieValues.put(MovieEntry.COLUMN_FAVORITE, Utility.getDbFavoriteValue(isChecked));
        Log.d("Lifecycle", Thread.currentThread().getStackTrace()[2] + " : " + Utility
                .thread() + " : ContentValues :  object created");

        mContext.getContentResolver().update(MovieContract.MovieEntry.CONTENT_URI,
                movieValues, MovieContract.MovieEntry
                        ._ID + "=?", new String[]{Long.toString(mId)});


    }
}
