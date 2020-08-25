package com.example.pocketclash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import java.util.ArrayList;
import java.util.Objects;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * Views
     */
    ImageView title;
    ImageView startButton;
    ImageView top10Button;
    ImageView aboutButton;
    TextView quitText;
    RelativeLayout relativeLayout;
    Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initViews();
        initListeners();
    }

    /**
     * Gametype 0 = solo
     * Gametype 1 = vs AI
     */
    @SuppressLint("ClickableViewAccessibility")
    private void initListeners() {

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GameModeActivity dialog = new GameModeActivity(WelcomeActivity.this);
                createDialogFragment(dialog, 0);
                Objects.requireNonNull(dialog.getWindow()).setDimAmount(0.975f);
            }
        });
        top10Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                Intent intent = new Intent(WelcomeActivity.this, TopTenActivity.class);
                startActivity(intent);
            }
        });
        quitText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AboutActivity aboutActivity = new AboutActivity(view.getContext());
                createDialogFragment(aboutActivity, 1);
            }
        });
    }

    /**
     * A method to initialize the widgets
     */
    private void initViews() {
        relativeLayout = findViewById(R.id.welcome_LAY_mainLayout);
        title = findViewById(R.id.welcome_IMG_title);
        top10Button = findViewById(R.id.welcome_IMG_top10);
        startButton = findViewById(R.id.welcome_IMG_start);
        aboutButton = findViewById(R.id.welcome_IMG_about);
        quitText = findViewById(R.id.welcome_LBL_quit);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Glide.with(this).load(R.drawable.pocket_clash2).into(title);
        Glide.with(this).load(R.drawable.start_button).into(startButton);
        Glide.with(this).load(R.drawable.top10button3).into(top10Button);
        Glide.with(aboutButton).load(R.drawable.about_button2).into(aboutButton);
        Glide.with(relativeLayout).load(R.drawable.background_welcome2).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                relativeLayout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });

    }

    /**
     * A method to create and show given dialog fragment
     */
    private void createDialogFragment(Dialog dialog, int var) { // 0 = start , 1 = about
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        int width, height;
        if (var == 0) {
            width = getResources().getDisplayMetrics().widthPixels;
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.3);
        } else {
            width = (int) (getResources().getDisplayMetrics().widthPixels * 0.55);
            height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
        }
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
    }
}