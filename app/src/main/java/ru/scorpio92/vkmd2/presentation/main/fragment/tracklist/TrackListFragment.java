package ru.scorpio92.vkmd2.presentation.main.fragment.tracklist;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.animation.AnimationUtils;

import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.di.PresenterInjection;
import ru.scorpio92.vkmd2.presentation.base.BaseFragment;
import ru.scorpio92.vkmd2.presentation.entity.UiTrack;
import ru.scorpio92.vkmd2.presentation.main.activity.MainActivity;
import ru.scorpio92.vkmd2.presentation.main.fragment.tracklist.adapter.SpacesItemDecoration;
import ru.scorpio92.vkmd2.presentation.main.fragment.tracklist.adapter.TrackListAdapter;

public class TrackListFragment extends BaseFragment<IContract.Presenter> implements IContract.View {

    private SwipeRefreshLayout srl;
    private TrackListAdapter mTrackListAdapter;
    private RecyclerView trackListView;
    private AppCompatTextView error;

    @Nullable
    @Override
    protected IContract.Presenter bindPresenter() {
        return PresenterInjection.provideTrackListPresenter(this);
    }

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.fragment_tracklist;
    }

    @Override
    protected void initUI(@NonNull View view) {
        srl = view.findViewById(R.id.srl);
        srl.setOnRefreshListener(refreshListener);

        mTrackListAdapter = new TrackListAdapter(getViewContext(), adapterListener);
        trackListView = view.findViewById(R.id.trackList);
        trackListView.addItemDecoration(new SpacesItemDecoration(0));
        trackListView.setLayoutManager(new LinearLayoutManager(getViewContext()));
        trackListView.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(getViewContext(), R.anim.layout_animation_slide_right));
        trackListView.setAdapter(mTrackListAdapter);

        error = view.findViewById(R.id.error);
    }

    @Override
    public void showProgress() {
        srl.setRefreshing(true);
    }

    @Override
    public void hideProgress() {
        srl.setRefreshing(false);
    }

    @Override
    public void onError(@NonNull String errorText) {
        trackListView.setVisibility(View.GONE);
        error.setVisibility(View.VISIBLE);
        error.setText(errorText);

        if(checkFragmentListener()) {
            getFragmentListener().onFragmentResult(
                    MainActivity.FragmentResult.TRACK_LIST_LOADING_ERROR, null);
        }
    }

    @Override
    public void clearTrackList() {
        mTrackListAdapter.clearList();
    }

    @Override
    public void renderTrackList(List<UiTrack> trackList) {
        if(checkFragmentListener()) {
            getFragmentListener().onFragmentResult(
                    MainActivity.FragmentResult.TRACK_LIST_UPDATE, trackList.get(0));
        }
        trackListView.setVisibility(View.VISIBLE);
        error.setVisibility(View.GONE);
        mTrackListAdapter.render(trackList);
    }

    private SwipeRefreshLayout.OnRefreshListener refreshListener = () -> {
        if(checkPresenterState()) {
            getPresenter().getTrackList();
        }
    };

    private TrackListAdapter.Listener adapterListener = new TrackListAdapter.Listener() {
        @Override
        public void onTrackClick(UiTrack track) {

        }

        @Override
        public void onCheckChanged() {

        }
    };
}
