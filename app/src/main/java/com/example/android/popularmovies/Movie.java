package com.example.android.popularmovies;

/**
 * Created by Elorri-user on 27/09/2015.
 */
public class Movie {
    String id;
    String posterPath;
    String title;
    String overview;
    String vote_average;
    String release_date;

    public Movie(String id, String posterPath){
        this.id=id;
        this.posterPath=posterPath;
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
}

