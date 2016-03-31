package com.nguyen.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by My on 3/28/2016.
 */
public class Trailer {
   String id;
   String key;
   String name;
   int size;

   public static Trailer fromJSONObject(JSONObject jsonObject) {
      Trailer trailer = new Trailer();
      try {
         trailer.id = jsonObject.getString("id");
         trailer.key = jsonObject.getString("key");
         trailer.name = jsonObject.getString("name");
         trailer.size = jsonObject.getInt("size");
      } catch (JSONException e) {
         e.printStackTrace();
      }

      return trailer;
   }

   public static List<Trailer> fromJSONArray(JSONObject response) {
      List<Trailer> trailers = new ArrayList<>();
      try {
         JSONArray jsonArray = response.getJSONArray("results");
         for (int i = 0; i < jsonArray.length(); i++) {
            try {
               JSONObject jsonObject = jsonArray.getJSONObject(i);
               Trailer trailer = fromJSONObject(jsonObject);
               if (trailer != null)
                  trailers.add(trailer);
            } catch (JSONException e) {
               e.printStackTrace();
               // keep deserializing the next JSONObject in the JSONArray even if the deserialization
               // of the current JSONObject fails
               continue;
            }
         }
      } catch (JSONException e) {
         e.printStackTrace();
      }

      return trailers;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      builder.append("<id: ").append(id)
            .append("><key: ").append(key)
            .append("><name: ").append(name)
            .append("><size: ").append(size)
            .append(">\n");
      return builder.toString();
   }
}
