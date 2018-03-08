package ru.scorpio92.vkmd2.presentation.view.adapter;

import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import ru.scorpio92.vkmd2.R;


class DownloadListViewHolder extends RecyclerView.ViewHolder {

    AppCompatCheckBox checkBox;
    CardView cardView;
    AppCompatTextView trackName, trackArtist, error, progressText;
    LinearLayoutCompat progressContainer;
    ProgressBar downloadProgress;

    DownloadListViewHolder(View itemView) {
        super(itemView);
        checkBox = itemView.findViewById(R.id.checkbox);
        cardView = itemView.findViewById(R.id.cv);
        trackName = itemView.findViewById(R.id.trackName);
        trackArtist = itemView.findViewById(R.id.trackArtist);
        error = itemView.findViewById(R.id.error);
        progressContainer = itemView.findViewById(R.id.progressContainer);
        progressText = itemView.findViewById(R.id.progressText);
        downloadProgress = itemView.findViewById(R.id.downloadProgress);
    }
}
