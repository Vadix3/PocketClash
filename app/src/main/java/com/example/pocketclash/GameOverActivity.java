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

    private static final int QUIT_GAME = 10;
    private static final int MAIN_MENU = 11;
    private static final int PLAY_AGAIN = 12;

    /**
     * GameMode 0 = solo
     * GameMode 1 = vsAI
     * GameMode 2 = Auto
     */
    public GameOverActivity(Context context, Player winner, int gameMode) {
        super(context);
        this.mContext = context;
        this.winner = winner;
        this.gameMode = gameMode;
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
            headLine.setText("Player 2 has won!");
        } else {
            headLine.setText(winner.getName() + " has won in " + winner.getNumOfTurns() + " turns!");
        }
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
