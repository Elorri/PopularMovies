package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.net.URL;

/**
 * {@link TrailersAdapter} exposes a list of trailer
 * from a {@link android.database.Cursor} to a {@link android.widget.ListView}.
 */
public class TrailersAdapter extends CursorAdapter {

    /**
     * Cache of the children views for a forecast list item.
     */
    public static class ViewHolder {
        public final ImageView trailerImgView;
        public final TextView trailerTitleView;

        public ViewHolder(View view) {
            trailerImgView = (ImageView) view.findViewById(R.id.trailer_img);
            trailerTitleView = (TextView) view.findViewById(R.id.trailer_title);
        }
    }


    public TrailersAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.trailer_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        Log.e("PopularMovies", Utility.constructYoutubeThumbnailTrailerURL("FRDdRto_3SA")
                .toString());

        ViewHolder viewHolder = (ViewHolder) view.getTag();
        URL thumbnailTrailerURL = Utility.constructYoutubeThumbnailTrailerURL(cursor.getString
                (DetailFragment.COL_KEY));
        Picasso.with(context).load(thumbnailTrailerURL.toString()).into(viewHolder.trailerImgView);
        viewHolder.trailerTitleView.setText(cursor.getString(DetailFragment.COL_NAME)+
                "("+cursor.getString(DetailFragment.COL_TYPE)+")");
    }
}
