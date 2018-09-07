package com.android305.moviesearcher.network.endpoints;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.android305.moviesearcher.network.Connection;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.List;

import okhttp3.Request;

public abstract class Endpoint {

    Endpoint() {
    }

    @NonNull
    public abstract String getURL();

    @NonNull
    public abstract String getTag();

    @Nullable
    public abstract JSONObject getGetParameters();

    @NonNull
    public abstract Request buildRequest(@NonNull Request.Builder request);

    @Nullable
    public abstract ResultSet getResultSet(Connection.AndroidResponse response);

    public static class ResultSet implements Serializable {
        private List<Result> results;
        private int page;
        private int totalResults;

        ResultSet() {
        }

        public List<Result> getResults() {
            return results;
        }

        void setResults(List<Result> results) {
            this.results = results;
        }

        public int getPage() {
            return page;
        }

        void setPage(int page) {
            this.page = page;
        }

        public int getTotalResults() {
            return totalResults;
        }

        void setTotalResults(int totalResults) {
            this.totalResults = totalResults;
        }

        @Override
        public String toString() {
            return "ResultSet{" +
                    "results=" + results +
                    ", page=" + page +
                    ", totalResults=" + totalResults +
                    '}';
        }

        public static class Result implements Serializable {

            private String title;
            private String year;
            private String posterURL;
            private String imdbId;
            private String type;
            private String plot;

            Result(String title) {
                this.title = title;
            }

            @NonNull
            public String getTitle() {
                return title;
            }

            @Nullable
            public String getYear() {
                return year;
            }

            void setYear(String year) {
                this.year = year;
            }

            @Nullable
            public String getPosterURL() {
                return posterURL;
            }

            void setPosterURL(String posterURL) {
                this.posterURL = posterURL;
            }

            @Nullable
            public String getImdbId() {
                return imdbId;
            }

            void setImdbId(String imdbId) {
                this.imdbId = imdbId;
            }

            @Nullable
            public String getType() {
                return type;
            }

            void setType(String type) {
                this.type = type;
            }

            @Nullable
            public String getPlot() {
                return plot;
            }

            void setPlot(String plot) {
                this.plot = plot;
            }

            @Override
            public String toString() {
                return "Result{" +
                        "title='" + title + '\'' +
                        ", year='" + year + '\'' +
                        ", posterURL='" + posterURL + '\'' +
                        ", imdbId='" + imdbId + '\'' +
                        ", type='" + type + '\'' +
                        ", plot='" + plot + '\'' +
                        '}';
            }
        }
    }
}
