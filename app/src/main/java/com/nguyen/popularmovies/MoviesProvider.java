package com.nguyen.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by My on 4/5/2016.
 */
// modeled after tutorial by Derek Banas' "How to Make Android Apps 21" on YouTube
public class MoviesProvider extends ContentProvider {
   static final String PROVIDER_NAME = "com.nguyen.popularmovies.MoviesProvider";
   static final String PATH = "favorites";
   static final String URL = "content://" + PROVIDER_NAME + "/" + PATH;
   static final Uri CONTENT_URI = Uri.parse(URL);

   private static final int uriCode = 1;
   private static HashMap<String, String> values;
   private static final UriMatcher uriMatcher;
   static {
      uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
      uriMatcher.addURI(PROVIDER_NAME, PATH, uriCode);
   }

   private static final String TABLE_NAME = "CPMovies";
   private DBHelper mDBHelper;
   private SQLiteDatabase mDatabase;

   @Nullable
   @Override
   public String getType(Uri uri) {
      switch (uriMatcher.match(uri)) {
         case uriCode:
            return "vnd.android.cursor.dir/" + PATH;
         default:
            throw new IllegalArgumentException("Unsupported URI " + uri);
      }
   }

   @Override
   public boolean onCreate() {
      mDBHelper = new DBHelper(getContext());
      mDatabase = mDBHelper.getWritableDatabase();
      return mDatabase != null;
   }

   @Nullable
   @Override
   public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
      SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
      builder.setTables(TABLE_NAME);
      switch (uriMatcher.match(uri)) {
         case uriCode:
            builder.setProjectionMap(values);
            break;
         default:
            throw new IllegalArgumentException("Unknown URI " + uri);
      }
      Cursor cursor = builder.query(mDatabase, projection, selection, selectionArgs, null, null, sortOrder);
      cursor.setNotificationUri(getContext().getContentResolver(), uri);
      return cursor;
   }

   @Nullable
   @Override
   public Uri insert(Uri uri, ContentValues values) {
      long rowId = mDatabase.insert(TABLE_NAME, null, values);
      if (rowId > 0) {
         Uri result = ContentUris.withAppendedId(CONTENT_URI, rowId);
         getContext().getContentResolver().notifyChange(result, null);
         return result;
      } else {
         Log.d("NGUYEN", "row insert failed");
         return null;
      }
   }

   @Override
   public int delete(Uri uri, String selection, String[] selectionArgs) {
      int count = 0;
      switch (uriMatcher.match(uri)) {
         case uriCode:
            count = mDatabase.delete(TABLE_NAME, selection, selectionArgs);
            break;
         default:
            throw new IllegalArgumentException("Unknown URI " + uri);
      }
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }

   @Override
   public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
      int count = 0;
      switch (uriMatcher.match(uri)) {
         case uriCode:
            count = mDatabase.update(TABLE_NAME, values, selection, selectionArgs);
            break;
         default:
            throw new IllegalArgumentException("Unknown URI " + uri);
      }
      getContext().getContentResolver().notifyChange(uri, null);
      return count;
   }

   private static class DBHelper extends SQLiteOpenHelper {
      private static final int DB_VERSION = 1;
      private static final String DB_NAME = "popular_movies";
      private static final String COL_ID = "id";
      private static final String COL_ORIGINAL_TITLE = "original_title";
      private static final String COL_POSTER_PATH = "poster_path";
      private static final String COL_OVERVIEW = "overview";
      private static final String COL_VOTE_AVERAGE = "vote_average";
      private static final String COL_RELEASE_DATE = "release_date";
      private static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (" +
            COL_ID + " INTEGER PRIMARY KEY UNIQUE ON CONFLICT REPLACE, " +
            COL_ORIGINAL_TITLE + " TEXT, " +
            COL_POSTER_PATH + " TEXT, " +
            COL_OVERVIEW + " TEXT, " +
            COL_VOTE_AVERAGE + " REAL, " +
            COL_RELEASE_DATE + " TEXT" + ")";

      public DBHelper(Context context) {
         super(context, DB_NAME, null, DB_VERSION);
      }

      @Override
      public void onCreate(SQLiteDatabase db) {
         db.execSQL(CREATE_TABLE);
      }

      @Override
      public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
         db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
         onCreate(db);
      }
   }
}
