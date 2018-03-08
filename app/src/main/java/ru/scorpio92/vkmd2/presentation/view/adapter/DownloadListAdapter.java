package ru.scorpio92.vkmd2.presentation.view.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.Track;

public class DownloadListAdapter extends RecyclerView.Adapter<DownloadListViewHolder> {

    private List<Track> trackList;
    private Listener listener;

    private List<String> selectedTracksId;

    public DownloadListAdapter(List<Track> trackList, Listener listener) {
        this.trackList = trackList;
        this.listener = listener;
        this.selectedTracksId = new ArrayList<>();
    }

    @Override
    public DownloadListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DownloadListViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.download_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(DownloadListViewHolder holder, int position) {
        Track track = trackList.get(position);

        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!selectedTracksId.contains(track.getTrackId()))
                    selectedTracksId.add(track.getTrackId());
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                selectedTracksId.remove(track.getTrackId());
                holder.checkBox.setVisibility(View.GONE);
            }

            if (listener != null)
                listener.onCheckChanged();
        });

        holder.checkBox.setChecked(selectedTracksId.contains(track.getTrackId()));

        holder.trackName.setText(track.getName());

        holder.trackArtist.setText(track.getArtist());

        if(track.isDownloadError()) {
            holder.error.setVisibility(View.VISIBLE);
            holder.progressContainer.setVisibility(View.GONE);
        } else {
            holder.error.setVisibility(View.GONE);
            holder.progressContainer.setVisibility(View.VISIBLE);
            holder.downloadProgress.setMax(100);
            holder.downloadProgress.setProgress(track.getDownloadProgress());
            holder.progressText.setText(String.format("%s%%", String.valueOf(track.getDownloadProgress())));
        }

        holder.cardView.setOnLongClickListener(v -> {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    public void renderDownloadList(List<Track> trackList) {
        this.trackList = new ArrayList<>(trackList);
        notifyDataSetChanged();
    }

    public void removeSelectedTracksFromList() {
        for (String trackId : selectedTracksId) {
            for (Track track : trackList) {
                if (track.getTrackId().equals(trackId)) {
                    trackList.remove(track);
                    break;
                }
            }
        }
        notifyDataSetChanged();
    }

    public void markSelectedTracksForRedownload() {
        for (String trackId : selectedTracksId) {
            for (Track track : trackList) {
                if (track.getTrackId().equals(trackId)) {
                    track.setDownloadError(false);
                    break;
                }
            }
        }
        //notifyDataSetChanged();
    }

    public void onTrackDownloaded(String trackId) {
        for (Track track : trackList) {
            if(track.getTrackId().equals(trackId)) {
                trackList.remove(track);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void onTrackDownloadError(String trackId) {
        for (Track track : trackList) {
            if(track.getTrackId().equals(trackId)) {
                track.setDownloadError(true);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void onTrackDownloadProgress(String trackId, int downloadProgress) {
        for (Track track : trackList) {
            if(track.getTrackId().equals(trackId)) {
                track.setDownloadError(false);
                track.setDownloadProgress(downloadProgress);
                break;
            }
        }
        notifyDataSetChanged();
    }

    public void selectAll() {
        selectedTracksId.clear();
        for (Track track : trackList) {
            selectedTracksId.add(track.getTrackId());
        }
        notifyDataSetChanged();
    }

    public void uncheckAll() {
        selectedTracksId.clear();
        notifyDataSetChanged();
    }

    public List<String> getSelectedTracks() {
        return new ArrayList<>(selectedTracksId);
    }

    public interface Listener {
        void onCheckChanged();
    }
}
