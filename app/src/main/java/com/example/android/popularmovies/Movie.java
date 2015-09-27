package com.example.android.popularmovies;

import java.io.Serializable;

/**
 * Created by Elorri-user on 27/09/2015.
 */
public class Movie implements Serializable {
    String id;
    String posterName;
    String title;
    String overview;
    String vote_average;
    String release_date;

    public Movie(String id, String posterName){
        this.id=id;
        this.posterName = posterName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getVote_average() {
        return vote_average;
    }

    public void setVote_average(String vote_average) {
        this.vote_average = vote_average;
    }

    public String getRelease_date() {
        return release_date;
    }

    public void setRelease_date(String release_date) {
        this.release_date = release_date;
    }

    public String getId() {
        return id;
    }

    public String getPosterName() {
        return posterName;
    }
}

