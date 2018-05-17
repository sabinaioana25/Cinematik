package com.example.android.cinematik.utilities;

import android.content.Context;
import android.text.TextUtils;

import com.example.android.cinematik.pojos.CastMember;
import com.example.android.cinematik.pojos.MovieItem;
import com.example.android.cinematik.pojos.ReviewItem;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Sabina on 3/19/2018.
 */

public class MovieJsonUtils {

    private static final String TAG = MovieJsonUtils.class.getSimpleName();

      /*
     * DetailActivity review components
     */
    private static final String URL_PATH_REVIEWS = "reviews";
    private static final String DETAIL_REVIEW_RESULTS = "results";
    private static final String DETAIL_REVIEW_AUTHOR_NAME = "author";
    private static final String DETAIL_REVIEW_CONTENT = "content";
    /*
    DetailActivity components to extract from JSON
     */
    private static final String KEY_RESULTS = "results";
    private static final String KEY_POSTER = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_MOVIE_ID = "id";
    private static final String KEY_TITLE = "title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String DETAIL_GENRES = "genres";
    private static final String KEY_GENRE_NAME = "name";
    private static final String DETAIL_VOTE_AVERAGE = "vote_average";
    private static final String DETAIL_RUNTIME = "runtime";
    private static final String DETAIL_CREDITS = "credits";
    private static final String DETAIL_CAST = "cast";
    private static final String CAST_CHARACTER_NAME = "character";
    private static final String CAST_PROFILE_PATH = "profile_path";
    private static final String CAST_ACTOR_NAME = "name";
    private static final String DETAIL_CREW = "crew";
    private static final String CREW_PERSON_NAME = "name";
    private static final String CREW_JOB = "job";
    private static final String CREW_JOB_DIRECTOR = "Director";
    private static final String CREW_JOB_PRODUCER = "Producer";
    private static final String CREW_PROFILE_PATH = "profile_path";
    private static final String DETAIL_VIDEOS = "videos";

    private MovieJsonUtils() {
    }

    public static List<MovieItem> getMovieData(String requestUrl, Context context) {
        URL url = NetworkUtils.createUrl(requestUrl);
        String jsonResponse = null;

        try {
            jsonResponse = NetworkUtils.makeHttpRequest(url, context);
        } catch (IOException e) {
        }

        List<MovieItem> movieItems = extractFeatureFromJson(jsonResponse);
        return movieItems;
    }

    public static List<MovieItem> extractFeatureFromJson(String jsonResponse) {

         /*
        Create an empty List<MovieItem>
         */
        List<MovieItem> movieItemsMain = new ArrayList<MovieItem>();

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }

        /*
        Build the list of Movie objects
         */
        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);
            JSONArray movieResult = baseJsonResponse.getJSONArray(KEY_RESULTS);
            String poster = null;
            int id;
            for (int i = 0; i < movieResult.length(); i++) {
                JSONObject movieObject = movieResult.getJSONObject(i);

                // Check if poster exists
                poster = movieObject.optString(KEY_POSTER);

                // Check if ID exists
                id = movieObject.optInt(KEY_MOVIE_ID);

                MovieItem movieItemMainActivity = new MovieItem(poster,
                        id,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null,
                        null);
                movieItemsMain.add(movieItemMainActivity);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movieItemsMain;
    }

    public static MovieItem extractDetailsFromJson(String jsonResponse) {

        /*
        Create an empty List<MovieItem>
         */
        MovieItem movieList = null;

        if (TextUtils.isEmpty(jsonResponse)) {
            return null;
        }
         /*
        Build the list of Movie objects
         */

        try {
            JSONObject baseJsonResponse = new JSONObject(jsonResponse);

            // ID
            int jsonID = baseJsonResponse.getInt(KEY_MOVIE_ID);

            String poster = null;
            poster = baseJsonResponse.optString(KEY_POSTER);

            // backdrop_path
            String jsonBackdrop = null;
            String jsonBackdropPath = baseJsonResponse.optString(KEY_BACKDROP_PATH);
            if (jsonBackdropPath != null) {
                jsonBackdrop = NetworkUtils.buildUrlImage(
                        jsonBackdropPath.substring(1),
                        NetworkUtils.URL_BACKDROP_SIZE_VALUE);
            }

            // Check if title exists
            String title = null;
            title = baseJsonResponse.optString(KEY_TITLE);

            // Check if release date exists
            String releaseDate = null;
            releaseDate = baseJsonResponse.optString(KEY_RELEASE_DATE);

            // Check if genres exist
            List<String> jsonGenres = new ArrayList<>();
            JSONArray jsonGenresArray = baseJsonResponse.optJSONArray(DETAIL_GENRES);
            if (jsonGenresArray.length() != 0) {
                for (int i = 0; i < Math.min(jsonGenresArray.length(), 3); i++) {
                    JSONObject jsonCurrentMovie = jsonGenresArray.getJSONObject(i);
                    jsonGenres.add(jsonCurrentMovie.optString(KEY_GENRE_NAME));
                }
            } else {
                jsonGenres = null;
            }

            // Check if voteAverage number exists
            String voteAverage = null;
            voteAverage = "IMDB " + baseJsonResponse.optString(DETAIL_VOTE_AVERAGE);

            // Check if overview exists
            String overview = null;
            overview = baseJsonResponse.optString(KEY_OVERVIEW);

            // Check if runtime exists
            String runtime = null;
            runtime = baseJsonResponse.optString(DETAIL_RUNTIME);

            // Check for Cast members
            List<CastMember> jsonCastMembers = new ArrayList<>();
            JSONObject jsonCredits = baseJsonResponse.getJSONObject(DETAIL_CREDITS);
            JSONArray jsonCastArray = jsonCredits.optJSONArray(DETAIL_CAST);
            if (jsonCastArray.length() > 0) {
                for (int i = 0; i < Math.min(jsonCastArray.length(), 10); i++) {
                    JSONObject jsonCast = jsonCastArray.optJSONObject(i);
                    String jsonCastActorName = jsonCast.optString(CAST_ACTOR_NAME);
                    String jsonCastCharName = jsonCast.optString(CAST_CHARACTER_NAME);

                    String jsonCastProfilePath = jsonCast.optString(CAST_PROFILE_PATH);
                    String jsonCastProfile = null;
                    if (jsonCastProfilePath != null) {
                        jsonCastProfile = NetworkUtils.buildUrlImage
                                (jsonCastProfilePath.substring(1),
                                        NetworkUtils.URL_PROFILE_SIZE_VALUE);
                    }
                    jsonCastMembers.add(new CastMember(jsonCastActorName,
                            jsonCastCharName,
                            jsonCastProfile));
                }
            }

            // Check for crewMembers
            List<CastMember> jsonCrewMembers = new ArrayList<>();
            JSONArray jsonCrewArray = jsonCredits.optJSONArray(DETAIL_CREW);
            String jsonCrewDirector = null;
            String jsonCrewProducer = null;
            if (jsonCrewArray.length() > 0) {
                for (int i = 0; i < jsonCrewArray.length(); i++) {
                    JSONObject jsonCrew = jsonCrewArray.optJSONObject(i);
                    String jsonCrewJob = jsonCrew.optString(CREW_JOB);
                    if (!(jsonCrewJob.equals(CREW_JOB_DIRECTOR) || jsonCrewJob.equals
                            (CREW_JOB_PRODUCER)))
                        continue;

                    String jsonCrewName = jsonCrew.optString(CREW_PERSON_NAME);
                    jsonCrewDirector = jsonCrew.optString(CREW_PERSON_NAME);
                    jsonCrewProducer = jsonCrew.optString(CREW_PERSON_NAME);
                    String jsonCrewProfilePath = jsonCrew.optString(CREW_PROFILE_PATH);
                    if (jsonCrewProfilePath != null) {
                        continue;
                    }

                    String jsonCrewProfileImage = NetworkUtils.buildUrlImage(
                            jsonCrewProfilePath, NetworkUtils.URL_PROFILE_SIZE_VALUE);

                    jsonCrewMembers.add(new CastMember(jsonCrewName, jsonCrewJob,
                            jsonCrewProfileImage));
                }
            } else {
                jsonCastMembers = null;
            }

            // Check if reviews exist
            List<ReviewItem> jsonReviewItems = new ArrayList<>();
            JSONObject jsonReview = baseJsonResponse.getJSONObject(URL_PATH_REVIEWS);
            JSONArray jsonReviewResults = jsonReview.optJSONArray(DETAIL_REVIEW_RESULTS);
            if (jsonReviewResults.length() > 0) {
                for (int i = 0; i < Math.min(jsonReviewResults.length(), 5); i++) {
                    JSONObject jsonReviewItem = jsonReviewResults.getJSONObject(i);
                    String jsonReviewAuthor = jsonReviewItem.optString(DETAIL_REVIEW_AUTHOR_NAME);
                    String jsonReviewContent = jsonReviewItem.optString(DETAIL_REVIEW_CONTENT);

                    jsonReviewItems.add(new ReviewItem(jsonReviewAuthor, jsonReviewContent));
                }
            } else {
                jsonReviewItems = null;

            }

            // Check if videos are available
            JSONObject jsonVideosObject = baseJsonResponse.optJSONObject(DETAIL_VIDEOS);
            JSONArray jsonVideosArray = jsonVideosObject.getJSONArray(KEY_RESULTS);
            String jsonKeyTrailer = null;
            if (jsonVideosArray.length() != 0) {
                for (int i = 0; i < jsonVideosArray.length(); i++) {
                    JSONObject jsonCurrentMovie = jsonVideosArray.getJSONObject(i);
                    jsonKeyTrailer = jsonCurrentMovie.optString("key");
                }
            }

            // return movieList
            movieList = new MovieItem(poster,
                    jsonID,
                    jsonBackdrop,
                    title,
                    releaseDate,
                    jsonGenres,
                    voteAverage,
                    overview,
                    runtime,
                    jsonCastMembers,
                    jsonCrewDirector,
                    jsonCrewProducer,
                    jsonReviewItems,
                    jsonKeyTrailer);

            return movieList;

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}

