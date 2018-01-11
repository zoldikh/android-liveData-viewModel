package com.thomaskioko.livedatademo.view.ui.fragment;

import android.arch.lifecycle.LifecycleFragment;
import android.arch.lifecycle.ViewModelProvider;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.thomaskioko.livedatademo.R;
import com.thomaskioko.livedatademo.di.Injectable;
import com.thomaskioko.livedatademo.repository.api.MovieResult;
import com.thomaskioko.livedatademo.repository.model.ApiResponse;
import com.thomaskioko.livedatademo.repository.model.Movie;
import com.thomaskioko.livedatademo.view.adapter.MovieListAdapter;
import com.thomaskioko.livedatademo.viewmodel.MovieListViewModel;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * @author Thomas Kioko
 */

public class MovieListFragment extends LifecycleFragment implements Injectable{

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    RecyclerView mRecyclerView;
    ProgressBar progressBar;
    MovieListAdapter mMovieListAdapter;
    private List<Movie> mMovieList = new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.movie_list_fragment, container, false);
        mRecyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.progressbar);

        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        GridLayoutManager gridLayoutManager = new GridLayoutManager(mRecyclerView.getContext(), 3);
        mRecyclerView.setLayoutManager(gridLayoutManager);

        mMovieListAdapter = new MovieListAdapter(mMovieList);
        mRecyclerView.setAdapter(mMovieListAdapter);



        progressBar.setVisibility(View.VISIBLE);
        ViewModelProviders.of(this, viewModelFactory)
                .get(MovieListViewModel.class)
                .getPopularMovies()
                .observe(this, apiResponse -> {
                    progressBar.setVisibility(View.GONE);
                    handleApiResponse(apiResponse);
                });
    }

    /**
     * Helper method that handles responses from, the API.It's responsible for displaying either
     * an error message of a list of movies based on the reponse from the server.
     *
     * @param apiResponse {@link ApiResponse}
     */
    private void handleApiResponse(ApiResponse apiResponse) {

        if (apiResponse.getStatusCode() != 200) {
            Timber.e("API Error: ");
        } else if (apiResponse.getError() != null) {
            Timber.e("Error: " + apiResponse.getError().getMessage());
        } else {
            MovieResult movieResult = apiResponse.getMovieResult();

            mMovieList.addAll(movieResult.getResults());
            mMovieListAdapter.notifyDataSetChanged();
        }

    }

}
