package com.nguyen.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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
   Movie dbMovie = null;

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
      final ImageButton favorite = (ImageButton)findViewById(R.id.favorite);
      // favorite.setBackgroundDrawable(getResources().getDrawable(R.drawable.heart));
      final ColorFilter gray = favorite.getColorFilter();
      dbMovie = Movie.query(movie.id);
      if (dbMovie != null)
         favorite.setColorFilter(Color.RED);
      favorite.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
            if (dbMovie == null) {
               favorite.setColorFilter(Color.RED);
               movie.save();
               dbMovie = movie;
               Log.d("NGUYEN", "saved movie: " + movie);
            } else {
               favorite.setColorFilter(gray);
               dbMovie.delete();
               dbMovie = null;
               Log.d("NGUYEN", "deleted movie");
            }
         }
      });
      average.setText(averageText);
      TextView synopsis = (TextView)findViewById(R.id.synopsis);
      synopsis.setText(movie.overview);

      TMDBClient client = TMDBClient.getInstance();
      client.getTrailers(movie.id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            final List<Trailer> trailers = Trailer.fromJSONArray(response);
            LinearLayout XMLLayout = (LinearLayout) findViewById(R.id.trailers);
            for (final Trailer trailer : trailers) {
               RelativeLayout dynamicLayout = new RelativeLayout(DetailActivity.this);
               RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                     RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
               dynamicLayout.setLayoutParams(layoutParams);

               ImageButton button = new ImageButton(DetailActivity.this);
               button.setImageResource(R.drawable.trailer_play);
               button.setBackgroundColor(Color.TRANSPARENT);
               // button.setId() is necessary for nameParams.addRule() below
               button.setId(1);
               RelativeLayout.LayoutParams buttonParams = new RelativeLayout.LayoutParams(
                     ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               buttonParams.setMargins(20, 0, 20, 0);
               dynamicLayout.addView(button, buttonParams);
               button.setOnClickListener(new View.OnClickListener() {
                  @Override
                  public void onClick(View v) {
                     Uri uri = Uri.parse("http://www.youtube.com/watch?v=" + trailer.key);
                     startActivity(new Intent(Intent.ACTION_VIEW, uri));
                  }
               });

               TextView name = new TextView(DetailActivity.this);
               name.setText(trailer.name);
               name.setGravity(Gravity.CENTER);
               RelativeLayout.LayoutParams nameParams = new RelativeLayout.LayoutParams(
                     ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
               nameParams.addRule(RelativeLayout.RIGHT_OF, button.getId());
               nameParams.addRule(RelativeLayout.ALIGN_TOP, button.getId());
               nameParams.addRule(RelativeLayout.ALIGN_BOTTOM, button.getId());
               nameParams.setMarginStart(30);
               dynamicLayout.addView(name, nameParams);

               XMLLayout.addView(dynamicLayout);
            }
         }
      });

      client.getReviews(movie.id, new JsonHttpResponseHandler() {
         @Override
         public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
            List<Review> reviews = Review.fromJSONArray(response);
            LinearLayout XMLLayout = (LinearLayout) findViewById(R.id.reviews);
            for (Review review : reviews) {
               LinearLayout dynamicLayout = new LinearLayout(DetailActivity.this);
               dynamicLayout.setOrientation(LinearLayout.VERTICAL);
               dynamicLayout.setPadding(20, 0, 20, 0);

               TextView author = new TextView(DetailActivity.this);
               Spanned html = Html.fromHtml("<i>by <b>" + review.author + ":</b></i>");
               author.setText(html);
               dynamicLayout.addView(author);

               TextView content = new TextView(DetailActivity.this);
               content.setText(review.content);
               content.setPadding(0, 20, 0, 20);
               dynamicLayout.addView(content);

               View separator = new View(DetailActivity.this);
               separator.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 2));
               separator.setBackgroundColor(Color.DKGRAY);
               dynamicLayout.addView(separator);

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
