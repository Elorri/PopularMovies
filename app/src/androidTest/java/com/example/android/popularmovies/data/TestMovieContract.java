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
    private static final String TRAILER_ID = "559198cac3a3685710000b58";
    private static final String REVIEW_ID = "75660928c3a3687ad7002db";

    public void testBuildMovieDetailUri() {
        Uri movieDetailUri = MovieEntry.buildMovieDetailUri(MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieDetailUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",  MOVIE_ID, Long.valueOf(movieDetailUri.getLastPathSegment()));
        assertEquals("Error: movieDetailUri doesn't match our expected result", movieDetailUri.toString(),"content://com.example.android.popularmovies/movie/135399");
    }


    public void testBuildSortByUri() {
        Uri movieSortByUri = MovieEntry.buildMoviesSortByUri(SORT_BY_VALUE);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieSortByUri);
        assertEquals("Error: query parameter not properly appended to the end of the Uri",  "popularity.desc", movieSortByUri.getLastPathSegment());
        assertEquals("Error: movieSortByUri doesn't match our expected result", movieSortByUri.toString(),"content://com.example.android.popularmovies/movie/popularity.desc");
    }

    public void testBuildFavoriteUri() {
        Uri movieFavoriteUri = MovieEntry.buildMoviesFavoriteUri();
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieFavoriteUri);
        assertEquals("Error: query parameter not properly appended to the end of the Uri",  "popularity.desc", movieFavoriteUri.getLastPathSegment());
        assertEquals("Error: movieFavoriteUri"+movieFavoriteUri.toString()+" doesn't match our expected result", movieFavoriteUri.toString(),"content://com.example.android.popularmovies/movie/favorite/popularity.desc");
    }

    public void testBuildTrailerDetailUri() {
        Uri trailerDetailUri = TrailerEntry.buildTrailerDetailUri(TRAILER_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailerDetailUri in MovieContract.", trailerDetailUri);
        assertEquals("Error: trailer not properly appended to the end of the Uri",  TRAILER_ID, trailerDetailUri.getLastPathSegment());
        assertEquals("Error: trailerDetailUri doesn't match our expected result", trailerDetailUri.toString(),"content://com.example.android.popularmovies/trailer/559198cac3a3685710000b58");
    }

    public void testBuildMovieTrailerUri() {
        Uri movieTrailerUri = TrailerEntry.buildMovieTrailerUri(MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieTrailerUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",  MOVIE_ID, Long.valueOf(movieTrailerUri.getLastPathSegment()));
        assertEquals("Error: movieTrailerUri"+movieTrailerUri.toString()+" doesn't match our expected result", movieTrailerUri.toString(),"content://com.example.android.popularmovies/trailer/135399");
    }

    public void testBuildReviewDetailUri() {
        Uri reviewDetailUri = ReviewEntry.buildReviewDetailUri(REVIEW_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildTrailerDetailUri in MovieContract.", reviewDetailUri);
        assertEquals("Error: review not properly appended to the end of the Uri",  REVIEW_ID, reviewDetailUri.getLastPathSegment());
        assertEquals("Error: reviewDetailUri doesn't match our expected result", reviewDetailUri.toString(),"content://com.example.android.popularmovies/review/75660928c3a3687ad7002db");
    }

    public void testBuildMovieReviewUri() {
        Uri movieReviewUri = ReviewEntry.buildMovieReviewUri(MOVIE_ID);
        assertNotNull("Error: Null Uri returned.  You must fill-in buildMovieDetailUri in MovieContract.", movieReviewUri);
        assertEquals("Error: movie not properly appended to the end of the Uri",  MOVIE_ID, Long.valueOf(movieReviewUri.getLastPathSegment()));
        assertEquals("Error: movieReviewUri"+movieReviewUri.toString()+" doesn't match our expected result", movieReviewUri.toString(),"content://com.example.android.popularmovies/review/135399");
    }


    public void testGetSortOrderFromMovieSortByUri(){
        String sort_by = MovieEntry.getSortOrderFromMovieSortByUri(MovieEntry.buildMoviesSortByUri(SORT_BY_VALUE));
        assertEquals("popularity",sort_by);
    }

    public void testGetMovieIdFromMovieDetailUri(){
        String movie_id = MovieEntry.getMovieIdFromMovieDetailUri(MovieEntry.buildMovieDetailUri(MOVIE_ID));
        assertEquals(MOVIE_ID.toString(),movie_id);
    }

    public void testGetMovieIdFromMovieTrailerUri(){
        String movie_id = TrailerEntry.getMovieIdFromMovieTrailerUri(TrailerEntry.buildMovieTrailerUri(MOVIE_ID));
        assertEquals(MOVIE_ID.toString(),movie_id);
    }

    public void testGetMovieIdFromMovieReviewUri(){
        String movie_id = ReviewEntry.getMovieIdFromMovieReviewUri(ReviewEntry.buildMovieReviewUri(MOVIE_ID));
        assertEquals(MOVIE_ID.toString(),movie_id);
    }

}
