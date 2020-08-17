package com.example.pocketclash;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * Widgets
     */
    ImageView title;
    ImageView soloStartButton;
    ImageView vsaiStartButton;
    RelativeLayout relativeLayout;
    Vibrator vb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initWidgets();
        initListeners();


    }

    /**
     * Gametype 0 = solo
     * Gametype 1 = vs AI
     */
    private void initListeners() {
        soloStartButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                vb.vibrate(5);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("GameType", 0);
                startActivity(intent);
                finish();
                return false;
            }
        });
        vsaiStartButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                vb.vibrate(5);
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                intent.putExtra("GameType", 1);
                startActivity(intent);
                finish();
                return false;
            }
        });
    }

    /**
     * A method to initialize the widgets
     */
    private void initWidgets() {
        relativeLayout = findViewById(R.id.welcome_LAY_mainLayout);
        title = findViewById(R.id.welcome_IMG_title);
        vsaiStartButton = findViewById(R.id.welcome_IMG_startAi);
        soloStartButton = findViewById(R.id.welcome_IMG_startSolo);
        vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        Glide.with(this).load(R.drawable.pocket_clash2).into(title);
        Glide.with(this).load(R.drawable.solo_start_button).into(soloStartButton);
        Glide.with(this).load(R.drawable.vsai_start_button).into(vsaiStartButton);
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
}