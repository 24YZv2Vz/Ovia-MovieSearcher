package com.android305.moviesearcher.network.endpoints;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.android305.moviesearcher.network.Connection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Request;

public class OMDBEndpoint extends Endpoint {
    private static final String TAG = "OMDBEndpoint";

    private static final String API_KEY = "ee60b1cc";

    public enum Type {
        MOVIE, SERIES, EPISODE
    }

    private String search;
    private Type type;
    private String year;
    private int page;

    public OMDBEndpoint(String search) {
        this.search = search;
        type = Type.MOVIE; // TODO: Allow searching for specific types or even no types specified. Since this is a "movie searcher" we will be forcing this for now
    }

    @NonNull
    @Override
    public String getURL() {
        return "http://omdbapi.com";
    }

    @NonNull
    @Override
    public String getTag() {
        return TAG;
    }

    @Nullable
    @Override
    public JSONObject getGetParameters() {
        JSONObject getParameters = new JSONObject();
        try {
            getParameters.put("apikey", API_KEY);

            getParameters.put("s", search);
            if (type != null)
                getParameters.put("type", type.name().toLowerCase());
            if (year != null)
                getParameters.put("y", year);
            if (page > 0)
                getParameters.put("page", page);
        } catch (JSONException e) {
            // This is a programming error, crash the app
            throw new RuntimeException(e);
        }
        return getParameters;
    }

    @NonNull
    @Override
    public Request buildRequest(@NonNull Request.Builder request) {
        return request.build();
    }

    @Nullable
    @Override
    public ResultSet getResultSet(Connection.AndroidResponse response) {
        try {
            ResultSet resultSet = new ResultSet();
            JSONObject payload = response.getJsonPayload();
            List<ResultSet.Result> results = new ArrayList<>();
            JSONArray resultsArray = payload.getJSONArray("Search");
            for (int i = 0; i < resultsArray.length(); i++) {
                JSONObject resultJSON = resultsArray.getJSONObject(i);
                ResultSet.Result result = new ResultSet.Result(resultJSON.getString("Title"));
                result.setYear(resultJSON.optString("Year", null));
                try {
                    URL url = new URL(resultJSON.optString("Poster", null));
                    result.setPosterURL(url.toString());
                } catch (MalformedURLException ignored) {
                    // if url fails, a placeholder image will be loaded
                }
                result.setImdbId(resultJSON.optString("imdbID", null));
                result.setType(resultJSON.optString("Type", null));
                results.add(result);
            }
            resultSet.setResults(results);
            resultSet.setTotalResults(payload.getInt("totalResults"));
            resultSet.setPage(1);
            return resultSet;
        } catch (JSONException e) {
            Log.w(TAG, e);
        }
        return null;
    }
}
