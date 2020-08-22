package com.example.pocketclash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class QuitActivity extends Dialog implements View.OnClickListener {

    private TextView yesButton;
    private TextView noButton;
    private TextView menuButton;
    private ImageView background;
    private RelativeLayout mainLayout;

    private Activity myActivity;


    public QuitActivity(Activity activity) {
        super(activity);
        this.myActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quit);
        initViews();
//        this.setCancelable(false);
        super.onCreate(savedInstanceState);
    }

    /**
     * A method to init widgets
     */
    private void initViews() {
        mainLayout = findViewById(R.id.quitDialog_LAY_mainLayout);
        yesButton = findViewById(R.id.quitDialog_TXT_yesText);
        yesButton.setOnClickListener(this);
        noButton = findViewById(R.id.quitDialog_TXT_noText);
        noButton.setOnClickListener(this);
        menuButton = findViewById(R.id.quitDialog_TXT_menuText);
        menuButton.setOnClickListener(this);
        background = findViewById(R.id.quitDialog_IMG_background);
        Glide.with(background).load(R.drawable.dialog_window).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mainLayout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quitDialog_TXT_yesText:
                dismiss();
                System.exit(0);
                break;
            case R.id.quitDialog_TXT_noText:
                dismiss();
                break;
            case R.id.quitDialog_TXT_menuText:
                dismiss();
                Intent intent = new Intent(view.getContext(), WelcomeActivity.class);
                myActivity.startActivity(intent);
                myActivity.finish();
        }
    }
}
