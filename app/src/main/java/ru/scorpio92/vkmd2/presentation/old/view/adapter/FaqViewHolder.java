package ru.scorpio92.vkmd2.presentation.old.view.adapter;

import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import ru.scorpio92.vkmd2.R;


class FaqViewHolder extends RecyclerView.ViewHolder {

    AppCompatTextView question, answer;

    FaqViewHolder(View itemView) {
        super(itemView);
        question = itemView.findViewById(R.id.question);
        answer = itemView.findViewById(R.id.answer);
    }
}
