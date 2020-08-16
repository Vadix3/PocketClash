package com.example.pocketclash;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.w3c.dom.Text;

public class GameOverActivity extends Dialog implements View.OnClickListener {
    private TextView headLine;
    private TextView againButton;
    private TextView quitButton;
    private Activity currentActivity;
    RelativeLayout background;
    private Player winner;
    private int gameMode;

    /**
     * GameMode 0 = solo
     * GameMode 1 = vsAI
     */
    public GameOverActivity(Activity activity, Player winner, int gameMode) {
        super(activity);
        this.currentActivity = activity;
        this.winner = winner;
        this.gameMode=gameMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gameover);
        Vibrator vb = (Vibrator) currentActivity.getSystemService(Context.VIBRATOR_SERVICE);
        vb.vibrate(150);
        initWidgets();
        this.setCancelable(false);

    }

    /**
     * A method to init widgets
     */
    private void initWidgets() {
        againButton = findViewById(R.id.gameOver_TXT_again);
        againButton.setOnClickListener(this);
        quitButton = findViewById(R.id.gameOver_TXT_quit);
        quitButton.setOnClickListener(this);
        headLine = findViewById(R.id.gameOver_LBL_title);
        headLine.setText(winner.getName() + " has won in " + winner.getNumOfTurns() + " turns!");
        background = findViewById(R.id.gameOver_LAY_mainLayout);
        Glide.with(background).load(R.drawable.game_over_dialog).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                background.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    /**
     * A method to init listeners
     */
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.gameOver_TXT_again:
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                intent.putExtra("GameType",gameMode);
                currentActivity.startActivity(intent);
                currentActivity.finish();
                break;
            case R.id.gameOver_TXT_quit:
                System.exit(0);
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
