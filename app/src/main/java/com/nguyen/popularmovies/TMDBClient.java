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

   public void getPopularMovies(AsyncHttpResponseHandler handler) {
      getMovies("popular", handler);
   }

   public void getTopRatedMovies(AsyncHttpResponseHandler handler) {
      getMovies("top_rated", handler);
   }

   private void getMovies(String sortCriteria, AsyncHttpResponseHandler handler) {
      AsyncHttpClient client = new AsyncHttpClient();
      String url = TMDB_BASE_URL + "/" + sortCriteria;
      RequestParams params = new RequestParams();
      params.put("api_key", TMDB_API_KEY);
      client.get(url, params, handler);
   }
}
