package com.example.android.popularmovies.data;

import android.content.UriMatcher;
import android.test.AndroidTestCase;


import com.example.android.popularmovies.data.MovieContract.MovieEntry;
import com.example.android.popularmovies.data.MovieContract.ReviewEntry;
import com.example.android.popularmovies.data.MovieContract.TrailerEntry;

/**
 * Created by Elorri on 27/10/2015.
 */
public class TestUriMatcher extends AndroidTestCase {

    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        assertEquals("Error:",testMatcher.match(MovieEntry.CONTENT_URI), MovieProvider.MOVIE);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMovieSortByUri("popularity.desc")), MovieProvider.MOVIES_SORT_BY);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMovieFavoriteUri()), MovieProvider.MOVIES_FAVORITE);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMovieDetailUri(135399l)), MovieProvider.MOVIE_DETAIL);
        assertEquals("Error:",testMatcher.match(TrailerEntry.CONTENT_URI), MovieProvider.TRAILER);
        assertEquals("Error:",testMatcher.match(TrailerEntry.buildMovieTrailerUri(135399l)), MovieProvider.TRAILERS_MOVIE);
        assertEquals("Error:",testMatcher.match(ReviewEntry.CONTENT_URI), MovieProvider.REVIEW);
        assertEquals("Error:",testMatcher.match(ReviewEntry.buildMovieReviewUri(135399l)), MovieProvider.REVIEWS_MOVIE);
    }
}
