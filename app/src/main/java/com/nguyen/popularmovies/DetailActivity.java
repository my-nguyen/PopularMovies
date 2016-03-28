package com.nguyen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;
import org.parceler.Parcels;

import java.util.List;

import cz.msebera.android.httpclient.Header;

/**
 * Created by My on 3/26/2016.
 */
public class DetailActivity extends AppCompatActivity {
   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.activity_detail);

      final Movie movie = (Movie)Parcels.unwrap(getIntent().getParcelableExtra("MOVIE_IN"));
      TextView title = (TextView)findViewById(R.id.title);
      title.setText(movie.originalTitle);
      ImageView poster = (ImageView)findViewById(R.id.poster);
      String imageUrl = "http://image.tmdb.org/t/p/w185/" + movie.posterPath;
      Picasso.with(this).load(imageUrl).into(poster);
      TextView year = (TextView)findViewById(R.id.year);
      String yearText = movie.releaseDate.split("-")[0];
      year.setText(yearText);
      TextView average = (TextView)findViewById(R.id.average);
      String averageText = movie.voteAverage + "/10";
      average.setText(averageText);
      TextView synopsis = (TextView)findViewById(R.id.synopsis);
      synopsis.setText(movie.overview);

      TMDBClient client = TMDBClient.getInstance();
      client.getTrailers(movie.id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            List<Trailer> trailers = Trailer.fromJSONArray(response);
            LinearLayout XMLLayout = (LinearLayout) findViewById(R.id.trailers);
            for (int i = 0; i < trailers.size(); i++) {
               // LinearLayout dynamicLayout = new LinearLayout(DetailActivity.this);
               // dynamicLayout.setOrientation(LinearLayout.HORIZONTAL);
               RelativeLayout dynamicLayout = new RelativeLayout(DetailActivity.this);
               RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
               dynamicLayout.setLayoutParams(layoutParams);

               ImageButton button = new ImageButton(DetailActivity.this);
               button.setImageResource(R.drawable.ic_trailer_play);
               button.setBackgroundColor(Color.TRANSPARENT);
               button.setId(1);
               RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                     ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               buttonParams.setMargins(20, 10, 20, 10);
               dynamicLayout.addView(button, buttonParams);

               TextView label = new TextView(DetailActivity.this);
               label.setText("Trailer " + (i + 1));
               label.setGravity(Gravity.CENTER);
               RelativeLayout.LayoutParams labelParams = new RelativeLayout.LayoutParams(
                     ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               labelParams.addRule(RelativeLayout.RIGHT_OF, button.getId());
               labelParams.addRule(RelativeLayout.ALIGN_TOP, button.getId());
               labelParams.addRule(RelativeLayout.ALIGN_BOTTOM, button.getId());
               labelParams.setMarginStart(30);
               dynamicLayout.addView(label, labelParams);

               XMLLayout.addView(dynamicLayout);
            }
         }
      });
      }

   public static Intent newIntent(Context context, Movie movie) {
      Intent intent = new Intent(context, DetailActivity.class);
      intent.putExtra("MOVIE_IN", Parcels.wrap(movie));
      return intent;
   }
}
