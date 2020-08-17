package com.example.pocketclash;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    /**
     * Widgets
     */

    RelativeLayout mainLayout;
    LinearLayout player2SkillsLayout;

    //ImageViews
    private ImageView backgroundImage;
    private ImageView player1_picture;
    private ImageView player2_picture;
    private ImageView volumeButton;

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
    private View.OnClickListener volumeClick;

    /**
     * Variables
     */
    private static final int LOW_DAMAGE = 0;
    private static final int HIGH_DAMAGE = 1;
    private static final int HEAL = 2;
    private static final int MAX_HP = 100;
    private static final int VS_AI = 1;
    private static final int SOLO = 0;
    private static final String PLAYER2 = "Player2";
    private static final String PLAYER1 = "Player1";
    private int gameMode;
    private int startingPlayer;

    MediaPlayer mediaPlayer;

    //Players
    private Player player1;
    private Player player2;

    //Skills
    private ArrayList<Skill> allSkills;
    //Booleans
    private boolean isGameOver = false;
    private boolean isVolumeOn = false;

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
        initAllSkills();
        initPlayers();
        initWidgets();
        initListeners();

        if (isVolumeOn) //TODO: Maybe do it on thread?
            playMusic();

        getGameMode();


    }

    /**
     * A method to initialize the total skills array (Database of skills)
     * TODO: add inflatable skill details?
     * Name,Damage,Points,Type
     */
    private void initAllSkills() {
        allSkills = new ArrayList<Skill>();
        // Light skills
        Skill punch = new Skill("Punch", 10, 7, 0);
        Skill slap = new Skill("Slap", 10, 7, 0);
        Skill spank = new Skill("Spank", 10, 7, 0);

        // Heavy skills
        Skill headButt = new Skill("Cut", 20, 5, 1);
        Skill kick = new Skill("Kick", 20, 5, 1);
        Skill charge = new Skill("Charge", 20, 5, 1);

        // Heal skills
        Skill meditate = new Skill("Heal", 10, 3, 2);
        Skill drain = new Skill("Drain", 10, 3, 2);
        Skill potion = new Skill("Potion", 10, 3, 2);

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

        player1Skills[0] = allSkills.get(randomIntFromInterval(0, 2));
        player2Skills[0] = allSkills.get(randomIntFromInterval(0, 2));

        player1Skills[1] = allSkills.get(randomIntFromInterval(3, 5));
        player2Skills[1] = allSkills.get(randomIntFromInterval(3, 5));

        player1Skills[2] = allSkills.get(randomIntFromInterval(6, 8));
        player2Skills[2] = allSkills.get(randomIntFromInterval(6, 8));

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
     * 2 = online (in future)
     */
    private void getGameMode() {
        gameMode = getIntent().getExtras().getInt("GameType");
        switch (gameMode) {
            case SOLO:
                newSoloGame();
                break;
            case VS_AI:
                newVsAIGame();
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        if (isVolumeOn) {
            mediaPlayer.start();
        }
        super.onResume();
    }

    @Override
    protected void onStop() {
        mediaPlayer.pause();
        super.onStop();
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
     * A method to start a new vsAI game
     */
    private void newVsAIGame() {
        player2SkillsLayout = findViewById(R.id.main_LAY_player2_SkillsLayout);
        player2SkillsLayout.setVisibility(View.INVISIBLE);

        selectStartingPlayer();

        if (startingPlayer == 2) { // AI goes first
            playAI();
        }
    }

    /**
     * A method to make AI move
     * TODO: For part 2, randomize skills here
     */
    private void playAI() {

        /** Insert AI move here*/

        dealDamageTo(player1, player2.lowSkill());

        displayToast("AI deals 10 damage!");
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
            case 1: // Human goes first
                holdPlayer(PLAYER2);
                releasePlayer(PLAYER1);
                startingPlayer = 1;
                break;
            case 2:
                startingPlayer = 2;
                if (gameMode == VS_AI) {
                    holdPlayer(PLAYER1);
                    break;
                } else {
                    holdPlayer(PLAYER1);
                    releasePlayer(PLAYER2);
                    break;
                }
        }
    }

    /**
     * A method to release current player
     */
    private void releasePlayer(String playerName) {
        switch (playerName) {
            case PLAYER1: // Releasing player 1
                player1_skill1.setBackground(getDrawable(R.drawable.skill_button));
                player1_skill2.setBackground(getDrawable(R.drawable.heavy_attack_button));
                player1_skill3.setBackground(getDrawable(R.drawable.heal_button));
                player1_skill1.setClickable(true);
                player1_skill2.setClickable(true);
                player1_skill3.setClickable(true);
                break;
            case PLAYER2: // Releasing plyer2
                player2_skill1.setBackground(getDrawable(R.drawable.skill_button));
                player2_skill2.setBackground(getDrawable(R.drawable.heavy_attack_button));
                player2_skill3.setBackground(getDrawable(R.drawable.heal_button));
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
                player1_skill1.setBackground(getDrawable(R.drawable.unavailable_skill));
                player1_skill2.setBackground(getDrawable(R.drawable.unavailable_skill));
                player1_skill3.setBackground(getDrawable(R.drawable.unavailable_skill));
                player1_skill1.setClickable(false);
                player1_skill2.setClickable(false);
                player1_skill3.setClickable(false);
                break;
            case PLAYER2:
                player2_skill1.setBackground(getDrawable(R.drawable.unavailable_skill));
                player2_skill2.setBackground(getDrawable(R.drawable.unavailable_skill));
                player2_skill3.setBackground(getDrawable(R.drawable.unavailable_skill));
                player2_skill1.setClickable(false);
                player2_skill2.setClickable(false);
                player2_skill3.setClickable(false);
                break;
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

        /** Weak skill*/
        weakClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill1) {

                    dealDamageTo(player2, player1.lowSkill());

                } else {
                    dealDamageTo(player1, player2.lowSkill());
                }
            }
        };
        /** Strong skill*/
        mediumClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill2) {
                    dealDamageTo(player2, player1.highSkill());

                } else {
                    dealDamageTo(player1, player2.highSkill());
                }
            }
        };
        /** Heal*/
        strongClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (view == player1_skill3) {
                    //TODO:Heal method here?
                    dealDamageTo(player2, player2.heal());

                } else {
                    dealDamageTo(player1, player1.heal());
                }
            }
        };
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
        player1_skill1.setOnClickListener(weakClick);
        player1_skill2.setOnClickListener(mediumClick);
        player1_skill3.setOnClickListener(strongClick);
        player2_skill1.setOnClickListener(weakClick);
        player2_skill2.setOnClickListener(mediumClick);
        player2_skill3.setOnClickListener(strongClick);
        volumeButton.setOnClickListener(volumeClick);


    }

    /**
     * A method to heal given player
     */
    private void healPlayer(Player player, Skill heal) {
        switch (player.getName()) {
            case PLAYER1:
                player1_health.setProgress(player1_health.getProgress() + heal.getDamage());
                player.setHealth(player1.getHealth() + heal.getDamage());
                break;
            case PLAYER2:
                player2_health.setProgress(player2_health.getProgress() + heal.getDamage());
                player.setHealth(player2.getHealth() + heal.getDamage());
                break;
        }
        checkIfSwitchProgressColor();
    }

    /**
     * A method to initialize the widgets
     */
    private void initWidgets() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.pocket_clash_battle2);
        mediaPlayer.setLooping(true);
        mainLayout = findViewById(R.id.main_LAY_mainLayout);
        mainLayout.setClickable(true);
        backgroundImage = findViewById(R.id.main_IMG_backGround);
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(backgroundImage).load(randomizeBackground()).into(backgroundImage);
        volumeButton = findViewById(R.id.welcome_IMG_menu);

        player1_skill1 = findViewById(R.id.main_BTN_player1_skill1);
        player1_skill1.setText(player1.lowSkill().getName());
        player1_skill2 = findViewById(R.id.main_BTN_player1_skill2);
        player1_skill2.setText(player1.highSkill().getName());
        player1_skill3 = findViewById(R.id.main_BTN_player1_skill3);
        player1_skill3.setText(player1.heal().getName());
        player2_skill1 = findViewById(R.id.main_BTN_player2_skill1);
        player2_skill1.setText(player2.lowSkill().getName());
        player2_skill2 = findViewById(R.id.main_BTN_player2_skill2);
        player2_skill2.setText(player2.highSkill().getName());
        player2_skill3 = findViewById(R.id.main_BTN_player2_skill3);
        player2_skill3.setText(player2.heal().getName());


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


    /**
     * A method to check if the game is over
     */
    private void checkIfGameOver() {
        if (player1.getHealth() <= 0 || player2.getHealth() <= 0) {
            isGameOver = true;
            gameOver();
        }
    }

    /**
     * A function to deal given damage to given player
     */
    private void dealDamageTo(final Player damagedPlayer, Skill currentSkill) {
        /** TODO: 1. Use thread
         *        2. Deal adjusted damage
         */
        if (damagedPlayer.equals(player1)) { // Damage player 1 (AI MOVE)
            updateStats(player1, currentSkill.getDamage(), LOW_DAMAGE);
            holdPlayer(PLAYER2);
            releasePlayer(PLAYER1);

        } else { // Damage player 2
            updateStats(player2, currentSkill.getDamage(), LOW_DAMAGE);
            holdPlayer(PLAYER1);
            releasePlayer(PLAYER2);
            if (gameMode == VS_AI) { // Wait a bit and wait for AI if game is not over
                checkIfGameOver();
                if (!isGameOver)
                    holdGame();
            }

        }
        checkIfSwitchProgressColor();
        checkIfGameOver();
    }

    /**
     * A method to hold the game to simulate thinking
     */
    private void holdGame() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                playAI();
            }
        }, 2000);
    }

    /**
     * A method to update progress Hp and points.
     * Display toast with damage.
     */
    private void updateStats(Player player, int damage, int updateType) {
        switch (player.getName()) {
            case PLAYER1:
                player1_health.setProgress(player1_health.getProgress() - damage);
                player1.setHealth(player1_health.getProgress());
                player2.setNumOfTurns(player2.getNumOfTurns() + 1);
                displayToast(player2.getName() + " deals " + damage + " damage!");
                break;
            case PLAYER2:
                player2_health.setProgress(player2_health.getProgress() - damage);
                player2.setHealth(player2_health.getProgress());
                player1.setNumOfTurns(player1.getNumOfTurns() + 1);
                displayToast(player1.getName() + " deals " + damage + " damage!");
                break;
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


//    /**
//     * A function to deal given damage to given player
//     */
//    private void dealDamageTo(final Player damagedPlayer, final int damage) {
//        /** A thread to make a move*/
//        Thread play = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                if (damagedPlayer.equals(player1)) { // Damage player 1
//                    player1_health.setProgress(player1_health.getProgress() - damage);
//                    player1.setHealth(player1_health.getProgress());
//                    if (player1.getHealth() <= 25)
//                        player1_health.setProgressTintList(getColorStateList(R.color.colorLowHP));
//                    player2.setNumOfTurns(player2.getNumOfTurns() + 1);
//                    runOnUiThread(new Runnable() { // Deal with UI
//                        @Override
//                        public void run() {
//                            displayToast(player2.getName() + " deals " + damage + " damage!");
//                            holdPlayer(PLAYER2);
//                            releasePlayer(PLAYER1);
//                        }
//                    });
//
//                } else { // Damage player 2
//                    player2_health.setProgress(player2_health.getProgress() - damage);
//                    player2.setHealth(player2_health.getProgress());
//                    if (player2.getHealth() <= 25)
//                        player2_health.setProgressTintList(getColorStateList(R.color.colorLowHP));
//                    player1.setNumOfTurns(player1.getNumOfTurns() + 1);
//
//                    runOnUiThread(new Runnable() { // Deal with UI
//                        @Override
//                        public void run() {
//                            displayToast(player1.getName() + " deals " + damage + " damage!");
//                            holdPlayer(PLAYER1);
//                            releasePlayer(PLAYER2);
//                        }
//                    });
//                    humanTurn=false;
//                    playAI();
//                }
//            }
//        });
//        play.start();
//        try {
//            play.join();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        checkIfGameOver();
//    }

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
        QuitActivity dialog = new QuitActivity(MainActivity.this);
        createDialogFragment(dialog);
    }

    /**
     * A method to create and show given dialog fragment
     */
    private void createDialogFragment(Dialog dialog) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.55);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
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
        GameOverActivity win = new GameOverActivity(MainActivity.this, winner, gameMode);
        createDialogFragment(win);
    }
}
