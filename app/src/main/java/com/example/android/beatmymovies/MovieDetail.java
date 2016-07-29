package com.example.android.beatmymovies;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MovieDetail extends AppCompatActivity {
    private static final String LOG_TAG = MovieDetail.class.getSimpleName();
    TrailorListAdapter trailorListAdapter;
    ReviewListAdapter reviewListAdapter;

    private String posterPath;
    private  String original_title;
    private  String overview;
    private  String vote_count;
    private  String release_date;
    private int id;

    ArrayList<Trailor> trailorList = new ArrayList<>();
    ArrayList<Review> reviewList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_movie_detail);

        Bundle b = getIntent().getExtras();
        MovieForGrid movie = b.getParcelable("movieObj");


        TextView title = (TextView) findViewById(R.id.detail_activity_title);
        if (movie != null) {
            title.setText(movie.original_title);
            original_title = movie.original_title;
        }

        ImageView poster = (ImageView) findViewById(R.id.detail_activity_image);
        posterPath = movie.poster_path;
        String realPath = "http://image.tmdb.org/t/p/w185" + posterPath;
        Picasso.with(getApplicationContext()).load(realPath).into(poster);

        TextView releaseDate = (TextView) findViewById(R.id.detail_activity_release_date);
        releaseDate.setText(movie.release_date);

        release_date = movie.release_date;

        TextView votes = (TextView) findViewById(R.id.detail_activity_votes);
        votes.setText(movie.vote_count);

        vote_count = movie.vote_count;
        TextView overView = (TextView) findViewById(R.id.detail_activity_overview);
        overView.setText(movie.overview);

        overview = movie.overview;
        id = movie.id;
        //Log.v(LOG_TAG, "The ID is: " + id);
        getTrailorsAndRevies(id);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_movie_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void getTrailorsAndRevies(int id) {


        FetchMovieTrailorsTask movieTrailorsTaskTask = new FetchMovieTrailorsTask();

        movieTrailorsTaskTask.execute(id);
        ListView listView = (ListView) findViewById(R.id.trailor_list_view);

        trailorListAdapter = new TrailorListAdapter(listView.getContext(), 0, trailorList);
        listView.setAdapter(trailorListAdapter);
        FetchMovieReviewsTask movieReviewsTask = new FetchMovieReviewsTask();
        movieReviewsTask.execute(id);

        ListView reviewListView = (ListView) findViewById(R.id.review_list_view);

        reviewListAdapter = new ReviewListAdapter(reviewListView.getContext(), 0, new ArrayList<Review>());
        reviewListView.setAdapter(reviewListAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Trailor trailor = trailorListAdapter.getItem(position);
                //Toast.makeText(getApplicationContext(), "Hiya", Toast.LENGTH_SHORT).show();
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + trailor.key));
                    startActivity(intent);
                } catch (ActivityNotFoundException ex) {
                    Intent intent = new Intent(Intent.ACTION_VIEW,
                            Uri.parse("http://www.youtube.com/watch?v=" + trailor.key));
                    startActivity(intent);
                }
            }
        });
    }

    public class FetchMovieTrailorsTask extends AsyncTask<Integer, Void, ArrayList<Trailor>> {
        private final String LOG_TAG = FetchMovieTrailorsTask.class.getSimpleName();

        private ArrayList<Trailor> getMovieDataFromJson(String movieDetailsJsonStr)
                throws JSONException {
            ArrayList<Trailor> trailorList = new ArrayList<>();
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_PAGE = "page";
            final String OWM_RESULTS = "results";
            final String OWM_KEY = "key";
            final String OWM_NAME = "name";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(movieDetailsJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);


            //Log.v(LOG_TAG, "Movie Array: " + moviesArray);


            for (int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"

                // Get the JSON object representing the day
                JSONObject movieObject = moviesArray.getJSONObject(i);

                //Log.v(LOG_TAG, "Movie Object: " + movieObject);

                String id = movieObject.getString(OWM_ID);
                String key = movieObject.getString(OWM_KEY);
                String name = movieObject.getString(OWM_NAME);
                //String averageVote = movieObject.getString(OWM_VOTE_AVERAGE);
                //String description = movieObject.getString(OWM_DESCRIPTION);
                //int id = movieObject.getInt(OWM_ID);
                //Log.v(LOG_TAG, "Movie entry: " + s);

                //resultStrs[i].original_title = title;
                //resultStrs[i].poster_path = posterPath;
                //resultStrs[i].release_date = releaseDate;
                //resultStrs[i].vote_count = averageVote;
                //resultStrs[i].overview = description;

                //resultStrs[i] = new GridItem(posterPath, title, description, averageVote, releaseDate, id);
                Trailor trailor = new Trailor(name, id, key);

                trailorList.add(trailor);

            }
            for (Trailor s : trailorList) {
                //Log.v(LOG_TAG, "Trailor entry: " + s);
            }
            return trailorList;
        }

        @Override
        protected ArrayList<Trailor> doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieDetailsJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/videos?";
                //final String MOVIE_ID = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        //.appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                Log.v(LOG_TAG, "Built URI " + url.toString());

                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL(baseUrl.concat(apiKey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieDetailsJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie JSon String: " + movieDetailsJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieDetailsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Trailor> result) {
            if (result != null) {
                trailorListAdapter.clear();
                for (Trailor movieStrStr : result) {
                    if (movieStrStr != null) {
                        trailorListAdapter.add(movieStrStr);
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    public class FetchMovieReviewsTask extends AsyncTask<Integer, Void, ArrayList<Review>> {
        private final String LOG_TAG = FetchMovieReviewsTask.class.getSimpleName();

        private ArrayList<Review> getMovieDataFromJson(String movieDetailsJsonStr)
                throws JSONException {
            ArrayList<Review> reviewList = new ArrayList<>();
            // These are the names of the JSON objects that need to be extracted.
            final String OWM_PAGE = "page";
            final String OWM_RESULTS = "results";
            final String OWM_AUTHOR = "author";
            final String OWM_CONTENT = "content";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(movieDetailsJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);


            //Log.v(LOG_TAG, "Movie Array: " + moviesArray);


            for (int i = 0; i < moviesArray.length(); i++) {
                // For now, using the format "Day, description, hi/low"

                // Get the JSON object representing the day
                JSONObject movieObject = moviesArray.getJSONObject(i);

                //Log.v(LOG_TAG, "Movie Object: " + movieObject);

                String id = movieObject.getString(OWM_ID);
                String author = movieObject.getString(OWM_AUTHOR);
                String content = movieObject.getString(OWM_CONTENT);
                //String averageVote = movieObject.getString(OWM_VOTE_AVERAGE);
                //String description = movieObject.getString(OWM_DESCRIPTION);
                //int id = movieObject.getInt(OWM_ID);
                //Log.v(LOG_TAG, "Movie entry: " + s);

                //resultStrs[i].original_title = title;
                //resultStrs[i].poster_path = posterPath;
                //resultStrs[i].release_date = releaseDate;
                //resultStrs[i].vote_count = averageVote;
                //resultStrs[i].overview = description;

                //resultStrs[i] = new GridItem(posterPath, title, description, averageVote, releaseDate, id);
                Review review = new Review(author, content);

                reviewList.add(review);

            }
            for (Review s : reviewList) {
                Log.v(LOG_TAG, "Trailor entry: " + s);
            }
            return reviewList;
        }

        @Override
        protected ArrayList<Review> doInBackground(Integer... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String movieDetailsJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/movie/" + params[0] + "/reviews?";
                //final String MOVIE_ID = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        //.appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());


                Log.v(LOG_TAG, "Built URI " + url.toString());

                //String apiKey = "&APPID=" + BuildConfig.OPEN_WEATHER_MAP_API_KEY;
                //URL url = new URL(baseUrl.concat(apiKey));

                // Create the request to OpenWeatherMap, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                movieDetailsJsonStr = buffer.toString();

                Log.v(LOG_TAG, "Movie JSon String: " + movieDetailsJsonStr);

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                // If the code didn't successfully get the weather data, there's no point in attemping
                // to parse it.
                return null;
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            try {
                return getMovieDataFromJson(movieDetailsJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<Review> result) {
            if (result != null) {
                reviewListAdapter.clear();
                for (Review movieStrStr : result) {
                    if (movieStrStr != null) {
                        reviewListAdapter.add(movieStrStr);
                    }
                }
            }
            super.onPostExecute(result);
        }
    }

    public void addToFavourites(View view){
        DatabaseHelper movieTable = new DatabaseHelper(this);
        movieTable.open();
        long d = movieTable.insertRecord(posterPath,original_title ,overview ,vote_count ,release_date ,id);
        Log.v("The power is :", String.valueOf(d));
        movieTable.close();
    }
}
