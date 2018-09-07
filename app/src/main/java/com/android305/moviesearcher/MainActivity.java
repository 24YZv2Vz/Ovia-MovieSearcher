package com.android305.moviesearcher;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android305.moviesearcher.adapters.SearchResultAdapter;
import com.android305.moviesearcher.network.ConnectException;
import com.android305.moviesearcher.network.Connection;
import com.android305.moviesearcher.network.endpoints.Endpoint;
import com.android305.moviesearcher.network.endpoints.OMDBEndpoint;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.integration.recyclerview.RecyclerViewPreloader;
import com.bumptech.glide.util.ViewPreloadSizeProvider;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@SuppressLint("Registered")
@EActivity(R.layout.activity_main)
public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final String SAVED_QUERY = "SAVED_QUERY";

    @ViewById(R.id.start_searching)
    TextView startSearchingView;

    @ViewById(R.id.movie_recycler_view)
    RecyclerView recyclerView;

    @ViewById(R.id.search_progress)
    ProgressBar searchProgress;

    private String mQuery;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mQuery = savedInstanceState.getString(SAVED_QUERY);
        }
    }

    @AfterViews
    void preInit() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        assert actionBar != null;
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        startSearchingView.setVisibility(View.VISIBLE);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            // Check if an outside source requested a search
            mQuery = intent.getStringExtra(SearchManager.QUERY);
            search(mQuery);
        } else if (mQuery != null) {
            // use the saved query value if one exists
            search(mQuery);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(SAVED_QUERY, mQuery);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        assert searchManager != null;
        MenuItem searchViewAction = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false);

        // We are not sending this to another activity so let's handle it here.
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                if (!searchView.isIconified()) {
                    searchView.setIconified(true);
                }
                searchViewAction.collapseActionView();
                search(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
        return true;
    }

    @Background
    void search(String query) {
        mQuery = query;
        setProgressVisibility(View.VISIBLE);
        Endpoint.ResultSet resultSet = null;
        try {
            resultSet = Connection.sendRequest(new OMDBEndpoint(query));
            if (resultSet != null) {
                Log.v(TAG, resultSet.toString());
            } else {
                Log.v(TAG, "Result set null");
            }
        } catch (ConnectException e) {
            Log.e(TAG, "connect exception", e);

            // TODO: Handle network failure with more grace
        }
        setAdapter(resultSet);
    }

    @UiThread(propagation = UiThread.Propagation.REUSE)
    void setProgressVisibility(int vis) {
        searchProgress.setVisibility(vis);
    }

    @UiThread
    void setAdapter(@Nullable Endpoint.ResultSet resultSet) {
        if (resultSet != null) {
            RequestBuilder<Drawable> glideRequest = Glide.with(this).asDrawable();
            ViewPreloadSizeProvider<Endpoint.ResultSet.Result> preloadSizeProvider = new ViewPreloadSizeProvider<>();

            SearchResultAdapter mSearchResultAdapter = new SearchResultAdapter(resultSet, glideRequest, preloadSizeProvider, result -> {
                // We can assume the imdb id is not null here because we hide the button if it is
                String url = "https://www.imdb.com/title/" + result.getImdbId();
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            });
            recyclerView.setAdapter(mSearchResultAdapter);

            RecyclerViewPreloader<Endpoint.ResultSet.Result> preloader = new RecyclerViewPreloader<>(this, mSearchResultAdapter, preloadSizeProvider, 4);

            recyclerView.addOnScrollListener(preloader);
            recyclerView.setRecyclerListener(holder -> {
                SearchResultAdapter.SearchResultViewHolder vh = (SearchResultAdapter.SearchResultViewHolder) holder;
                Glide.with(this).clear(vh.poster);
            });
        }
        if (resultSet == null || resultSet.getResults() == null || resultSet.getResults().size() == 0) {
            startSearchingView.setVisibility(View.VISIBLE);
            startSearchingView.setText(R.string.no_results_found);
        } else {
            startSearchingView.setVisibility(View.GONE);
        }
        searchProgress.setVisibility(View.GONE);
    }

}
