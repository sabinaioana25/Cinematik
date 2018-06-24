package com.example.android.cinematik.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.android.cinematik.BuildConfig;
import com.example.android.cinematik.data.MoviePreferences;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;

@SuppressWarnings("unused")
public class NetworkUtils {

    private static final String LOG_TAG = NetworkUtils.class.getSimpleName();

    /*
     The query parameters allow sorting of the movies by popularity, rating and genre
    */
    public static final String SORT_BY_PARAM = "sort_by";
    public static final String TOP_RATED_PARAM = "top_rated";
    public static final String MOST_POPULAR_PARAM = "popular";

    /*
     Main API URL components
     */
    private static final String URL_SCHEME = "http";
    private static final String URL_AUTHORITY = "api.themoviedb.org";

    //    private static final String URL_PATH_DISCOVER = "discover";
    private static final String URL_PATH_VERSION = "3";
    private static final String URL_PATH_MOVIES = "movie";

    // api key details
    private static final String API_KEY_PARAM = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;

    //    private static final String URL_VOTE_COUNT_KEY = "vote_count.gte";
    //    public static final String URL_VOTE_COUNT = "2500";

    private static final int READ_TIME_OUT = 10000;
    private static final int CONN_TIME_OUT = 10000;

    /*
     Poster API URL components
     */
    private static final String URL_POSTER_AUTHORITY = "image.tmdb.org";
    private static final String URL_POSTER_PATH_T = "t";
    private static final String URL_POSTER_PATH_P = "p";
    public static final String URL_POSTER_SIZE_VALUE = "w500";
    public static final String URL_BACKDROP_SIZE_VALUE = "original";
    public static final String URL_PROFILE_SIZE_VALUE = "original";
    /*
     Keys to call on values from the API
     */
    private static final String URL_APPEND_TO_RESPONSE_KEY = "append_to_response";
    private static final String URL_APPEND_TO_RESPONSE_VALUE_CREDITS = "credits";
    private static final String URL_APPEND_TO_RESPONSE_REVIEWS = "reviews";
    private static final String URL_APPEND_TO_RESPONSE_VIDEOS = "videos";
    private static final String URL_YOUTUBE_AUTHORITY = "youtube.com";
    private static final String URL_YOUTUBE_PATH = "watch";
    private static final String URL_APPEND_TO_RESPONSE_VIDEO_KEY = "v";

    // constructor
    private NetworkUtils() {
    }

    static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException ignored) {
        }
        return url;
    }

    public static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = null;
        if (url == null) {
            return null;
        }
        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;

        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(READ_TIME_OUT);
            urlConnection.setConnectTimeout(CONN_TIME_OUT);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            if (urlConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
            }
        } catch (IOException ignored) {
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

    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset
                    .forName("UTF-8"));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while (line != null) {
                output.append(line);
                line = bufferedReader.readLine();
            }
        }
        return output.toString();
    }

    private static URL buildUrl(Uri.Builder uri) {
        try {
            return new URL(uri.toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Request could not be completed ", e);
        }
        return null;
    }

    public static String buildUrlMovieActivity(Context context, String sort_by) {
        Uri.Builder uriBuilderMovieList = new Uri.Builder();
        uriBuilderMovieList.scheme(URL_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath(URL_PATH_VERSION)
                .appendPath(URL_PATH_MOVIES)
                .appendPath(MoviePreferences.getSortByPreference(context))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        return uriBuilderMovieList.toString();
    }

    public static URL buildUrlDetailActivity(int id) {
        Uri.Builder uriBuildDetailActivity = new Uri.Builder();
        uriBuildDetailActivity.scheme(URL_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath(URL_PATH_VERSION)
                .appendPath(URL_PATH_MOVIES)
                .appendPath(String.valueOf(id))
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .appendQueryParameter(URL_APPEND_TO_RESPONSE_KEY,
                        URL_APPEND_TO_RESPONSE_VALUE_CREDITS)
                .build();
        return buildUrl(uriBuildDetailActivity);
    }

    public static String buildUrlImage(String path, String size) {
        Uri.Builder uriBuilderPoster = new Uri.Builder();
        uriBuilderPoster.scheme(URL_SCHEME)
                .authority(URL_POSTER_AUTHORITY)
                .appendPath(URL_POSTER_PATH_T)
                .appendPath(URL_POSTER_PATH_P)
                .appendPath(size)
                .appendPath(path)
                .build();
        return uriBuilderPoster.toString();
    }

    public static URL buildUrlReviewList(int id) {
        Uri.Builder uriBuilderDetailReviewsList = new Uri.Builder();
        uriBuilderDetailReviewsList.scheme(URL_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath(URL_PATH_VERSION)
                .appendPath(URL_PATH_MOVIES)
                .appendPath(String.valueOf(id))
                .appendPath(URL_APPEND_TO_RESPONSE_REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        return buildUrl(uriBuilderDetailReviewsList);
    }

    public static URL buildUrlVideo(int id) {
        Uri.Builder uriBuilderVideo = new Uri.Builder();
        uriBuilderVideo.scheme(URL_SCHEME)
                .authority(URL_AUTHORITY)
                .appendPath(URL_PATH_VERSION)
                .appendPath(URL_PATH_MOVIES)
                .appendPath(String.valueOf(id))
                .appendPath(URL_APPEND_TO_RESPONSE_VIDEOS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();
        return buildUrl(uriBuilderVideo);
    }

    public static String buildUrlVideoFromYoutube(String videoKey) {
        Uri.Builder uriBuilderVideos = new Uri.Builder();
        uriBuilderVideos.scheme(URL_SCHEME)
                .authority(URL_YOUTUBE_AUTHORITY)
                .path(URL_YOUTUBE_PATH)
                .appendQueryParameter(URL_APPEND_TO_RESPONSE_VIDEO_KEY, videoKey)
                .build();
        return uriBuilderVideos.toString();
    }
}

