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

    private static final Long MOVIE_ID=135399L;


    public void testUriMatcher() {
        UriMatcher testMatcher = MovieProvider.buildUriMatcher();
        assertEquals("Error:",testMatcher.match(MovieEntry.CONTENT_URI), MovieProvider.MOVIE);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMoviesSortByUri("popularity.desc")), MovieProvider.MOVIES_SORT_BY);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMoviesFavoriteUri()), MovieProvider.MOVIES_FAVORITE);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMovieDetailUri(MOVIE_ID)), MovieProvider.MOVIE_DETAIL);
        assertEquals("Error:",testMatcher.match(TrailerEntry.CONTENT_URI), MovieProvider.TRAILER);
        assertEquals("Error:",testMatcher.match(TrailerEntry.buildMovieTrailerUri(MOVIE_ID)), MovieProvider.TRAILERS_MOVIE);
        assertEquals("Error:",testMatcher.match(TrailerEntry.buildTrailerDetailUri("559198cac3a3685710000b58")), MovieProvider.TRAILER_DETAIL);
        assertEquals("Error:",testMatcher.match(ReviewEntry.CONTENT_URI), MovieProvider.REVIEW);
        assertEquals("Error:",testMatcher.match(ReviewEntry.buildMovieReviewUri(MOVIE_ID)), MovieProvider.REVIEWS_MOVIE);
        assertEquals("Error:",testMatcher.match(ReviewEntry.buildReviewDetailUri("75660928c3a3687ad7002db")), MovieProvider.REVIEW_DETAIL);
        assertEquals("Error:",testMatcher.match(MovieEntry.buildMovieTrailersReviewsUri(MOVIE_ID.toString())
        ), MovieProvider.TRAILERS_REVIEWS_MOVIE);
    }
}
