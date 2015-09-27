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
    String voteAverage;
    String releaseDate;
    String duration;

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

    public String getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(String voteAverage) {
        this.voteAverage = voteAverage;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getId() {
        return id;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getPosterName() {
        return posterName;
    }
}

