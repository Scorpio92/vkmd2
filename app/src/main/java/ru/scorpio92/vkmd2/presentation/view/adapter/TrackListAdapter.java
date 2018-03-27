package ru.scorpio92.vkmd2.presentation.view.adapter;


import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.Track;

public class TrackListAdapter extends RecyclerView.Adapter<TrackListViewHolder> implements Filterable {

    private Context context;
    private List<Track> trackList;
    private Listener listener;

    private List<String> selectedTracksId;

    private ValueFilter valueFilter;
    private List<Track> trackListTmp;
    private boolean filterByArtist;
    private boolean filterByTrackName;
    private boolean onlineSearch;

    public TrackListAdapter(Context context, List<Track> trackList, Listener listener) {
        this.context = context;
        this.trackList = trackList;
        this.trackListTmp = trackList;
        this.listener = listener;
        this.selectedTracksId = new ArrayList<>();
        this.filterByArtist = true;
        this.filterByTrackName = true;
        setHasStableIds(true);
    }

    @Override
    public TrackListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new TrackListViewHolder(LayoutInflater.from(context).inflate(R.layout.track_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(TrackListViewHolder holder, int position) {
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

        if (track.isPlaying()) {
            holder.trackArtist.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
            holder.trackName.setTextColor(ContextCompat.getColor(context, R.color.colorPrimaryDark));
        } else {
            holder.trackArtist.setTextColor(ContextCompat.getColor(context, R.color.colorFont));
            holder.trackName.setTextColor(ContextCompat.getColor(context, R.color.colorFont));
        }

        holder.saved.setVisibility(track.isSaved() ? View.VISIBLE : View.GONE);

        holder.cardView.setOnClickListener(v -> {
            if (listener != null)
                listener.onTrackClick(track.getTrackId(), track.getArtist(), track.getName(), track.getDuration(), track.getUrlImage());
        });

        holder.cardView.setOnLongClickListener(v -> {
            holder.checkBox.setVisibility(View.VISIBLE);
            holder.checkBox.setChecked(true);
            return true;
        });

        /*new SwipeDetector(holder.cardView).setOnSwipeListener((v, SwipeType) -> {
            Logger.log("SWIPE DETECTED: " + SwipeType.name());
            switch (SwipeType) {
                case LEFT_TO_RIGHT:
                    holder.checkBox.setVisibility(View.VISIBLE);
                    break;
                case RIGHT_TO_LEFT:
                    holder.checkBox.setVisibility(View.GONE);
                    break;
            }
        });*/
    }

    @Override
    public int getItemCount() {
        return trackList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public Filter getFilter() {
        if (valueFilter == null) {
            valueFilter = new ValueFilter();
        }
        return valueFilter;
    }

    public void filter(CharSequence value) {
        String stringValue = value.toString().trim();
        if (!stringValue.isEmpty())
            getFilter().filter(value);
    }

    public void resetFilter() {
        renderTrackList(trackListTmp);
    }

    public void setFilterByArtist(boolean filterByArtist) {
        this.filterByArtist = filterByArtist;
    }

    public void setFilterByTrackName(boolean filterByTrackName) {
        this.filterByTrackName = filterByTrackName;
    }

    public void setOnlineSearch(boolean onlineSearch) {
        this.onlineSearch = onlineSearch;
    }

    public void renderTrackList(List<Track> trackList) {
        renderTrackList(trackList, true);
    }

    public void renderTrackList(List<Track> trackList, boolean refreshList) {
        this.trackList = new ArrayList<>(trackList);
        this.trackListTmp = new ArrayList<>(trackList);
        if (refreshList)
            notifyDataSetChanged();
    }

    public void renderTrack(String trackId) {
        for (Track track : trackList) {
            if (track.getTrackId().equals(trackId)) {
                track.setSaved(true);
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

    public boolean checkSelectedTracksIsEmpty() {
        return selectedTracksId.isEmpty();
    }

    public void showCurrentTrackIsPlayed(String trackId) {
        for (Track track : trackList) {
            track.setPlaying(track.getTrackId().equals(trackId));
        }
        notifyDataSetChanged();
    }

    public void showNoneTrackIsPlayed() {
        for (Track track : trackList) {
            track.setPlaying(false);
        }
        notifyDataSetChanged();
    }

    public interface Listener {
        void onTrackClick(String trackId, String artist, String name, int duration, String imageUrl);

        void onCheckChanged();

        void onFilterComplete(List<String> tracksId);
    }

    private class ValueFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();

            if (constraint != null && constraint.length() > 0) {
                List<Track> filterList = new ArrayList<>();
                for (int i = 0; i < trackListTmp.size(); i++) {
                    if ((filterByArtist && filterByTrackName) || (!filterByArtist && !filterByTrackName)) {
                        if ((trackListTmp.get(i).getArtist().toUpperCase()).contains(constraint.toString().toUpperCase()) ||
                                (trackListTmp.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(trackListTmp.get(i));
                        }
                    } else if (filterByArtist) {
                        if ((trackListTmp.get(i).getArtist().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(trackListTmp.get(i));
                        }
                    } else if (filterByTrackName) {
                        if ((trackListTmp.get(i).getName().toUpperCase()).contains(constraint.toString().toUpperCase())) {
                            filterList.add(trackListTmp.get(i));
                        }
                    }
                }
                results.count = filterList.size();
                results.values = filterList;
            } else {
                results.count = trackListTmp.size();
                results.values = trackListTmp;
            }
            return results;

        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            trackList = (List<Track>) results.values;
            if(listener != null)
                listener.onFilterComplete(getTracksId());
            notifyDataSetChanged();
        }
    }

    public List<String> getTracksId() {
        List<String> list = new ArrayList<>();
        for (Track track : trackList) {
            list.add(track.getTrackId());
        }

        return list;
    }
}
