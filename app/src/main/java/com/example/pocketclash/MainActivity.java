package com.example.pocketclash;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.util.Log;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;

/**
 * TODO: **FIX OTHER RESOLUTION PROBLEMS
 */
public class MainActivity extends AppCompatActivity implements CoinFlipActivity.CoinFlipListener {
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

    private TextView player2_skill1;
    private TextView player2_skill2;
    private TextView player2_skill3;

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
     * Variables
     */
    private static final int DAMAGE = 1;
    private static final int HEAL = 2;
    private static final int MAX_HP = 100;
    private static final int SOLO = 0; // Part 1 of HW
    private static final int VS_AI = 1;
    private static final int AUTO = 2; // Part 2 of HW

    private static final int BASHAR = 0;

    private static final String PLAYER2 = "Player2";
    private static final String PLAYER1 = "Player1";
    private int gameMode;
    private int gameTheme;
    private int startingPlayer;

    MediaPlayer mediaPlayer;

    //Shared prefs
    MySP mySP;

    //Players
    private Player player1;
    private Player player2;
    private Player currentPlayer;

    //Arraya
    private ArrayList<Skill> allSkills;
    private ArrayList<Score> top10Scores;

    //Booleans
    private boolean isGameOver = false;
    private boolean isVolumeOn = true;
    private boolean top10ArraySaved = false;

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
        mySP = new MySP(this);
        gameMode = getIntent().getIntExtra("GameType", 0);
        initAllSkills();
        initPlayers();
        initWidgets();
        initListeners();
        initScores();
        if (isVolumeOn) //TODO: Maybe do it on thread?
            playMusic();

        getGameMode();

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
     * 2 = online (in future)
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
     * A method to start a new AUTO game (HW part 2)
     * method continues after callback returns , getStartingAutoPlayer method(
     */
    private void newAutoGame() {
        // TODO: add onStop and on destroy
        // handler.removeCallBacks(Runnable);
        holdPlayer(PLAYER1);
        holdPlayer(PLAYER2);
        flipCoinButton.setVisibility(View.VISIBLE);
    }

    @Override
    public void getStartingAutoPlayer(int result) {
        Vibrator vb = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
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


    /**
     * Make random AI move and wait
     */
    private void makeStupidAIPlay() {
        checkIfGameOver();
        int randomNum = randomIntFromInterval(0, 2);
        Log.d("pttt", "Selected number: " + randomNum);
        final Player damagedPlayer;
        if (currentPlayer.getName() == PLAYER1)
            damagedPlayer = player2;
        else damagedPlayer = player1;

        final Skill randomSKill = currentPlayer.getSkills()[randomNum];
        Log.d("pttt", "Selected skill: " + randomSKill);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
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
        }, 1000);
    }

    /**
     * A method to start a new vsAI game
     */
    private void newVsAIGame() {
        player1_name.setText("Player");
        player2_name.setText("Comp");
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
    }

    /**
     * A method to select the starting player
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
     * TODO:Reduce points here
     */

    private void initListeners() {
        /**TODO: 1.fix the mutual heal
         *       2.move the update Turns after skill execution*/
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
        coinFlipClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                flipCoinToStart();
                flipCoinButton.setVisibility(View.INVISIBLE);
            }
        };
        //TODO: Choose player grid
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


    }

    /**
     * A method to flip the coin to decide starting player (HW part2)
     */
    private void flipCoinToStart() {
        CoinFlipActivity dialog = new CoinFlipActivity(MainActivity.this);
        createDialogFragment(dialog, null);
        dialog.getWindow().setDimAmount(0.9f);
    }

    /**
     * A method to check if attack is possible (enough points)
     */
    private boolean checkIfAttackPossible(Skill skill) {
        /**TODO: do return until fix AI*/

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
     * A method to initialize the widgets
     */
    private void initWidgets() {
        mediaPlayer = MediaPlayer.create(getApplicationContext(), R.raw.god_suriya_bashar);
        mediaPlayer.setLooping(true);
        mainLayout = findViewById(R.id.main_LAY_mainLayout);
        mainLayout.setClickable(true);
        backgroundImage = findViewById(R.id.main_IMG_backGround);
        backgroundImage.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(backgroundImage).load(randomizeBackground()).into(backgroundImage);
        flipCoinButton = findViewById(R.id.main_IMG_flipcoin);
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
        switch (gameTheme) {
            case BASHAR:
                player1_name.setText("Bashar");
                player2_name.setText("Jihadi");
                break;
            default:
                player1_name.setText("Player 1");
                player2_name.setText("Player 2");
        }


        player1_health = findViewById(R.id.main_BAR_Player1_progressBar);
        player2_health = findViewById(R.id.main_BAR_Player2_progressBar);

        player1_health.setProgress(MAX_HP);
        player2_health.setProgress(MAX_HP);


        player1_picture = findViewById(R.id.main_IMG_player1_picture);
        player2_picture = findViewById(R.id.main_IMG_player2_picture);
        gameTheme = BASHAR;
        if (gameTheme == BASHAR) {
            Glide.with(player1_picture).load(R.drawable.bashar2).into(player1_picture);
            Glide.with(player2_picture).load(R.drawable.jihadi_player).into(player2_picture);
        } else {
            Glide.with(player1_picture).load(R.drawable.player_demo_circle);
            Glide.with(player2_picture).load(R.drawable.player_demo_circle);
        }
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
            gameOver();
        }
    }

    /**
     * A function to deal given damage to given player
     */
    private void dealDamage(final Player damagedPlayer, final Skill currentSkill) {
        /** TODO: 1. Use thread
         *        2. Deal adjusted damage
         */

        if (damagedPlayer.equals(player1)) { // Damage player 1 (AI MOVE)
            if (currentSkill.getType() != HEAL) {
                // Deal damage to player 1
                updateStats(player1, currentSkill.getDamage(), DAMAGE);

            } else {
                // Heal player 2
                updateStats(player2, currentSkill.getDamage(), HEAL);

            }
            holdPlayer(PLAYER2);
            releasePlayer(PLAYER1);


        } else { // Damage player 2
            if (currentSkill.getType() != HEAL) {
                // Deal damage to player 2
                updateStats(player2, currentSkill.getDamage(), DAMAGE);
            } else {
                // Heal player 1
                //TODO:check if switch back to player2.heal().getDamage (possible problem)
                updateStats(player1, player1.heal().getDamage(), HEAL);

            }
            holdPlayer(PLAYER1);
            releasePlayer(PLAYER2);
            if (gameMode == VS_AI) { // Wait a bit and wait for AI if game is not over
                checkIfGameOver();
                //Attacking again after finish or adding twice
                if (!isGameOver) // if not over play AI
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
        }, 1500);
    }

    /**
     * A method to update progress Hp and points.
     * Display toast with damage/heal.
     */
    private void updateStats(Player player, int damage, int updateType) {

        /** TODO: Display visual numbers in HP*/
        switch (player.getName()) {
            case PLAYER1:
                if (updateType == DAMAGE) { // Damage player 1
                    updateTurns(player2); // Player 2 dealt damage
                    player1_health.setProgress(player1_health.getProgress() - damage);
                    player1.setHealth(player1_health.getProgress());
                    displayToast(player2.getName() + " deals " + damage + " damage!");
                } else {
                    // Heal player 1
                    updateTurns(player1);
                    player1_health.setProgress(player1_health.getProgress() + damage);
                    player1.setHealth(player1_health.getProgress());
                    displayToast(player1.getName() + " heals " + damage + " HP!");
                }

                break;
            case PLAYER2:
                if (updateType == DAMAGE) { // Damage player 2
                    updateTurns(player1); // Player 1 dealt damage
                    player2_health.setProgress(player2_health.getProgress() - damage);
                    player2.setHealth(player2_health.getProgress());
                    displayToast(player1.getName() + " deals " + damage + " damage!");
                } else {

                    //TODO: Healing for both of them
                    updateTurns(player2);
                    player2_health.setProgress(player2_health.getProgress() + damage);
                    player2.setHealth(player2_health.getProgress());
                    displayToast(player2.getName() + " heals " + damage + " HP!");
                }
                break;
        }
    }

    /**
     * Update number of turnes
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
        QuitActivity dialog = new QuitActivity(MainActivity.this);
        createDialogFragment(dialog, null);
    }

    /**
     * A method to create and show given dialog fragment
     */
    private void createDialogFragment(final Dialog dialog, String val) {
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.55);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.55);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {

            }
        });
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
        isGameOver = true;
        GameOverActivity win = new GameOverActivity(MainActivity.this, winner, gameMode);
        createDialogFragment(win, null);
        getWinnerLocation(winner);
        if (!top10ArraySaved)
            updateTop10Array(winner);
    }

    /**
     * A method to get the location of winning player
     */
    private void getWinnerLocation(Player winner) {

    }

    /**
     * A method to update top 10 array and prefs
     */
    private void updateTop10Array(Player winner) {
        if (winner.getNumOfTurns() != 0) {
            //TODO: 1. Check for duplicates?
            int numberOfTotalTurns = player1.getNumOfTurns() + player2.getNumOfTurns(); // Total turns
            int numberOfWinnerTurns = winner.getNumOfTurns(); // Winner turns
            Score temp = new Score(numberOfWinnerTurns); //TODO: Check if total number of turns is needed

            if (top10Scores == null) { // if array is empty
                top10Scores.add(temp);
            } else { // Array contains something
                if (top10Scores.size() < 10) { // There is space in the array
                    top10Scores.add(temp);

                    Collections.sort(top10Scores);
                } else { // No space in the array

                    //Check if the score is better than the last score
                    if (temp.getNumOfTurns() < top10Scores.get(9).getNumOfTurns()) { //If temp is good
                        top10Scores.set(9, temp);
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
    }


}
