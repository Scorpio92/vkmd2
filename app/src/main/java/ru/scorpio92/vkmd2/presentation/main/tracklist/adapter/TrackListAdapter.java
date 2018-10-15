package ru.scorpio92.vkmd2.presentation.main.tracklist.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.presentation.entity.UiTrack;
import ru.scorpio92.vkmd2.tools.DateUtils;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListAdapter.TrackListViewHolder> {

    private Context mContext;
    private Listener mListener;
    private List<UiTrack> mTrackList;
    private String mNowPlayingTrackId;

    public TrackListAdapter(Context context, Listener listener) {
        this.mContext = context;
        this.mListener = listener;
        this.mTrackList = new ArrayList<>();
    }

    @NonNull
    @Override
    public TrackListViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new TrackListViewHolder(LayoutInflater.from(mContext).inflate(R.layout.track_list_row, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrackListViewHolder holder, int position) {
        final UiTrack track = mTrackList.get(position);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            track.setSelected(isChecked);
            holder.checkBox.setVisibility(isChecked ? View.VISIBLE : View.GONE);

            if (mListener != null)
                mListener.onCheckChanged();
        });

        holder.checkBox.setChecked(track.isSelected());

        holder.trackName.setText(track.getName());

        holder.trackArtist.setText(track.getArtist());

        if (track.getTrackId().equals(mNowPlayingTrackId)) {
            holder.trackArtist.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
            holder.trackName.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimaryDark));
        } else {
            holder.trackArtist.setTextColor(ContextCompat.getColor(mContext, R.color.colorFont));
            holder.trackName.setTextColor(ContextCompat.getColor(mContext, R.color.colorFont));
        }

        holder.trackDuration.setText(DateUtils.getHumanTimeFromMilliseconds(track.getDuration() * 1000));

        holder.saved.setVisibility(track.isSaved() ? View.VISIBLE : View.GONE);

        holder.cardView.setOnClickListener(v -> {
            if (mListener != null)
                mListener.onTrackClick(track);
        });

        holder.cardView.setOnLongClickListener(v -> {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return mTrackList.size();
    }

    static class TrackListViewHolder extends RecyclerView.ViewHolder {

        AppCompatCheckBox checkBox;
        CardView cardView;
        AppCompatTextView trackName, trackArtist, trackDuration;
        ImageView saved;

        TrackListViewHolder(View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkbox);
            cardView = itemView.findViewById(R.id.cv);
            trackName = itemView.findViewById(R.id.trackName);
            trackArtist = itemView.findViewById(R.id.trackArtist);
            trackDuration = itemView.findViewById(R.id.trackDuration);
            saved = itemView.findViewById(R.id.saved);
        }
    }

    public interface Listener {
        void onTrackClick(UiTrack track);

        void onCheckChanged();
    }

    public void render(List<UiTrack> trackList) {
        this.mTrackList.addAll(trackList);
        notifyDataSetChanged();
    }

    public void renderNowPayingTrack(String nowPlayingTrackId) {
        this.mNowPlayingTrackId = nowPlayingTrackId;
        notifyDataSetChanged();
    }

    public void clearList() {
        this.mTrackList.clear();
        notifyDataSetChanged();
    }
}
