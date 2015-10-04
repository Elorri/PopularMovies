package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Elorri-user on 27/09/2015.
 */
public class Movie implements Parcelable {
    String id;
    String posterName;
    String title;
    String overview;
    String voteAverage;
    String releaseDate;
    String duration;


    public Movie(String id, String title, String posterName, String releaseDate, String duration, String voteAverage, String overview) {
        this.id = id;
        this.title = title;
        this.posterName = posterName;
        this.releaseDate = releaseDate;
        this.duration = duration;
        this.voteAverage = voteAverage;
        this.overview = overview;
    }


    public String getTitle() {
        return title;
    }


    public String getOverview() {
        return overview;
    }


    public String getVoteAverage() {
        return voteAverage;
    }


    public String getReleaseDate() {
        return releaseDate;
    }


    public String getId() {
        return id;
    }

    public String getDuration() {
        return duration;
    }


    public String getPosterName() {
        return posterName;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.posterName);
        dest.writeString(this.title);
        dest.writeString(this.overview);
        dest.writeString(this.voteAverage);
        dest.writeString(this.releaseDate);
        dest.writeString(this.duration);
    }

    private Movie(Parcel in) {
        this.id = in.readString();
        this.posterName = in.readString();
        this.title = in.readString();
        this.overview = in.readString();
        this.voteAverage = in.readString();
        this.releaseDate = in.readString();
        this.duration = in.readString();
    }

    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        public Movie createFromParcel(Parcel source) {
            return new Movie(source);
        }

        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}

