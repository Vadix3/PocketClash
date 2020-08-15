package com.example.pocketclash;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;

public class MainActivity extends AppCompatActivity {
    /**
     * Widgets
     */

    RelativeLayout mainLayout;

    //ImageViews
    private ImageView backgroundImage;
    private ImageView player1_picture;
    private ImageView player2_picture;

    //Progress Bars
    private ProgressBar player1_health;
    private ProgressBar player2_health;

    //TextViews
    private TextView player1_name;
    private TextView player2_name;

    private TextView player1_skill1;
    private TextView player1_skill2;
    private TextView player1_skill3;

    private TextView player2_skill1;
    private TextView player2_skill2;
    private TextView player2_skill3;

    Toast feedBackToast;

    /**
     * Listeners
     */
    private View.OnClickListener weakClick;
    private View.OnClickListener mediumClick;
    private View.OnClickListener strongClick;

    /**
     * Variables
     */
    private static final int LOW_DAMAGE = 10;
    private static final int MED_DAMAGE = 15;
    private static final int MAX_DAMAGE = 20;
    private static final int MAX_HP = 100;

    MediaPlayer mediaPlayer;

    //Enums
    private enum CurrentPlayer {PLAYER1, PLAYER2}

    private CurrentPlayer currentPlayer; // Who's turn it is

    private Player player1;
    private Player player2;

    /**
     * TODO: 1. Add on click animations
     * 2. Winner fragment
     * 3. Theme selection? Grid?
     * 4. Limit moves? Damage?
     * 5. Name input?
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        player1 = new Player(MAX_HP, "Player1");
        player2 = new Player(MAX_HP, "Player2");

        initWidgets();
        initListeners();
        playMusic();
        newGame();

    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        mediaPlayer.start();
        super.onResume();
    }

    @Override
    protected void onStop() {
        mediaPlayer.pause();
        super.onStop();
    }

    /**
     * A method to play backgroud music
     */
    private void playMusic() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pocket_clash_battle2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

    }

    /**
     * A method to start a new game
     */
    private void newGame() {
        selectStartingPlayer();
    }

    /**
     * A method to select the starting player
     */
    private void selectStartingPlayer() {

        player1.setHealth(100);
        player2.setHealth(100);
        player1_health.setProgress(100);
        player2_health.setProgress(100);

        switch ((int) Math.floor(Math.random() * 2) + 1) {
            case 1:
                currentPlayer = CurrentPlayer.PLAYER1;
                break;
            case 2:
                currentPlayer = CurrentPlayer.PLAYER2;
                break;
        }
        holdPlayer(currentPlayer);
    }

    /**
     * A method to hold waiting player, and release current player
     */
    private void holdPlayer(CurrentPlayer currentPlayer) {
        switch (currentPlayer) {
            case PLAYER1:
                player2_skill1.setBackground(getDrawable(R.drawable.unavailable_skill));
                player2_skill2.setBackground(getDrawable(R.drawable.unavailable_skill));
                player2_skill3.setBackground(getDrawable(R.drawable.unavailable_skill));
                player1_skill1.setBackground(getDrawable(R.drawable.skill_button));
                player1_skill2.setBackground(getDrawable(R.drawable.skill_button));
                player1_skill3.setBackground(getDrawable(R.drawable.skill_button));


                player2_skill1.setClickable(false);
                player2_skill2.setClickable(false);
                player2_skill3.setClickable(false);
                player1_skill1.setClickable(true);
                player1_skill2.setClickable(true);
                player1_skill3.setClickable(true);

                break;
            case PLAYER2:
                player1_skill1.setBackground(getDrawable(R.drawable.unavailable_skill));
                player1_skill2.setBackground(getDrawable(R.drawable.unavailable_skill));
                player1_skill3.setBackground(getDrawable(R.drawable.unavailable_skill));
                player2_skill1.setBackground(getDrawable(R.drawable.skill_button));
                player2_skill2.setBackground(getDrawable(R.drawable.skill_button));
                player2_skill3.setBackground(getDrawable(R.drawable.skill_button));

                player2_skill1.setClickable(true);
                player2_skill2.setClickable(true);
                player2_skill3.setClickable(true);
                player1_skill1.setClickable(false);
                player1_skill2.setClickable(false);
                player1_skill3.setClickable(false);
        }
    }


    /**
     * A method to finish the game
     */
    private void gameOver() {
        player2_skill1.setClickable(false);
        player2_skill2.setClickable(false);
        player2_skill3.setClickable(false);
        player1_skill1.setClickable(false);
        player1_skill2.setClickable(false);
        player1_skill3.setClickable(false);

        player2_skill1.setBackground(getDrawable(R.drawable.unavailable_skill));
        player2_skill2.setBackground(getDrawable(R.drawable.unavailable_skill));
        player2_skill3.setBackground(getDrawable(R.drawable.unavailable_skill));
        player1_skill1.setBackground(getDrawable(R.drawable.unavailable_skill));
        player1_skill2.setBackground(getDrawable(R.drawable.unavailable_skill));
        player1_skill3.setBackground(getDrawable(R.drawable.unavailable_skill));

        if (player1_health.getProgress() <= 0) {
            onGameOver(player2);
        } else {
            onGameOver(player1);
        }
    }

    /**
     * A method to init the listeners as follows:
     * Weak skill click (-10)
     * Medium skill click (-15)
     * Strong skill click (-20)
     */
    private void initListeners() {

        weakClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill1) {
                    dealDamageTo(player2, LOW_DAMAGE);
                } else {
                    dealDamageTo(player1, LOW_DAMAGE);
                }
            }
        };
        mediumClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill2) {
                    dealDamageTo(player2, MED_DAMAGE);
                } else {
                    dealDamageTo(player1, MED_DAMAGE);
                }
            }
        };
        strongClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill3) {
                    dealDamageTo(player2, MAX_DAMAGE);
                } else {
                    dealDamageTo(player1, MAX_DAMAGE);
                }
            }
        };
        player1_skill1.setOnClickListener(weakClick);
        player1_skill2.setOnClickListener(mediumClick);
        player1_skill3.setOnClickListener(strongClick);
        player2_skill1.setOnClickListener(weakClick);
        player2_skill2.setOnClickListener(mediumClick);
        player2_skill3.setOnClickListener(strongClick);


    }

    /**
     * A method to initialize the widgets
     */
    private void initWidgets() {
        mainLayout = findViewById(R.id.main_LAY_mainLayout);
        mainLayout.setClickable(true);
        backgroundImage = findViewById(R.id.main_IMG_backGround);
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(backgroundImage).load(randomizeBackground()).into(backgroundImage);

        player1_skill1 = findViewById(R.id.main_BTN_player1_skill1);
        player1_skill2 = findViewById(R.id.main_BTN_player1_skill2);
        player1_skill3 = findViewById(R.id.main_BTN_player1_skill3);
        player2_skill1 = findViewById(R.id.main_BTN_player2_skill1);
        player2_skill2 = findViewById(R.id.main_BTN_player2_skill2);
        player2_skill3 = findViewById(R.id.main_BTN_player2_skill3);

        player1_name = findViewById(R.id.main_LBL_player1_Stats_name);
        player2_name = findViewById(R.id.main_LBL_player2_Stats_name);
        player1_name.setText("Hi Im player 1");
        player2_name.setText("Hi Im player 2");


        player1_health = findViewById(R.id.main_BAR_Player1_progressBar);
        player2_health = findViewById(R.id.main_BAR_Player2_progressBar);

        player1_health.setProgress(100);
        player2_health.setProgress(100);


        player1_picture = findViewById(R.id.main_IMG_player1_picture);
        player2_picture = findViewById(R.id.main_IMG_player2_picture);

        Glide.with(player1_picture).load(R.drawable.player_demo_circle);
        Glide.with(player2_picture).load(R.drawable.player_demo_circle);
    }

    /**
     * A method to choose the background randomly
     */
    private int randomizeBackground() {
        switch ((int) Math.floor(Math.random() * 3) + 1) {
            case 1:
                return R.drawable.background_game_day2;
            case 2:
                return R.drawable.background_game_afternoon2;
            case 3:
                return R.drawable.background_game_night2;
            default:
                return 1;
        }
    }

    private void checkIfGameOver() {
        if (player1.getHealth() <= 0 || player2.getHealth() <= 0) {
            gameOver();
        }
    }

    /**
     * A function to deal given damage to given player
     */
    private void dealDamageTo(Player damagedPlayer, int damage) {
        if (damagedPlayer.equals(player1)) { // Damage player 1
            player1_health.setProgress(player1_health.getProgress() - damage);
            player1.setHealth(player1_health.getProgress());
            if (player1.getHealth() <= 25)
                player1_health.setProgressTintList(getColorStateList(R.color.colorLowHP));
            displayToast(player2.getName() + " deals " + damage + " damage!");
            holdPlayer(CurrentPlayer.PLAYER1);
        } else { // Damage player 2
            player2_health.setProgress(player2_health.getProgress() - damage);
            player2.setHealth(player2_health.getProgress());
            if (player2.getHealth() <= 25)
                player2_health.setProgressTintList(getColorStateList(R.color.colorLowHP));
            displayToast(player1.getName() + " deals " + damage + " damage!");
            holdPlayer(CurrentPlayer.PLAYER2);
        }
        checkIfGameOver();
    }

    /**
     * A function to display Toast with given text
     */
    private void displayToast(String message) {
        if (feedBackToast != null)
            feedBackToast.cancel();

        feedBackToast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        feedBackToast.show();
    }

    private void exitApp() {
        QuitActivity dialog = new QuitActivity();
        dialog.show(getSupportFragmentManager(), "QuitActivity");
    }

    /**
     * Method to ask for exit when backPress
     */
    @Override
    public void onBackPressed() {
        exitApp();
    }


    /**
     * A method to display the game over dialog
     */
    public void onGameOver(Player winner) {
        WinActivity win = new WinActivity(winner);
        win.show(getSupportFragmentManager(), "WinActivity");
    }
}
