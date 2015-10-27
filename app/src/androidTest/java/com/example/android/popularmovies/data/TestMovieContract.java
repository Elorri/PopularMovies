package com.example.android.popularmovies.data;

import android.net.Uri;
import android.test.AndroidTestCase;

import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;

/**
 * Created by Elorri on 26/10/2015.
 */
public class TestMovieContract extends AndroidTestCase {

    private static final Long MOVIE_ID = 135399l;
    public static final String SORT_BY_VALUE="popularity.desc";

    public void testBuildMovieDetailUri() {
        Uri movieDetailUri = MovieEntry.buildMovieDetailUri(MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieDetailUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",  MOVIE_ID, Long.valueOf(movieDetailUri.getLastPathSegment()));
        assertEquals("Error: movieDetailUri doesn't match our expected result", movieDetailUri.toString(),"content://com.example.android.popularmovies/movie/135399");
    }


    public void testBuildSortByUri() {
        Uri movieSortByUri = MovieEntry.buildMovieSortByUri(SORT_BY_VALUE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieSortByUri);
        assertEquals("Error: query parameter not properly appended to the end of the Uri",  "popularity.desc", movieSortByUri.getLastPathSegment());
        assertEquals("Error: movieSortByUri doesn't match our expected result", movieSortByUri.toString(),"content://com.example.android.popularmovies/movie/popularity.desc");
    }

    public void testBuildFavoriteUri() {
        Uri movieFavoriteUri = MovieEntry.buildMovieFavoriteUri();
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieFavoriteUri);
        assertEquals("Error: query parameter not properly appended to the end of the Uri",  "popularity.desc", movieFavoriteUri.getLastPathSegment());
        assertEquals("Error: movieFavoriteUri"+movieFavoriteUri.toString()+" doesn't match our expected result", movieFavoriteUri.toString(),"content://com.example.android.popularmovies/movie/favorite/popularity.desc");
    }

    public void testBuildMovieTrailerUri() {
        Uri movieTrailerUri = TrailerEntry.buildMovieTrailerUri(MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieTrailerUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",  MOVIE_ID, Long.valueOf(movieTrailerUri.getLastPathSegment()));
        assertEquals("Error: movieTrailerUri"+movieTrailerUri.toString()+" doesn't match our expected result", movieTrailerUri.toString(),"content://com.example.android.popularmovies/trailer/135399");
    }

    public void testBuildMovieReviewUri() {
        Uri movieReviewUri = ReviewEntry.buildMovieReviewUri(MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieReviewUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",  MOVIE_ID, Long.valueOf(movieReviewUri.getLastPathSegment()));
        assertEquals("Error: movieReviewUri"+movieReviewUri.toString()+" doesn't match our expected result", movieReviewUri.toString(),"content://com.example.android.popularmovies/review/135399");
    }

}
