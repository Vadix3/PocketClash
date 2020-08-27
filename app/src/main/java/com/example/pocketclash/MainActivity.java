package com.example.pocketclash;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity implements CallBackListener {
    /**
     * Views
     */
    RelativeLayout mainLayout;
    RelativeLayout player1Stats;
    RelativeLayout player2Stats;
    LinearLayout player2SkillsLayout;

    //ImageViews
    private ImageView backgroundImage;
    private ImageView player1_picture;
    private ImageView player2_picture;
    private ImageView volumeButton;
    private ImageView flipCoinButton;

    //Progress Bars
    private ProgressBar player1_health;
    private ProgressBar player2_health;

    //TextViews
    private TextView player1_name;
    private TextView player2_name;

    private TextView player1_skill1;
    private TextView player1_skill2;
    private TextView player1_skill3;
    private TextView player1_hp;

    private TextView player2_skill1;
    private TextView player2_skill2;
    private TextView player2_skill3;
    private TextView player2_hp;


    Toast feedBackToast;

    /**
     * Listeners
     */
    private View.OnClickListener weakClick;
    private View.OnClickListener strongClick;
    private View.OnClickListener healClick;
    private View.OnClickListener volumeClick;
    private View.OnClickListener coinFlipClick;

    /**
     * Display stats on long click. Didnt work, enables the button
     */
    private View.OnLongClickListener statsListener;

    /**
     * Dialogs
     */
    QuitActivity quitDialog;
    GameOverActivity gameOverDialog;

    /**
     * Variables
     */
    private static final int LOW_DAMAGE = 0;
    private static final int HIGH_DAMAGE = 1;
    private static final int HEAL = 2;
    private static final int MAX_HP = 100;
    private static final int SOLO = 0; // Part 1 of HW
    private static final int VS_AI = 1;
    private static final int AUTO = 2; // Part 2 of HW
    private static final int QUIT_GAME = 10;
    private static final int MAIN_MENU = 11;
    private static final int PLAY_AGAIN = 12;

    private static final int BASHAR = 0;

    private static final String PLAYER2 = "Player2";
    private static final String PLAYER1 = "Player1";
    private static final String TAG = "pttt";
    private int gameTheme;// In construction
    private int gameMode; // AUTO, VS_AI, SOLO*/
    private int gameOverMode = 0; // MENU, QUIT, SOLO*/
    private int startingPlayer;
    private int callbackResult = 0;

    //Handlers
    private MediaPlayer mediaPlayer;
    private Handler handler;
    private Runnable playAIRunnable;
    private Runnable autoPlayRunnable;

    //Location
    FusedLocationProviderClient fusedLocationProviderClient;
    private static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;


    //Shared prefs
    MySP mySP;

    //Players
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    //Arrays
    private ArrayList<Skill> allSkills;
    private ArrayList<Score> top10Scores;

    //Booleans
    private boolean basharMode;
    private boolean isGameOver = false;
    private boolean isVolumeOn = true;
    private boolean top10ArraySaved = false;
    private boolean isHandlerRunning = false;
    private boolean isQuitDialogCalled = false;
    private boolean isGameOverDialogCalled = false;
    private boolean isBackButtonPressed = false;
    private boolean hasAImademove = false;

    /**
     * A method to run the handler if not already running
     */
    private void runHandlerIfNotRunning() {
        if (!isHandlerRunning) { // If handler is not running, run it
            if (gameMode == AUTO) {
                handler.postDelayed(autoPlayRunnable, 1000);
            }
            if (gameMode == VS_AI && !hasAImademove)
                handler.postDelayed(playAIRunnable, 1000);
            isHandlerRunning = true;
        }
    }

    /**
     * Stop the handler
     */
    private void stopHandler() {
        if (mediaPlayer.isPlaying())
            mediaPlayer.pause();
        handler.removeCallbacksAndMessages(autoPlayRunnable);
        handler.removeCallbacksAndMessages(playAIRunnable);
        isHandlerRunning = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("pttt", "On pause");
        stopHandler();
        if (gameOverMode == 0) { // Game is not over yet, here for onBackPressed or home
            if (isGameOver) { // The game is over, go do game over sequence
                gameOver();
            } else { // The game is not over, user pressed back button.
                if (quitDialog.isShowing() || isBackButtonPressed) { // Dialog is not showing,
                    // but still going to exit app
                    Log.d("pttt", "exiting app");
                    exitApp();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("pttt", "On resume");
        if (!isQuitDialogCalled && flipCoinButton.getVisibility() != View.VISIBLE) {
            runHandlerIfNotRunning();
        }
        if (isVolumeOn && !isGameOverDialogCalled && !isQuitDialogCalled) {
            mediaPlayer.start();
        }
    }

    @Override
    protected void onStop() {
        Log.d("pttt", "On stop");
        super.onStop();
        if (feedBackToast != null) {
            feedBackToast.cancel();
        }
        if (gameOverMode == 0) { // Game is not over yet, here for onBackPressed or home
            /** If quit dialog is open, close to reopen upon onStart*/
            if (isQuitDialogCalled) {
                Log.d("pttt", "Quit dialog was called, stopping app");
            }
            stopHandler();
            mediaPlayer.pause();
        }
        if (gameOverDialog != null) {
            gameOverDialog.dismiss();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        isBackButtonPressed = false;
        Log.d("pttt", "On start");

        /** If the quit dialog was called upon onStop, reopen it again*/
        if (isQuitDialogCalled) {
            Log.d("pttt", "onStart, Quit dialog was open");
        } else {
            Log.d("pttt", "onStart, Quit dialog was not open");
            runHandlerIfNotRunning();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        mySP = new MySP(this);
        gameMode = getIntent().getIntExtra("GameType", 0);
        basharMode = getIntent().getBooleanExtra("basharMode", false);
        Log.d(TAG, "basharMode: " + basharMode);
        initAllSkills();
        initNewGame();
        super.onCreate(savedInstanceState);
    }

    /**
     * Initialize a new game
     */
    private void initNewGame() {
        isGameOver = false;
        top10ArraySaved = false;
        isVolumeOn = true;
        initPlayers();
        initViews();
        initListeners();
        initScores();
        if (isVolumeOn && mediaPlayer.isPlaying() == false)
            playMusic();
        getGameMode();
    }

    /**
     * A method to initialize the widgets
     */
    private void initViews() {
        if (basharMode) {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.god_suriya_bashar);
        } else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.ein_kmo_bb);
        }
        mediaPlayer.setLooping(true);
        mainLayout = findViewById(R.id.main_LAY_mainLayout);
        mainLayout.setClickable(true);
        player1Stats = findViewById(R.id.main_LAY_player1_StatsLayout);
        glideToBackground(player1Stats, R.drawable.health_bar);
        player2Stats = findViewById(R.id.main_LAY_player2_StatsLayout);
        glideToBackground(player2Stats, R.drawable.health_bar);
        backgroundImage = findViewById(R.id.main_IMG_backGround);
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(backgroundImage).load(getRandomBackground()).into(backgroundImage);
        flipCoinButton = findViewById(R.id.main_IMG_flipcoin);
        volumeButton = findViewById(R.id.main_IMG_volume);
        player1_skill1 = findViewById(R.id.main_BTN_player1_skill1);
        player1_skill1.setText(player1.lowSkill().getName());
        player1_skill2 = findViewById(R.id.main_BTN_player1_skill2);
        player1_skill2.setText(player1.highSkill().getName());
        player1_skill3 = findViewById(R.id.main_BTN_player1_skill3);
        player1_skill3.setText(player1.heal().getName());
        player1_hp = findViewById(R.id.main_BAR_Player1_HealthPoings);
        player2_skill1 = findViewById(R.id.main_BTN_player2_skill1);
        player2_skill1.setText(player2.lowSkill().getName());
        player2_skill2 = findViewById(R.id.main_BTN_player2_skill2);
        player2_skill2.setText(player2.highSkill().getName());
        player2_skill3 = findViewById(R.id.main_BTN_player2_skill3);
        player2_skill3.setText(player2.heal().getName());
        player2_hp = findViewById(R.id.main_BAR_Player2_HealthPoings);


        player1_name = findViewById(R.id.main_LBL_player1_Stats_name);
        player2_name = findViewById(R.id.main_LBL_player2_Stats_name);

        if (basharMode) {
            player1_name.setText("Bashar Assad");
            player2_name.setText("Jihadi");
        } else {
            player1_name.setText("Benjamin Netanyahu");
            player2_name.setText("Hassan Rouhani");
        }

        player1_health = findViewById(R.id.main_BAR_Player1_progressBar);
        player2_health = findViewById(R.id.main_BAR_Player2_progressBar);

        player1_health.setProgress(MAX_HP);
        player2_health.setProgress(MAX_HP);

        player1_hp.setText("100");
        player2_hp.setText("100");

        checkIfSwitchProgressColor();
        quitDialog = new QuitActivity(MainActivity.this);

        player1_picture = findViewById(R.id.main_IMG_player1_picture);
        player2_picture = findViewById(R.id.main_IMG_player2_picture);
        gameTheme = BASHAR;
        if (basharMode == true) {
            Glide.with(player1_picture).load(R.drawable.bashar2).into(player1_picture);
            Glide.with(player2_picture).load(R.drawable.jihadi_player).into(player2_picture);
        } else {
            Glide.with(player1_picture).load(R.drawable.bb).into(player1_picture);
            Glide.with(player2_picture).load(R.drawable.rohani).into(player2_picture);
        }
    }

    /**
     * A method to get a random background id
     */
    private int getRandomBackground() {
        switch (randomIntFromInterval(1, 3)) {
            case 1:
                return R.drawable.my_background_colorgreen;
            case 2:
                return R.drawable.my_background_colorrblueorange;
            case 3:
                return R.drawable.my_background_colorredpink;
            default:
                return R.drawable.my_background_colorgreen;
        }
    }

    /**
     * A method to init the listeners as follows:
     * Weak attack skill click
     * Medium attac skill click
     * Heal skill click
     */
    private void initListeners() {

        /** Weak skill*/
        weakClick = new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (view == player1_skill1) {

                    if (checkIfAttackPossible(player1.lowSkill()))
                        dealDamage(player2, player1.lowSkill());
                } else {
                    if (checkIfAttackPossible(player2.lowSkill()))
                        dealDamage(player1, player2.lowSkill());
                }
            }
        };
        /** Strong skill*/
        strongClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill2) {
                    if (checkIfAttackPossible(player1.highSkill()))
                        dealDamage(player2, player1.highSkill());

                } else {
                    if (checkIfAttackPossible(player2.highSkill()))
                        dealDamage(player1, player2.highSkill());
                }
            }
        };
        /** Heal*/
        healClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill3) {
                    if (checkIfAttackPossible(player1.heal()))
                        dealDamage(player2, player1.heal());


                } else {
                    if (checkIfAttackPossible(player2.heal()))
                        dealDamage(player1, player2.heal());
                }
            }
        };
        /**Volume button*/
        volumeClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isVolumeOn) { // Volume is on
                    volumeButton.setImageResource(R.drawable.ic_baseline_volume_off_24);
                    mediaPlayer.pause();
                    isVolumeOn = false;
                } else { //Volume is off
                    volumeButton.setImageResource(R.drawable.ic_baseline_volume_up_24);
                    playMusic();
                    isVolumeOn = true;
                }
            }
        };
        /** Coin flip*/
        coinFlipClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCoinToStart();
                flipCoinButton.setVisibility(View.INVISIBLE);
            }
        };
        /** Display the stats of the given attack - ** Not in use **/
        statsListener = new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                Skill temp = getSkillFromArray(view);
                if (temp.getType() == HEAL)
                    displayToast("Heal: " + temp.getDamage() + " HP\nAP: " + temp.getPoints() +
                            " Points");
                else
                    displayToast("Damage: " + temp.getDamage() + " HP\nAP: " + temp.getPoints() +
                            " Points");

                return true;
            }
        };
        player1_skill1.setOnClickListener(weakClick);
        player1_skill2.setOnClickListener(strongClick);
        player1_skill3.setOnClickListener(healClick);
        flipCoinButton.setOnClickListener(coinFlipClick);

        if (gameMode == SOLO) {
            player2_skill1.setOnClickListener(weakClick);
            player2_skill2.setOnClickListener(strongClick);
            player2_skill3.setOnClickListener(healClick);
        }
        volumeButton.setOnClickListener(volumeClick);
        handler = new Handler();
    }

    /**
     * A method to init the top 10 scores array from SP
     */
    private void initScores() {
        Gson gson = new Gson();
        String top10String = mySP.getString(MySP.KEYS.TOP_10_ARRAY, "");
        Type scoreType = new TypeToken<ArrayList<Score>>() {
        }.getType();
        top10Scores = gson.fromJson(top10String, scoreType); // get grades array from json
        if (top10Scores != null) { // If there is something to load
            System.out.println("Scores array: " + top10Scores.toString());
        } else { // If there is nothing to load, init array
            top10Scores = new ArrayList<Score>();
        }
    }

    /**
     * A method to initialize the total skills array (Database of skills)
     * Name,Damage,Points,Type
     */
    private void initAllSkills() {
        allSkills = new ArrayList<Skill>();
        // Light skills
        Skill punch = new Skill("Punch", 10, 7, LOW_DAMAGE);
        Skill slap = new Skill("Slap", 10, 7, LOW_DAMAGE);
        Skill spank = new Skill("Spank", 10, 7, LOW_DAMAGE);

        // Heavy skills
        Skill headButt = new Skill("Cut", 20, 5, HIGH_DAMAGE);
        Skill kick = new Skill("Kick", 20, 5, HIGH_DAMAGE);
        Skill charge = new Skill("Charge", 20, 5, HIGH_DAMAGE);

        // Heal skills
        Skill meditate = new Skill("Heal", 10, 3, HEAL);
        Skill drain = new Skill("Drain", 10, 3, HEAL);
        Skill potion = new Skill("Potion", 10, 3, HEAL);

        allSkills.add(punch);
        allSkills.add(slap);
        allSkills.add(spank);
        allSkills.add(headButt);
        allSkills.add(kick);
        allSkills.add(charge);
        allSkills.add(meditate);
        allSkills.add(drain);
        allSkills.add(potion);
    }

    /**
     * A method to init players (HP,Name,Skills)
     */
    private void initPlayers() {
        player1 = new Player(MAX_HP, PLAYER1);
        player2 = new Player(MAX_HP, PLAYER2);
        getPlayerSkills();
    }

    /**
     * Randomize player skills
     */
    private void getPlayerSkills() {

        Skill[] player1Skills = new Skill[3];
        Skill[] player2Skills = new Skill[3];

        // Low damage
        player1Skills[0] = new Skill(allSkills.get(randomIntFromInterval(0, 2)));
        player2Skills[0] = new Skill(allSkills.get(randomIntFromInterval(0, 2)));

        //High damage
        player1Skills[1] = new Skill(allSkills.get(randomIntFromInterval(3, 5)));
        player2Skills[1] = new Skill(allSkills.get(randomIntFromInterval(3, 5)));

        //Heal
        player1Skills[2] = new Skill(allSkills.get(randomIntFromInterval(6, 8)));
        player2Skills[2] = new Skill(allSkills.get(randomIntFromInterval(6, 8)));

        player1.setSkills(player1Skills);
        player2.setSkills(player2Skills);

    }

    /**
     * Get random numbers from given interval
     */
    private int randomIntFromInterval(int min, int max) { // min and max included
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    /**
     * A method to get the game mode as follows:
     * 0 = solo
     * 1 = vs ai
     * 2 = auto
     */
    private void getGameMode() {
        switch (gameMode) {
            case SOLO:
                newSoloGame();
                break;
            case VS_AI:
                newVsAIGame();
                break;
            case AUTO:
                newAutoGame();
                break;
        }
    }

    /**
     * A method to play background music
     */
    private void playMusic() {
        mediaPlayer.start();
    }

    /**
     * A method to start a new solo game
     */
    private void newSoloGame() {
        holdPlayer(PLAYER2);
        releasePlayer(PLAYER1);
    }

    /**
     * A method to start a new AUTO game (HW part 2)
     */
    private void newAutoGame() {
        holdPlayer(PLAYER1);
        holdPlayer(PLAYER2);
        flipCoinButton.setVisibility(View.VISIBLE);
        Glide.with(flipCoinButton).load(R.drawable.flip_start).into(flipCoinButton);
    }

    /**
     * A CallBack method to get data from fragments
     */
    @Override
    public void getCallback(int result) {
        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        /** See who is the starting player, and init game accordingly*/
        if (result == 1 || result == 2) { // Coming from coinFlip
            vb.vibrate(400);
            startingPlayer = result;
            if (result == 1) {
                currentPlayer = player1;
                holdPlayer(PLAYER2);
                releasePlayer(PLAYER1);
            } else {
                currentPlayer = player2;
                holdPlayer(PLAYER1);
                releasePlayer(PLAYER2);
            }
            makeStupidAIPlay();
        }
        /** See what is the return result of gameOver menu. MAIN_MENU / AGAIN / QUIT*/
        else {
            callbackResult = result;
            performRequiredOP();
        }
    }

    /**
     * Make random AI move and wait
     */
    private void makeStupidAIPlay() {
        player1_skill1.setClickable(false);
        player1_skill2.setClickable(false);
        player1_skill3.setClickable(false);

        if (!isGameOver)
            checkIfGameOver();
        int randomNum = randomIntFromInterval(0, 2);
        final Player damagedPlayer;
        if (currentPlayer.getName() == PLAYER1)
            damagedPlayer = player2;
        else damagedPlayer = player1;

        final Skill randomSKill = currentPlayer.getSkills()[randomNum];

        autoPlayRunnable = new Runnable() {
            @Override
            public void run() {
                if (!isGameOver) {
                    dealDamage(damagedPlayer, randomSKill);
                    if (currentPlayer.getName() == PLAYER1) {
                        currentPlayer = player2;
                    } else currentPlayer = player1;
                    makeStupidAIPlay();
                }
            }
        };
        handler.postDelayed(autoPlayRunnable, 1000);
    }

    /**
     * A method to start a new vsAI game
     */
    private void newVsAIGame() {
        player2SkillsLayout = findViewById(R.id.main_LAY_player2_SkillsLayout);
        selectStartingPlayer();
        if (startingPlayer == 2) { // AI goes first
            playAI();
        }
    }

    /**
     * A method to make AI move
     */
    private void playAI() {
        /** Insert AI move here*/
        if (gameMode == VS_AI)
            makeSmartAIPlay();
    }

    /**
     * A method to make AI move
     * Try to win, apply logic
     */
    private void makeSmartAIPlay() {
        if (player2.getHealth() + player2.heal().getDamage() < MAX_HP && // Try to heal first if possible
                checkIfAttackPossible(player2.heal())) {
            dealDamage(player1, player1.heal());

        } else { // No need to heal, try to play strong attack
            if (checkIfAttackPossible(player2.highSkill())) { // Try to go for max damage first
                dealDamage(player1, player2.highSkill());
            } else { // Strong attack is not possible
                if (checkIfAttackPossible(player2.lowSkill())) { // Try to play low skill
                    dealDamage(player1, player2.lowSkill());
                } else { // No points available
                    displayToast("AI has no more skills available");
                }
            }
        }
        hasAImademove = true;
    }

    /**
     * A method to randomly select the starting player
     */
    private void selectStartingPlayer() {
        player1.setHealth(MAX_HP);
        player2.setHealth(MAX_HP);
        player1_health.setProgress(MAX_HP);
        player2_health.setProgress(MAX_HP);

        switch ((int) Math.floor(Math.random() * 2) + 1) {
            case 1: // Human goes first
                holdPlayer(PLAYER2);
                releasePlayer(PLAYER1);
                startingPlayer = 1;
                break;
            case 2:
                startingPlayer = 2;
                holdPlayer(PLAYER1);
                releasePlayer(PLAYER2);
                break;
        }
    }

    /**
     * A method to release current player
     */
    private void releasePlayer(String playerName) {
        switch (playerName) {
            case PLAYER1: // Releasing player 1
                glideToBackground(player1_skill1, R.drawable.skill_button);
                glideToBackground(player1_skill2, R.drawable.heavy_attack_button);
                glideToBackground(player1_skill3, R.drawable.heal_button);

                player1_skill1.setClickable(true);
                player1_skill2.setClickable(true);
                player1_skill3.setClickable(true);
                break;
            case PLAYER2: // Releasing player2
                glideToBackground(player2_skill1, R.drawable.skill_button);
                glideToBackground(player2_skill2, R.drawable.heavy_attack_button);
                glideToBackground(player2_skill3, R.drawable.heal_button);
                player2_skill1.setClickable(true);
                player2_skill2.setClickable(true);
                player2_skill3.setClickable(true);
                break;
        }
    }

    /**
     * A method to hold given player
     */
    private void holdPlayer(String playerName) {
        switch (playerName) {
            case PLAYER1: // Holding player 1
                glideToBackground(player1_skill1, R.drawable.unavailable_skill);
                glideToBackground(player1_skill2, R.drawable.unavailable_skill);
                glideToBackground(player1_skill3, R.drawable.unavailable_skill);

                player1_skill1.setClickable(false);
                player1_skill2.setClickable(false);
                player1_skill3.setClickable(false);
                break;
            case PLAYER2:
                glideToBackground(player2_skill1, R.drawable.unavailable_skill);
                glideToBackground(player2_skill2, R.drawable.unavailable_skill);
                glideToBackground(player2_skill3, R.drawable.unavailable_skill);

                player2_skill1.setClickable(false);
                player2_skill2.setClickable(false);
                player2_skill3.setClickable(false);
                break;
        }
    }

    /**
     * A method to hold the players, and see who won
     */
    private void gameOver() {
        player2_skill1.setClickable(false);
        player2_skill2.setClickable(false);
        player2_skill3.setClickable(false);
        player1_skill1.setClickable(false);
        player1_skill2.setClickable(false);
        player1_skill3.setClickable(false);

        glideToBackground(player2_skill1, R.drawable.unavailable_skill);
        glideToBackground(player2_skill2, R.drawable.unavailable_skill);
        glideToBackground(player2_skill3, R.drawable.unavailable_skill);
        glideToBackground(player1_skill1, R.drawable.unavailable_skill);
        glideToBackground(player1_skill2, R.drawable.unavailable_skill);
        glideToBackground(player1_skill3, R.drawable.unavailable_skill);

        if (player1_health.getProgress() <= 0) {
            player1.setHealth(0);
            onGameOver(player2);
        } else {
            onGameOver(player1);
            player2.setHealth(0);
        }
    }

    /**
     * A method to return the damage and the remaining points of the selected skill
     */
    private Skill getSkillFromArray(View view) {
        String skillName = ((TextView) view).getText().toString();

        if (view.equals(player1_skill1) || view.equals(player1_skill2) ||
                view.equals(player1_skill3)) { // Player 1 skill
            for (Skill skill : player1.getSkills())
                if (skill.getName().equals(skillName)) {
                    Log.d("pttt", "Player 1 skill: " + skill.getName());
                    return skill;
                }
        } else { // Player 2 skill
            for (Skill skill : player2.getSkills())
                if (skill.getName().equals(skillName)) {
                    Log.d("pttt", "Player 2 skill: " + skill.getName());
                    return skill;
                }
        }
        return null;
    }

    /**
     * A method to flip the coin to decide starting player
     */
    private void flipCoinToStart() {
        CoinFlipActivity dialog = new CoinFlipActivity(MainActivity.this);
        createDialogFragment(dialog, "coin");
        dialog.getWindow().setDimAmount(0.9f);
    }


    /**
     * A method to check if attack is possible (enough points)
     */
    private boolean checkIfAttackPossible(Skill skill) {
        if (skill.getPoints() > 0) {
            /** Not reducing the number of points for AUTO mode*/
            if (gameMode != AUTO) {
                skill.setPoints(skill.getPoints() - 1);
            }
            return true;
        } else {
            displayToast("Not enough points!");
            return false;
        }
    }


    /**
     * A method to check if the game is over
     */
    private void checkIfGameOver() {
        if (player1.getHealth() <= 0 || player2.getHealth() <= 0) {
            isGameOver = true; // Declare over
            onPause(); // Pause game
        }
    }

    /**
     * A function to deal given damage to given player
     */
    private void dealDamage(final Player damagedPlayer, final Skill currentSkill) {
        if (damagedPlayer.equals(player1)) { // Damage player 1 (AI MOVE)
            if (currentSkill.getType() != HEAL) {
                // Deal damage to player 1
                updateStats(player1, currentSkill.getDamage(), HIGH_DAMAGE);

            } else {
                // Heal player 2
                updateStats(player2, currentSkill.getDamage(), HEAL);

            }
            holdPlayer(PLAYER2);
            releasePlayer(PLAYER1);


        } else { // Damage player 2
            if (currentSkill.getType() != HEAL) {
                // Deal damage to player 2
                updateStats(player2, currentSkill.getDamage(), HIGH_DAMAGE);
            } else {
                // Heal player 1
                updateStats(player1, player1.heal().getDamage(), HEAL);

            }
            holdPlayer(PLAYER1);
            releasePlayer(PLAYER2);
            if (gameMode == VS_AI) { // Wait a bit and wait for AI if game is not over
                if (!isGameOver)
                    checkIfGameOver();
                //Attacking again after finish or adding twice
                if (!isGameOver) { // if not over play AI
                    hasAImademove = false;
                    holdGame();
                }
            }

        }
        checkIfSwitchProgressColor();
        if (!isGameOver)
            checkIfGameOver();
    }

    /**
     * A method to hold the game to simulate thinking (for VS_AI mode)
     */
    private void holdGame() {
        playAIRunnable = new Runnable() {
            @Override
            public void run() {
                playAI();
            }
        };
        handler.postDelayed(playAIRunnable, 1500);
    }

    /**
     * A method to update progress Hp and points.
     * Display toast with damage/heal.
     */
    private void updateStats(Player player, int damage, int updateType) {

        switch (player.getName()) {
            case PLAYER1:
                if (updateType == HIGH_DAMAGE) { // Damage player 1
                    updateTurns(player2); // Player 2 dealt damage
                    player1_health.setProgress(player1_health.getProgress() - damage);
                    player1.setHealth(player1_health.getProgress());
                    displayToast(player2_name.getText() + " deals " + damage + " damage!");
                } else {
                    // Heal player 1
                    updateTurns(player1);
                    player1_health.setProgress(player1_health.getProgress() + damage);
                    player1.setHealth(player1_health.getProgress());
                    displayToast(player1_name.getText() + " heals " + damage + " HP!");
                }
                if (player1_health.getProgress() < 0) {
                    player1_hp.setText("0");
                } else {
                    player1_hp.setText("" + player1_health.getProgress());
                }

                break;
            case PLAYER2:
                if (updateType == HIGH_DAMAGE) { // Damage player 2
                    updateTurns(player1); // Player 1 dealt damage
                    player2_health.setProgress(player2_health.getProgress() - damage);
                    player2.setHealth(player2_health.getProgress());
                    displayToast(player1_name.getText() + " deals " + damage + " damage!");
                } else {

                    updateTurns(player2);
                    player2_health.setProgress(player2_health.getProgress() + damage);
                    player2.setHealth(player2_health.getProgress());
                    displayToast(player2_name.getText() + " heals " + damage + " HP!");
                }
                if (player2_health.getProgress() < 0) {
                    player2_hp.setText("0");
                } else {
                    player2_hp.setText("" + player2_health.getProgress());
                }
                break;
        }
    }

    /**
     * Update number of turns
     */
    private void updateTurns(Player player) {
        if (gameMode != VS_AI) { // if not vs AI update
            player.setNumOfTurns(player.getNumOfTurns() + 1);
        } else {
            if (player.getName() == PLAYER1) {// if vs AI update only player 1
                player.setNumOfTurns(player.getNumOfTurns() + 1);
            }
        }
    }

    /**
     * A method to switch the progress bar color if needed
     */
    private void checkIfSwitchProgressColor() {
        if (player1.getHealth() <= 25)
            player1_health.setProgressTintList(getColorStateList(R.color.colorLowHP));
        if (player2.getHealth() <= 25)
            player2_health.setProgressTintList(getColorStateList(R.color.colorLowHP));
        if (player1.getHealth() > 25)
            player1_health.setProgressTintList(getColorStateList(R.color.colorHighHP));
        if (player2.getHealth() > 25)
            player2_health.setProgressTintList(getColorStateList(R.color.colorHighHP));

    }

    /**
     * A function to display Toast with given text
     */
    private void displayToast(final String message) {

        if (feedBackToast != null)
            feedBackToast.cancel();

        feedBackToast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        feedBackToast.show();
    }

    private void exitApp() {
        createDialogFragment(quitDialog, "exit");
    }

    /**
     * A method to create and show given dialog fragment
     */
    private void createDialogFragment(final Dialog dialog, final String val) {
        if (feedBackToast != null)
            feedBackToast.cancel();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        if (val.equals("exit")) {
            Log.d("pttt", "exit dialog was shown");
        }
        if (val.equals("gameOver")) {
            isGameOverDialogCalled = true;
        }
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.55);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                /** If the dialog is an exit dialog*/
                if (val.equals("exit")) {
                    isBackButtonPressed = false;
                    isQuitDialogCalled = false;
                    onResume();
                }
            }
        });
    }

    /**
     * Method to pause game on back press
     */
    @Override
    public void onBackPressed() {
        if (feedBackToast != null)
            feedBackToast.cancel();
        isBackButtonPressed = true;
        isQuitDialogCalled = true;
        onPause();
    }

    /**
     * A method to display the game over dialog
     */
    public void onGameOver(Player winner) {
        gameOverDialog = new GameOverActivity(MainActivity.this, winner, gameMode, basharMode);
        Score temp = new Score(winner.getNumOfTurns());
        if (!top10ArraySaved) {
            getWinnerLocation(winner, temp);
        }
    }

    /**
     * A method to get the location of winning player
     */
    private void getWinnerLocation(final Player winner, final Score score) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (getApplicationContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) { // If user gave permission
            // Get the winner location
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) { // null if location turned off
                        Double lat = location.getLatitude();
                        Double lon = location.getLongitude();
                        MyLocation tempLocation = new MyLocation(lat, lon);
                        score.setLocation(tempLocation);

                    } else { // Could not determine location
                        Log.d("pttt", "Could not determine location");
                        displayToast("Could not determine location");
                        score.setLocation(new MyLocation(0d, 0d));
                    }
                    if (!top10ArraySaved)
                        updateTop10Array(winner, score);
                    if (!isGameOverDialogCalled)
                        createDialogFragment(gameOverDialog, "gameOver");
                }
            });

        } else { // Request permission from user
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
            mediaPlayer.stop();
            getWinnerLocation(winner, score);
        }
    }

    /**
     * A method to check if location is enabled. ** Not in use
     **/
    public static Boolean isLocationEnabled(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            // This is new method provided in API 28
            LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            return lm.isLocationEnabled();
        } else {
            // This is Deprecated in API 28
            int mode = Settings.Secure.getInt(context.getContentResolver(), Settings.Secure.LOCATION_MODE,
                    Settings.Secure.LOCATION_MODE_OFF);
            return (mode != Settings.Secure.LOCATION_MODE_OFF);
        }
    }

    /**
     * A method to update top 10 array and prefs
     */
    private void updateTop10Array(Player winner, Score score) {
        if (winner.getNumOfTurns() != 0) {
            if (top10Scores == null) { // if array is empty
                top10Scores.add(score);
            } else { // Array contains something
                if (top10Scores.size() < 10) { // There is space in the array
                    top10Scores.add(score);

                    Collections.sort(top10Scores);
                } else { // No space in the array

                    //Check if the score is better than the last score
                    if (score.getNumOfTurns() < top10Scores.get(9).getNumOfTurns()) { //If temp is good
                        top10Scores.set(9, score);
                        Collections.sort(top10Scores);
                    }
                }
            }
            saveArrayToSP();
            top10ArraySaved = true;
        } else {
            //AI has won, you lost to the app you built. GGWP.
        }
    }

    /**
     * A method to save the top10Array to SP
     */
    private void saveArrayToSP() {
        Gson gson = new Gson();
        String gradeJson = gson.toJson(top10Scores);
        mySP.putString(MySP.KEYS.TOP_10_ARRAY, gradeJson);
        Log.d("pttt", "Saved to sp");
    }

    /**
     * A method to perform required OnGameOver operation
     * after receiving callback from gameOver dialog
     * Main menu
     * Quit
     * New Game
     */
    private void performRequiredOP() {
        isGameOverDialogCalled = false;
        switch (callbackResult) {
            case MAIN_MENU:
                finish();
                Intent intent = new Intent(this, WelcomeActivity.class);
                startActivity(intent);
                break;
            case QUIT_GAME:
                System.exit(0);
                break;
            case PLAY_AGAIN:
                initNewGame();
                break;
        }
    }

    /**
     * A method to insert image to view background with glide
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


}