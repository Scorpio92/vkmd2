package ru.scorpio92.vkmd2.presentation.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.FaqItem;


public class FaqListAdapter extends RecyclerView.Adapter<FaqViewHolder> {

    private Context context;
    private List<FaqItem> faqItems;

    private Animation animationFadeIn, animationFadeOut;

    public FaqListAdapter(Context context, List<FaqItem> faqItems) {
        this.context = context;
        this.faqItems = faqItems;

        animationFadeIn = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animationFadeOut = AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
    }

    @Override
    public FaqViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FaqViewHolder(LayoutInflater.from(context).inflate(R.layout.activity_faq_list_row, parent, false));
    }

    @Override
    public void onBindViewHolder(FaqViewHolder holder, int position) {
        FaqItem faqItem = faqItems.get(position);

        holder.question.setText(faqItem.getQuestion());
        holder.question.setOnClickListener(v -> hideShow(holder.answer));
        holder.answer.setText(faqItem.getAnswer());
    }

    @Override
    public int getItemCount() {
        return faqItems.size();
    }

    private void hideShow(View view) {
        if(view.getVisibility() == View.GONE)
            slideToBottom(view);
        else
            slideToTop(view);
    }

    private void slideToBottom(View view){
        view.startAnimation(animationFadeIn);
        view.setVisibility(View.VISIBLE);
    }

    private void slideToTop(View view){
        view.startAnimation(animationFadeOut);
        view.setVisibility(View.GONE);
    }
}
