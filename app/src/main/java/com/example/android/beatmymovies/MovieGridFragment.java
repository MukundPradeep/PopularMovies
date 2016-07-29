package com.example.android.beatmymovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

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
public class MovieGridFragment extends Fragment {

    MovieGridAdapter gridAdapter;
    ArrayList<MovieForGrid> moviesForGrid = new ArrayList<MovieForGrid>();
    private boolean mTwoPane;

    public MovieGridFragment() {
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.movie_grid_fragment, container, false);

        SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);
        int defaultValue = 1;
        long highScore = sharedPref.getInt(getString(R.string.panes), defaultValue);
        if(highScore == 1) {
            mTwoPane = true;

        }else {
            mTwoPane = false;
            //getSupportActionBar().setElevation(0f);
        }

        GridView moviesGrid = (GridView) rootView.findViewById(R.id.movie_grid_view);
        gridAdapter = new MovieGridAdapter(getActivity(), 0, new ArrayList<MovieForGrid>());

        moviesGrid.setAdapter(gridAdapter);

        moviesGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                MovieForGrid movie = gridAdapter.getItem(position);
                //Toast.makeText(getActivity(), movie.original_title, Toast.LENGTH_SHORT).show();

                if(mTwoPane == true){
                        getFragmentManager().beginTransaction()
                                .replace(R.id.movie_detail_container, new MovieDetailFragment(movie), "DFTAG")
                                .commit();
                }else{

                Intent intent = new Intent(getActivity(), MovieDetail.class);
                intent.putExtra("movieObj", movie);
                startActivity(intent);
                }
            }
        });
        return rootView;
    }

    public void updateMovies() {
        FetchMoviesTask movieTask = new FetchMoviesTask();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        String sortBy = prefs.getString(getString(R.string.pref_sort_key),
                "popularity.desc");
        String favourites = new String("favourites");
        if (sortBy.equals(favourites)) {
            DatabaseHelper db = new DatabaseHelper(getContext());
            ArrayList<MovieForGrid> moviesForGrid = new ArrayList<MovieForGrid>();
            moviesForGrid = db.getAllMovies();
            gridAdapter.clear();
            for (MovieForGrid movieStrStr : moviesForGrid) {
                if (movieStrStr != null) {
                    gridAdapter.add(movieStrStr);
                }
            }
        } else {
            movieTask.execute(sortBy);
        }
    }

    public class FetchMoviesTask extends AsyncTask<String, Void, ArrayList<MovieForGrid>> {
        private final String LOG_TAG = FetchMoviesTask.class.getSimpleName();

        ArrayList<MovieForGrid> moviesForGrid = new ArrayList<MovieForGrid>();

        private ArrayList<MovieForGrid> getMovieDataFromJson(String moviesJsonStr)
                throws JSONException {

            // These are the names of the JSON objects that need to be extracted.
            final String OWM_PAGE = "page";
            final String OWM_RESULTS = "results";
            final String OWM_POSTER_PATH = "poster_path";
            final String OWM_RELEASE_DATE = "release_date";
            final String OWM_VOTE_AVERAGE = "vote_average";
            final String OWM_DESCRIPTION = "overview";
            final String OWM_TITLE = "original_title";
            final String OWM_ID = "id";

            JSONObject moviesJson = new JSONObject(moviesJsonStr);
            JSONArray moviesArray = moviesJson.getJSONArray(OWM_RESULTS);


            //Log.v(LOG_TAG, "Movie Array: " + moviesArray);


            for (int i = 0; i < moviesArray.length(); i++) {

                // Get the JSON object representing the day
                JSONObject movieObject = moviesArray.getJSONObject(i);

                //Log.v(LOG_TAG, "Movie Object: " + movieObject);

                String title = movieObject.getString(OWM_TITLE);
                String posterPath = movieObject.getString(OWM_POSTER_PATH);
                String releaseDate = movieObject.getString(OWM_RELEASE_DATE);
                String averageVote = movieObject.getString(OWM_VOTE_AVERAGE);
                String description = movieObject.getString(OWM_DESCRIPTION);
                int id = movieObject.getInt(OWM_ID);
                //Log.v(LOG_TAG, "Movie entry: " + s);

                moviesForGrid.add(new MovieForGrid(posterPath, title, description, averageVote, releaseDate, id));
            }
            for (MovieForGrid s : moviesForGrid) {
                //Log.v(LOG_TAG, "Movie entry: " + s);
            }
            return moviesForGrid;
        }

        @Override
        protected ArrayList<MovieForGrid> doInBackground(String... params) {

            if (params.length == 0) {
                return null;
            }

            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String moviesJsonStr = null;


            try {
                // Construct the URL for the OpenWeatherMap query
                // Possible parameters are avaiable at OWM's forecast API page, at
                // http://openweathermap.org/API#forecast
                //String baseUrl = "http://api.openweathermap.org/data/2.5/forecast/daily?q=94043&mode=json&units=metric&cnt=7";
                final String FORECAST_BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";
                final String SORT_BY = "sort_by";
                final String APPID_PARAM = "api_key";

                Uri builtUri = Uri.parse(FORECAST_BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_BY, params[0])
                        .appendQueryParameter(APPID_PARAM, BuildConfig.MOVIE_DB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                Log.v(LOG_TAG, "Built URI " + builtUri.toString());

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
                moviesJsonStr = buffer.toString();

                //Log.v(LOG_TAG, "Movie JSon String: " + moviesJsonStr);

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
                return getMovieDataFromJson(moviesJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(ArrayList<MovieForGrid> result) {
            if (result != null) {
                gridAdapter.clear();
                for (MovieForGrid movieStrStr : result) {
                    if (movieStrStr != null) {
                        gridAdapter.add(movieStrStr);
                    }
                }
            }
            super.onPostExecute(result);
        }
    }
}
