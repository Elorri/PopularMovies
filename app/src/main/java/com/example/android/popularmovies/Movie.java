package com.example.android.popularmovies;

import java.io.Serializable;

/**
 * Created by Elorri-user on 27/09/2015.
 */
public class Movie{
    String id;
    String posterName;
    String title;
    String overview;
    String voteAverage;
    String releaseDate;
    String duration;

    public Movie(String id, String title, String posterName) {
        this.id = id;
        this.title = title;
        this.posterName = posterName;
    }

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
}

