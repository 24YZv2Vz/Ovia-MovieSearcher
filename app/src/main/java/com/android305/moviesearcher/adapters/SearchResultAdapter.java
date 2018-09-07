package com.android305.moviesearcher.adapters;

import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android305.moviesearcher.R;
import com.android305.moviesearcher.network.endpoints.Endpoint;
import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import java.util.Collections;
import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.SearchResultViewHolder> implements ListPreloader.PreloadModelProvider<Endpoint.ResultSet.Result> {
    public interface OnResultAction {
        void onLaunchIMDB(Endpoint.ResultSet.Result result);
    }

    public static class SearchResultViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        TextView type;
        TextView plot;
        public ImageView poster;
        Button imdb;

        SearchResultViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            type = itemView.findViewById(R.id.type);
            plot = itemView.findViewById(R.id.plot);
            poster = itemView.findViewById(R.id.poster);
            imdb = itemView.findViewById(R.id.imdb);
        }
    }

    private Endpoint.ResultSet resultSet;
    private List<Endpoint.ResultSet.Result> resultList;
    private final RequestBuilder<Drawable> glideRequest;
    private final ViewPreloadSizeProvider<Endpoint.ResultSet.Result> preloadSizeProvider;
    private OnResultAction mCallback;

    public SearchResultAdapter(Endpoint.ResultSet resultSet, RequestBuilder<Drawable> glideRequest, ViewPreloadSizeProvider<Endpoint.ResultSet.Result> preloadSizeProvider, OnResultAction mCallback) {
        this.resultSet = resultSet;
        this.resultList = resultSet.getResults();
        this.glideRequest = glideRequest;
        this.preloadSizeProvider = preloadSizeProvider;
        this.mCallback = mCallback;
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    @NonNull
    @Override
    public SearchResultViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int pos) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tile, viewGroup, false);
        return new SearchResultViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final SearchResultViewHolder vh, int pos) {
        final Endpoint.ResultSet.Result result = resultList.get(pos);
        if (result.getYear() != null) {
            vh.title.setText(String.format("%s (%s)", result.getTitle(), result.getYear()));
        } else {
            vh.title.setText(result.getTitle());
        }
        vh.type.setText(result.getType());

        if (result.getPlot() != null) {
            vh.plot.setVisibility(View.VISIBLE);
            vh.plot.setText(result.getPlot());
        } else {
            vh.plot.setVisibility(View.GONE);
        }

        if (result.getImdbId() != null) {
            vh.imdb.setVisibility(View.VISIBLE);
            vh.imdb.setOnClickListener(v -> mCallback.onLaunchIMDB(result));
        } else {
            vh.imdb.setVisibility(View.INVISIBLE);
            vh.imdb.setOnClickListener(null);
        }

        glideRequest.load(result.getPosterURL()).into(vh.poster);

        preloadSizeProvider.setView(vh.poster);
    }

    @NonNull
    @Override
    public List<Endpoint.ResultSet.Result> getPreloadItems(int position) {
        return Collections.singletonList(resultList.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder<?> getPreloadRequestBuilder(@NonNull Endpoint.ResultSet.Result item) {
        if (item.getPosterURL() != null) {
            return glideRequest.load(item.getPosterURL()).apply(new RequestOptions().fallback(R.drawable.ic_launcher_foreground));
        }
        return null;
    }
}