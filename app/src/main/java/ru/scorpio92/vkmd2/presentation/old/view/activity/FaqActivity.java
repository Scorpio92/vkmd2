package ru.scorpio92.vkmd2.presentation.old.view.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;
import java.util.List;

import ru.scorpio92.vkmd2.R;
import ru.scorpio92.vkmd2.data.entity.FaqItem;
import ru.scorpio92.vkmd2.presentation.old.view.adapter.FaqListAdapter;


public class FaqActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faq);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            final ActionBar ab = getSupportActionBar();
            ab.setDisplayShowHomeEnabled(true);
            ab.setDisplayHomeAsUpEnabled(true);
            ab.setDisplayShowCustomEnabled(true);
            ab.setDisplayShowTitleEnabled(false);
        }

        List<FaqItem> faqItems = new ArrayList<>();
        faqItems.add(new FaqItem(getString(R.string.q1), getString(R.string.a1)));
        faqItems.add(new FaqItem(getString(R.string.q2), getString(R.string.a2)));
        faqItems.add(new FaqItem(getString(R.string.q3), getString(R.string.a3)));
        faqItems.add(new FaqItem(getString(R.string.q4), getString(R.string.a4)));
        faqItems.add(new FaqItem(getString(R.string.q5), getString(R.string.a5)));

        RecyclerView list = findViewById(R.id.list);
        list.setLayoutManager(new LinearLayoutManager(this));
        list.setLayoutAnimation(AnimationUtils.loadLayoutAnimation(this, R.anim.layout_animation_slide_right));
        list.setAdapter(new FaqListAdapter(this, faqItems));
    }
}
