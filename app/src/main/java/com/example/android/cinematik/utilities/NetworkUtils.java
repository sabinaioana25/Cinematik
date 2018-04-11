package com.example.android.cinematik.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Created by Sabina on 3/19/2018.
 */

public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /*
     The query parameters allow sorting of the movies by popularity, rating and genre
    */
    private static final String SORT_BY_PARAM = "sort_by";
    public static final String POPULAR_PARAM = "popularity.desc";
    public static final String TOP_RATED_PARAM = "vote_average.desc";
    public static final String VOTE_COUNT_GTE_PARAM = "vote_count.gte";
    public static final String VOTE_COUNT_VALUE = "2000";
    private static final String PAGE_NUMBER_PARAM = "page";

    /*
     Main API URL components
     */
    private static final String URL_SCHEME = "https";
    private static final String URL_AUTHORITY = "api.themoviedb.org";
    private static final String URL_PATH_VERSION = "3";
    private static final String URL_PATH_DISCOVER = "discover";
    private static final String URL_PATH_MOVIES = "movie";
    private static final String API_KEY = "9f1d1dfcda4410144eaa3fec1fea5d4a";
    private static final String API_KEY_PARAM = "api_key";

    /*
     Poster API URL components
     */
    private static final String URL_POSTER_AUTHORITY = "image.tmdb.org";
    private static final String URL_POSTER_PATH_T = "t";
    private static final String URL_POSTER_PATH_P = "p";
    public static final String URL_POSTER_SIZE_VALUE = "w342";
    public static final String URL_BACKDROP_SIZE_VALUE = "w154";
    public static final String URL_PROFILE_SIZE_VALUE = "w45";
    public static final int URL_PAGE_VALUE = 20;


     /*
      Keys to call on values from the API
      */

    private static final String URL_APPEND_TO_RESPONSE_KEY = "append_to_response";
    private static final String URL_APPEND_TO_RESPONSE_VALUE = "credits,reviews";
    /*
      DetailActivity review components
     */

    private NetworkUtils() {
    }

    public static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
        }
        return url;
    }

    public static String makeHttpRequest(URL url, Context context) throws IOException {
        String jsonResponse = null;
        if (url == null) {
            return jsonResponse;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000);
            urlConnection.setConnectTimeout(10000);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
            }
        } catch (IOException e) {
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            return jsonResponse;
        }
    }

    public static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    public static String buildUrlMovieActivity(String sort_by) {
        Uri.Builder uriBuilderPopular = new Uri.Builder();
        uriBuilderPopular.scheme(URL_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath(URL_PATH_VERSION)
                .appendPath(URL_PATH_DISCOVER)
                .appendPath(URL_PATH_MOVIES)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(SORT_BY_PARAM, sort_by)
                .build();

        String urlMoviesActivity = uriBuilderPopular.toString();
        return urlMoviesActivity;
    }

    public static URL buildUrlDetailActivity(int id) {
        Uri.Builder uriBuildDetailActivity = new Uri.Builder();
        uriBuildDetailActivity.scheme(URL_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath(URL_PATH_VERSION)
                .appendPath(URL_PATH_MOVIES)
                .appendPath(String.valueOf(id))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(URL_APPEND_TO_RESPONSE_KEY, URL_APPEND_TO_RESPONSE_VALUE)
                .build();
        Log.e(LOG_TAG, "IS IT WORKING?? " +uriBuildDetailActivity.toString());
        return buildUrl(uriBuildDetailActivity.toString());
    }

    private static URL buildUrl(String uri) {
        try {
            URL detailMovieQuery = new URL(uri);
            return detailMovieQuery;
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Request could not be completed ", e);
        }
        return null;
    }


    public static String buildUrlPoster(String path, String size) {
        Uri.Builder uriBuilderPoster = new Uri.Builder();
        uriBuilderPoster.scheme(URL_SCHEME)
                .authority(URL_POSTER_AUTHORITY)
                .appendPath(URL_POSTER_PATH_T)
                .appendPath(URL_POSTER_PATH_P)
                .appendPath(size)
                .appendPath(path)
                .build();
        String urlPoster = uriBuilderPoster.toString();
        return urlPoster;
    }
}

