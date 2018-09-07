package com.android305.moviesearcher.network;

import android.support.annotation.NonNull;
import android.util.Log;

import com.android305.moviesearcher.network.endpoints.Endpoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class Connection {
    private static final String TAG = "Connection";

    public static class AndroidResponse {
        private JSONObject jsonPayload;
        private Response response;

        private AndroidResponse() {
        }

        public JSONObject getJsonPayload() {
            return jsonPayload;
        }

        public Response getHTTPResponse() {
            return response;
        }
    }

    private static final OkHttpClient singleClient = new OkHttpClient();

    public static OkHttpClient buildClient(String url) {
        return singleClient.newBuilder().connectTimeout(1, TimeUnit.SECONDS).readTimeout(5, TimeUnit.SECONDS).eventListener(new PrintingEventListener(url)).build();
    }

    public static Endpoint.ResultSet sendRequest(@NonNull Endpoint endpoint) throws ConnectException {
        return sendRequest(buildClient(endpoint.getTag()), endpoint, 3);
    }

    public static Endpoint.ResultSet sendRequest(@NonNull OkHttpClient client, @NonNull Endpoint endpoint, int retries) throws ConnectException {
        String url = endpoint.getURL();

        String tag = TAG + "." + endpoint.getTag();
        StringBuilder urlBuilder = new StringBuilder(url);

        JSONObject getParameters = endpoint.getGetParameters();
        if (getParameters != null) {
            urlBuilder.append("?");
            try {
                for (Iterator<String> it = getParameters.keys(); it.hasNext(); ) {
                    String param = it.next();
                    urlBuilder.append(param).append('=').append(URLEncoder.encode(getParameters.getString(param), "UTF-8"));
                    if (it.hasNext()) {
                        urlBuilder.append("&");
                    }
                }
            } catch (JSONException | UnsupportedEncodingException e) {
                Log.w(TAG, "Error building get parameter list", e);
                throw new ConnectException(e);
            }
            Log.v(tag, "Sending request to: " + url + " params: " + getParameters.toString());
        } else {
            Log.v(tag, "Sending request to: " + url);
        }
        Request.Builder request;
        try {
            request = new Request.Builder().url(urlBuilder.toString());
        } catch (IllegalArgumentException e) {
            Log.w(tag, "Connection exception", e);
            throw new ConnectException(e);
        }
        Request req = endpoint.buildRequest(request);
        try (Response response = client.newCall(req).execute()) {
            if (response.isSuccessful()) {
                Log.v(tag, "Request successful");
                ResponseBody body = response.body();
                if (body != null) {
                    String bodyString = body.string();
                    Log.v(tag, "Received response: " + bodyString);
                    AndroidResponse androidResponse = new AndroidResponse();
                    androidResponse.response = response;
                    androidResponse.jsonPayload = new JSONObject(bodyString);
                    return endpoint.getResultSet(androidResponse);
                }
                throw new ConnectException("Request succeeded but body was empty. Response code: " + response.code());
            }
            throw new ConnectException("Request failed with error code: " + response.code());
        } catch (Exception e) {
            if (retries > 0)
                return sendRequest(client, endpoint, retries - 1);
            throw new ConnectException(e);
        }
    }
}
