package com.example.android.beatmymovies;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class MovieDetailFragment extends Fragment {
    ArrayList<Trailor> trailorList = new ArrayList<>();
    static final String DETAIL_URI = "URI";
    TrailorListAdapter movieTrailorAdapter;
    private Uri mUri;
    private boolean mTwoPane;
    private MovieForGrid mov;
    private String posterPath;
    private  String original_title;
    private  String overview;
    private  String vote_count;
    private  String release_date;
    private int id;
    TrailorListAdapter trailorListAdapter;
    ReviewListAdapter reviewListAdapter;

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    public MovieDetailFragment() {
    }
    public MovieDetailFragment(MovieForGrid movie) {
        mov = movie;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_movie_detail, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int defaultValue = 2;
        long highScore = sharedPref.getInt(getString(R.string.panes), defaultValue);
        if(highScore == 1) {
            mTwoPane = true;

            TextView title = (TextView) rootView.findViewById(R.id.detail_activity_title);
            if (mov != null) {
                title.setText(mov.original_title);
                original_title = mov.original_title;
            }

            ImageView poster = (ImageView) rootView.findViewById(R.id.detail_activity_image);
            posterPath = mov.poster_path;
            String realPath = "http://image.tmdb.org/t/p/w185" + posterPath;
            Picasso.with(rootView.getContext()).load(realPath).into(poster);

            TextView releaseDate = (TextView) rootView.findViewById(R.id.detail_activity_release_date);
            releaseDate.setText(mov.release_date);

            release_date = mov.release_date;

            TextView votes = (TextView) rootView.findViewById(R.id.detail_activity_votes);
            votes.setText(mov.vote_count);

            vote_count = mov.vote_count;
            TextView overView = (TextView) rootView.findViewById(R.id.detail_activity_overview);
            overView.setText(mov.overview);

            overview = mov.overview;
            id = mov.id;
            //Log.v(LOG_TAG, "The ID is: " + id);
            getTrailorsAndRevies(id, rootView);
        }

        return rootView;
    }

    private void getTrailorsAndRevies(int id,View rootView) {

        FetchMovieTrailorsTask movieTrailorsTaskTask = new FetchMovieTrailorsTask();

        movieTrailorsTaskTask.execute(id);
        ListView listView = (ListView) rootView.findViewById(R.id.trailor_list_view);

        trailorListAdapter = new TrailorListAdapter(listView.getContext(), 0, trailorList);
        listView.setAdapter(trailorListAdapter);
        FetchMovieReviewsTask movieReviewsTask = new FetchMovieReviewsTask();
        movieReviewsTask.execute(id);

        ListView reviewListView = (ListView) rootView.findViewById(R.id.review_list_view);

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

}
