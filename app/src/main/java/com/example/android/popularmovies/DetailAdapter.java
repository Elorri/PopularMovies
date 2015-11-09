package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.SwitchCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * {@link DetailAdapter} exposes a list of trailer
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class DetailAdapter extends CursorAdapter  implements CompoundButton.OnCheckedChangeListener{

    private static final int VIEW_TYPE_COUNT = 4;

    private static final int ITEM_DESC = 0;
    private static final int ITEM_LABEL = 1;
    private static final int ITEM_TRAILER = 2;
    private static final int ITEM_REVIEW = 3;

    private int TRAILER_POSITION=1;
    private int review_position;


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

        private TextView labelTextview;

        private ImageView trailerImgView;
        private TextView trailerTitleView;

        private TextView reviewAuthorTextView;
        private TextView reviewContentTextView;


        public ViewHolder(View view, int viewType) {
            switch (viewType) {
                case ITEM_DESC: {
                    titleTextView= (TextView) view.findViewById(R.id.title);
                    posterimageImageView= (ImageView) view.findViewById(R.id.posterImage);
                    plotsynopsisTextView= (TextView) view.findViewById(R.id.overview);
                    voteaverageTextView= (TextView) view.findViewById(R.id.voteAverage);
                    releasedateTextView= (TextView) view.findViewById(R.id.releaseYear);
                    durationTextView= (TextView) view.findViewById(R.id.duration);
                    favoriteSwitchCompat= (SwitchCompat) view.findViewById(R.id.favorite);
                    break;
                }
                case ITEM_LABEL: {
                    labelTextview = (TextView) view.findViewById(R.id.detail_label_textview);
                    break;
                }
                case ITEM_TRAILER: {
                    trailerImgView = (ImageView) view.findViewById(R.id.trailer_img);
                    trailerTitleView = (TextView) view.findViewById(R.id.trailer_title);
                    break;
                }
                case ITEM_REVIEW: {
                    reviewAuthorTextView = (TextView) view.findViewById(R.id.review_author);
                    reviewContentTextView = (TextView) view.findViewById(R.id.review_content);
                    break;
                }
            }
        }
    }


    public DetailAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
        mContext=context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        Log.e("PopularMovies", "newView " + this.getClass().getSimpleName());
        // Choose the layout type
        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        switch (viewType) {
            case ITEM_DESC: {
                layoutId = R.layout.detail_desc_item;
                break;
            }
            case ITEM_LABEL: {
                layoutId = R.layout.detail_label_item;
                break;
            }
            case ITEM_TRAILER: {
                layoutId = R.layout.detail_trailer_item;
                break;
            }
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
        Log.e("PopularMovies", "bindView " + this.getClass().getSimpleName());

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        int viewType = getItemViewType(cursor.getPosition());
        switch (viewType) {
            case ITEM_DESC: {
                Log.e("PopularMovies", "MOVIE_LOADER "+" "+this.getClass().getSimpleName());
                mId = cursor.getLong(DetailFragment.COL_ID);
                mTitleValue = cursor.getString(DetailFragment.COL_TITLE);
                mDurationValue = cursor.getInt(DetailFragment.COL_DURATION);
                mReleaseDateValue = cursor.getString(DetailFragment.COL_RELEASE_DATE).split("-")[0];//To extract the
                // year  from the date
                mPosterPathValue = cursor.getString(DetailFragment.COL_POSTER_PATH);
                mPlotSynopsisValue = cursor.getString(DetailFragment.COL_PLOT_SYNOPSIS);
                mRateValue = cursor.getDouble(DetailFragment.COL_RATE);
                mPopularityValue = cursor.getString(DetailFragment.COL_POPULARITY);
                mFavoriteValue = Utility.isFavorite(cursor.getInt(DetailFragment.COL_FAVORITE));


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
            case ITEM_LABEL: {
                if (cursor.getPosition() == TRAILER_POSITION){
                    viewHolder.labelTextview.setText(context.getString(R.string.detail_label_trailer));
                    review_position=TRAILER_POSITION+1;
                }
                viewHolder.labelTextview.setText(context.getString(R.string.detail_label_review));
                break;
            }
            case ITEM_TRAILER: {
                review_position=review_position++;
                URL thumbnailTrailerURL = Utility.constructYoutubeThumbnailTrailerURL(cursor.getString
                        (DetailFragment.COL_KEY));
                Picasso.with(context).load(thumbnailTrailerURL.toString()).into(viewHolder.trailerImgView);
                viewHolder.trailerTitleView.setText(cursor.getString(DetailFragment.COL_NAME));
                break;
            }
            case ITEM_REVIEW: {
                viewHolder.reviewAuthorTextView.setText(cursor.getString(DetailFragment.COL_AUTHOR));
                viewHolder.reviewContentTextView.setText(cursor.getString(DetailFragment.COL_CONTENT));
                break;
            }
        }
    }


    @Override
    public int getItemViewType(int position) {
        return (position == 0) ? ITEM_DESC : ITEM_LABEL;
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
