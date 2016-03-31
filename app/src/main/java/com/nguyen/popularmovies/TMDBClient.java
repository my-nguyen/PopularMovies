package com.nguyen.popularmovies;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * Created by My on 3/26/2016.
 */
public class TMDBClient {
   private static final String TMDB_BASE_URL = "http://api.themoviedb.org/3/movie";
   private static final String TMDB_API_KEY = "1fca74d1a066b2433a06dea9b96239fe";
   private static TMDBClient instance = null;

   // to defeat instantiation
   private TMDBClient() {
   }

   public static TMDBClient getInstance() {
      if (instance == null)
         instance = new TMDBClient();
      return instance;
   }

   public void getNowPlayingMovies(int page, AsyncHttpResponseHandler handler) {
      getMovies("now_playing", page, handler);
   }

   public void getPopularMovies(int page, AsyncHttpResponseHandler handler) {
      getMovies("popular", page, handler);
   }

   public void getTopRatedMovies(int page, AsyncHttpResponseHandler handler) {
      getMovies("top_rated", page, handler);
   }

   public void getUpcomingMovies(int page, AsyncHttpResponseHandler handler) {
      getMovies("upcoming", page, handler);
   }

   public void getTrailers(long movieId, AsyncHttpResponseHandler handler) {
      getMetadata("videos", movieId, handler);
   }

   public void getReviews(long movieId, AsyncHttpResponseHandler handler) {
      getMetadata("reviews", movieId, handler);
   }

   private void getMovies(String sortCriteria, int page, AsyncHttpResponseHandler handler) {
      AsyncHttpClient client = new AsyncHttpClient();
      String url = TMDB_BASE_URL + "/" + sortCriteria;
      RequestParams params = new RequestParams();
      params.put("api_key", TMDB_API_KEY);
      params.put("page", page);
      client.get(url, params, handler);
   }

   private void getMetadata(String metadata, long movieId, AsyncHttpResponseHandler handler) {
      AsyncHttpClient client = new AsyncHttpClient();
      String url = TMDB_BASE_URL + "/" + movieId + "/" + metadata;
      RequestParams params = new RequestParams();
      params.put("api_key", TMDB_API_KEY);
      client.get(url, params, handler);
   }
}
