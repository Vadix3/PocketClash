package com.example.pocketclash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class GameOverActivity extends Dialog implements View.OnClickListener {
    private TextView headLine;
    private TextView againButton;
    private TextView quitButton;
    private TextView menuButton;
    private Context mContext;
    RelativeLayout background;
    private Player winner;
    private int gameMode;
    private CallBackListener listener;
    private boolean basharMode = false;

    private static final int QUIT_GAME = 10;
    private static final int MAIN_MENU = 11;
    private static final int PLAY_AGAIN = 12;

    /**
     * GameMode 0 = solo
     * GameMode 1 = vsAI
     * GameMode 2 = Auto
     */
    public GameOverActivity(Context context, Player winner, int gameMode, boolean basharMode) {
        super(context);
        this.mContext = context;
        this.winner = winner;
        this.gameMode = gameMode;
        this.basharMode = basharMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_gameover);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Vibrator vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
                vb.vibrate(400);
            }
        }, 200);

        initViews();
        this.setCancelable(false);
    }

    /**
     * A method to init widgets
     */
    private void initViews() {
        againButton = findViewById(R.id.gameOver_TXT_again);
        againButton.setOnClickListener(this);
        quitButton = findViewById(R.id.gameOver_TXT_quit);
        quitButton.setOnClickListener(this);
        menuButton = findViewById(R.id.gameOver_TXT_menu);
        menuButton.setOnClickListener(this);
        headLine = findViewById(R.id.gameOver_LBL_title);
        if (gameMode == 1 && winner.getName().equals("Player2")) { // AI has won
            if (basharMode) {
                headLine.setText("Jihadi has won!");
            } else {
                headLine.setText("Rohani has won!");
            }
        } else { // Not AI game
            if (basharMode) {
                if (winner.getName().equals("Player1")) {
                    headLine.setText("Bashar has won in " + winner.getNumOfTurns() + " turns!");
                } else {
                    headLine.setText("Jihadi has won in " + winner.getNumOfTurns() + " turns!");
                }
            } else {
                if (winner.getName().equals("Player2")) {
                    headLine.setText("Rohani has won in " + winner.getNumOfTurns() + " turns!");
                } else {
                    headLine.setText("Bibi has won in " + winner.getNumOfTurns() + " turns!");
                }
            }
        }
        background = findViewById(R.id.gameOver_LAY_mainLayout);
        glideToBackground(background, R.drawable.game_over_dialog);
    }

    /**
     * A method to insert image to background with glide
     */
    private void glideToBackground(final View target, int pictureID) {
        Glide.with(target).load(pictureID).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                target.setBackground(resource);
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
            case R.id.gameOver_TXT_menu:
                gameOverMode(MAIN_MENU);
                dismiss();
                break;
            case R.id.gameOver_TXT_quit:
                dismiss();
                gameOverMode(QUIT_GAME);
                break;
            case R.id.gameOver_TXT_again:
                dismiss();
                gameOverMode(PLAY_AGAIN);
        }
    }

    @Override
    public void onBackPressed() {
    }

    /**
     * A method to return to the game with proper mode
     */
    public void gameOverMode(final int gameOverMode) {
        try {
            listener = (CallBackListener) mContext;
        } catch (ClassCastException e) {
            throw new ClassCastException(mContext.toString() + "Must implement dialog listener");
        }

        listener.getCallback(gameOverMode);
    }
}
