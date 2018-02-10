package com.pimenta.bestv.fragments;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v17.leanback.app.BackgroundManager;
import android.support.v17.leanback.app.BrowseFragment;
import android.support.v17.leanback.widget.ArrayObjectAdapter;
import android.support.v17.leanback.widget.FocusHighlight;
import android.support.v17.leanback.widget.OnItemViewClickedListener;
import android.support.v17.leanback.widget.OnItemViewSelectedListener;
import android.support.v17.leanback.widget.Presenter;
import android.support.v17.leanback.widget.Row;
import android.support.v17.leanback.widget.RowPresenter;
import android.support.v17.leanback.widget.VerticalGridPresenter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pimenta.bestv.fragments.bases.BaseVerticalGridFragment;
import com.pimenta.bestv.models.Genre;
import com.pimenta.bestv.models.Movie;
import com.pimenta.bestv.presenters.MovieGridCallback;
import com.pimenta.bestv.presenters.MovieGridPresenter;
import com.pimenta.bestv.widget.MovieCardPresenter;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by marcus on 09-02-2018.
 */
public class MovieGridFragment extends BaseVerticalGridFragment<MovieGridPresenter> implements MovieGridCallback, BrowseFragment.MainFragmentAdapterProvider {

    private static final String TAG = "MovieGridFragment";
    private static final String GENRE = "GENRE";

    private static final int BACKGROUND_UPDATE_DELAY = 300;
    private static final int NUMBER_COLUMNS = 4;

    private final Handler mHandler = new Handler();
    private ArrayObjectAdapter mRowsAdapter;
    private final VerticalGridPresenter mVerticalGridPresenter = new VerticalGridPresenter(FocusHighlight.ZOOM_FACTOR_MEDIUM);
    private final BrowseFragment.MainFragmentAdapter<MovieGridFragment> mMainFragmentAdapter = new BrowseFragment.MainFragmentAdapter<>(this);

    private Timer mBackgroundTimer;
    private BackgroundManager mBackgroundManager;

    private Genre mGenre;

    public static MovieGridFragment newInstance(Genre genre) {
        Bundle args = new Bundle();
        args.putSerializable(GENRE, genre);

        MovieGridFragment movieGridFragment = new MovieGridFragment();
        movieGridFragment.setArguments(args);
        movieGridFragment.mGenre = genre;
        return movieGridFragment;
    }

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (mGenre == null) {
            mGenre = (Genre) getArguments().getSerializable(GENRE);
        }

        setupUI();
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, final Bundle savedInstanceState) {
        getProgressBarManager().setRootView(container);
        getProgressBarManager().enableProgressBar();
        getProgressBarManager().setInitialDelay(0);
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getMainFragmentAdapter().getFragmentHost().notifyViewCreated(getMainFragmentAdapter());

        getProgressBarManager().show();
        mPresenter.loadMoviesByGenre(mGenre);
    }

    @Override
    public void onDestroy() {
        getProgressBarManager().hide();
        if (mBackgroundTimer != null) {
            mBackgroundTimer.cancel();
        }
        super.onDestroy();
    }

    @Override
    protected MovieGridPresenter getPresenter() {
        return new MovieGridPresenter();
    }

    @Override
    public BrowseFragment.MainFragmentAdapter getMainFragmentAdapter() {
        return mMainFragmentAdapter;
    }

    @Override
    public void onMoviesLoaded(final List<Movie> movies) {
        for (final Movie movie : movies) {
            mRowsAdapter.add(movie);
        }

        getProgressBarManager().hide();
        getMainFragmentAdapter().getFragmentHost().notifyDataReady(getMainFragmentAdapter());
    }

    @Override
    public void onPosterImageLoaded(final Drawable drawable) {
        mBackgroundManager.setDrawable(drawable);
    }

    private void setupUI() {
        mBackgroundManager = BackgroundManager.getInstance(getActivity());

        mVerticalGridPresenter.setNumberOfColumns(NUMBER_COLUMNS);
        setGridPresenter(mVerticalGridPresenter);

        mRowsAdapter = new ArrayObjectAdapter(new MovieCardPresenter());
        setAdapter(mRowsAdapter);

        setOnItemViewSelectedListener(new ItemViewSelectedListener());
        setOnItemViewClickedListener(new ItemViewClickedListener());
    }

    private final class ItemViewSelectedListener implements OnItemViewSelectedListener {

        @Override
        public void onItemSelected(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            final Movie movie = (Movie) item;
            if (mBackgroundTimer != null) {
                mBackgroundTimer.cancel();
            }
            mBackgroundTimer = new Timer();
            mBackgroundTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.post(() -> {
                        mPresenter.loadPosterImage(movie);
                        mBackgroundTimer.cancel();
                    });
                }
            }, BACKGROUND_UPDATE_DELAY);
        }
    }

    private final class ItemViewClickedListener implements OnItemViewClickedListener {

        @Override
        public void onItemClicked(Presenter.ViewHolder itemViewHolder, Object item, RowPresenter.ViewHolder rowViewHolder, Row row) {
            /*if (item instanceof Movie) {
                Movie movie = (Movie) item;
                Log.d(TAG, "Item: " + item.toString());
                Intent intent = new Intent(getActivity(), MovieDetailsActivity.class);
                intent.putExtra(MovieDetailsFragment.MOVIE, movie);

                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        ((ImageCardView) itemViewHolder.view).getMainImageView(),
                        MovieDetailsFragment.SHARED_ELEMENT_NAME).toBundle();
                getActivity().startActivity(intent, bundle);
            }*/
        }
    }
}