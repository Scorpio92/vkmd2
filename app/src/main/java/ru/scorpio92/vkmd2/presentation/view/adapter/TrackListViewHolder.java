package ru.scorpio92.vkmd2.presentation.view.adapter;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import ru.scorpio92.vkmd2.R;


class TrackListViewHolder extends RecyclerView.ViewHolder {

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
