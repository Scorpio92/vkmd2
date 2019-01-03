package ru.scorpio92.vkmd2.presentation.main.fragment.player;

import android.graphics.PorterDuff;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.support.v7.widget.AppCompatTextView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.di.PresenterInjection;
import ru.scorpio92.vkmd2.domain.entity.Track;
import ru.scorpio92.vkmd2.presentation.base.BaseFragment;
import ru.scorpio92.vkmd2.tools.DateUtils;
import ru.scorpio92.vkmd2.tools.ViewUtils;

public class PlayerFragment extends BaseFragment<IContract.Presenter> implements IContract.View {

    private AppCompatTextView trackName, artist, currentTime, durationTime;
    private ProgressBar progress;
    private ImageView trackImage;
    private AppCompatSeekBar playProgressIndicator;
    private ImageButton loop;
    private ImageButton play;
    private ImageButton random;

    private int selectedColor;
    private int unselectedColor;

    @Nullable
    @Override
    protected IContract.Presenter bindPresenter() {
        return PresenterInjection.providePlayerPresenter(this);
    }

    @Nullable
    @Override
    protected Integer bindLayout() {
        return R.layout.fragment_player;
    }

    @Override
    protected void initUI(@NonNull View view) {
        trackName = view.findViewById(R.id.trackName);
        artist = view.findViewById(R.id.artist);

        currentTime = view.findViewById(R.id.currentTime);
        durationTime = view.findViewById(R.id.durationTime);

        progress = view.findViewById(R.id.progress);
        progress.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getContext(), R.color.colorPrimaryDark),
                PorterDuff.Mode.MULTIPLY);

        trackImage = view.findViewById(R.id.image);

        playProgressIndicator = view.findViewById(R.id.playProgressIndicator);

        loop = view.findViewById(R.id.loop);
        loop.setOnClickListener(v -> {
            if (checkPresenterState()) {
                getPresenter().onLoopPressed();
            }
        });

        ImageButton prev = view.findViewById(R.id.prev);
        prev.setOnClickListener(v -> {
            if (checkPresenterState()) {
                getPresenter().previous();
            }
        });

        play = view.findViewById(R.id.play);
        play.setOnClickListener(v -> {
            if (checkPresenterState()) {
                getPresenter().playOrPause();
            }
        });

        ImageButton next = view.findViewById(R.id.next);
        next.setOnClickListener(v -> {
            if (checkPresenterState()) {
                getPresenter().next();
            }
        });

        random = view.findViewById(R.id.random);
        random.setOnClickListener(v -> {
            if (checkPresenterState()) {
                getPresenter().onRandomPressed();
            }
        });

        selectedColor = getResources().getColor(R.color.colorPrimaryDark);
        unselectedColor = getResources().getColor(android.R.color.black);
    }

    @Override
    public void onTrackLoading() {
        trackImage.setVisibility(View.GONE);
        progress.setVisibility(View.VISIBLE);
    }

    @Override
    public void onTrackLoadingComplete() {
        setDefaultTrackArt();
        trackImage.setVisibility(View.VISIBLE);
        progress.setVisibility(View.GONE);
    }

    @Override
    public void onPlay() {
        play.setImageResource(R.mipmap.play);
    }

    @Override
    public void onPauseTrack() {
        play.setImageResource(R.mipmap.pause);
    }

    @Override
    public void onTrackRefresh(@NonNull Track track) {
        trackName.setText(track.getName());
        artist.setText(track.getArtist());

        loadTrackArtAsync(track.getUrlImage());

        playProgressIndicator.setProgress(0);
        playProgressIndicator.setMax(track.getDuration() * 1000);

        currentTime.setText(DateUtils.getHumanTimeFromMilliseconds(0));
        durationTime.setText(DateUtils.getHumanTimeFromMilliseconds(track.getDuration() * 1000));

        onPlay();
    }

    @Override
    public void onLoopEnabled(boolean enabled) {
        loop.setColorFilter(enabled ? selectedColor : unselectedColor);
    }

    @Override
    public void onRandomEnabled(boolean enabled) {
        random.setColorFilter(enabled ? selectedColor : unselectedColor);
    }

    @Override
    public void showToast(@NonNull String error) {
        ViewUtils.showToast(getContext(), error);
    }

    private void setDefaultTrackArt() {
        trackImage.setScaleType(ImageView.ScaleType.CENTER);
        trackImage.setImageResource(R.mipmap.note);
    }

    private void loadTrackArtAsync(String imageUrl) {
        if (getContext() != null && imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.with(getContext())
                    .load(imageUrl)
                    .into(trackImage, new Callback() {
                        @Override
                        public void onSuccess() {
                            trackImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        }

                        @Override
                        public void onError() {
                            setDefaultTrackArt();
                        }
                    });
        } else {
            setDefaultTrackArt();
        }
    }
}
