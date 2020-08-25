package com.example.pocketclash;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class CoinFlipActivity extends Dialog {

    private Activity activity;

    private ImageView player1Image;
    private ImageView player2Image;

    private CallBackListener listener;

    private boolean isPlayer1Starting = true;

    public CoinFlipActivity(Activity activity) {
        super(activity);
        this.activity = activity;
        Toast.makeText(activity, "Tap to start", Toast.LENGTH_SHORT).show();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_coinflip);
        this.setCancelable(false);

        /**Decide winner here and return result.
         * Player 1 = even
         * Player 2 = odd
         */
        initViews();
        decideWhoStarts();
    }

    /**
     * A method to decide who starts
     */
    private void decideWhoStarts() {

    }

    private void initViews() {

        player1Image = findViewById(R.id.ImageView01);
        Glide.with(player1Image).load(R.drawable.player1_coin2).into(player1Image);
        player2Image = findViewById(R.id.ImageView02);
        Glide.with(player2Image).load(R.drawable.player2_coin2).into(player2Image);
        player1Image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                player1Image.setClickable(false);
                player2Image.setClickable(false);
                int randNum = randomIntFromInterval(8, 20);
                flipCoin(randNum);
            }
        });
        player2Image.setVisibility(View.GONE);
    }

    /**
     * A method to flip the coin a couple of times
     */
    private void flipCoin(int numOfRotations) {
        if (isPlayer1Starting) {
            applyRotation(0, 90, numOfRotations);
            isPlayer1Starting = !isPlayer1Starting;

        } else {
            applyRotation(0, 90, numOfRotations);
            isPlayer1Starting = !isPlayer1Starting;
        }
    }

    /**
     * Get random numbers from given interval
     */
    private int randomIntFromInterval(int min, int max) { // min and max included
        return (int) Math.floor(Math.random() * (max - min + 1) + min);
    }

    private void applyRotation(float start, float end, int numOfRotations) {
// Find the center of image
        final float centerX = player1Image.getWidth() / 2.0f;
        final float centerY = player1Image.getHeight() / 2.0f;
// Create a new 3D rotation with the supplied parameter
// The animation listener is used to trigger the next animation
        final Flip3dAnimation rotation =
                new Flip3dAnimation(start, end, centerX, centerY);
        rotation.setDuration(100);
        rotation.setFillAfter(true);
        rotation.setInterpolator(new AccelerateInterpolator());
        rotation.setAnimationListener(new DisplayNextView(isPlayer1Starting, player1Image,
                player2Image, numOfRotations));
        rotation.setRepeatCount(numOfRotations);

        if (isPlayer1Starting) {
            player1Image.startAnimation(rotation);
        } else {
            player2Image.startAnimation(rotation);
        }
    }

    public final class DisplayNextView implements Animation.AnimationListener {
        private boolean mCurrentView;
        ImageView image1;
        ImageView image2;
        int numOfRotations;

        public DisplayNextView(boolean currentView, ImageView image1, ImageView image2,
                               int numOfRotations) {
            mCurrentView = currentView;
            this.image1 = image1;
            this.image2 = image2;
            this.numOfRotations = numOfRotations;
        }

        public void onAnimationStart(Animation animation) {
        }

        public void onAnimationEnd(Animation animation) {
            final int startingPlayer;
            if (numOfRotations % 2 == 0) {
                Glide.with(image2).load(R.drawable.player1_coin2).into(image2);
                startingPlayer = 1;
                Toast.makeText(activity, "Player 1 starts!", Toast.LENGTH_SHORT).show();
            } else {
                Glide.with(image1).load(R.drawable.player2_coin2).into(image1);
                startingPlayer = 2;
                Toast.makeText(activity, "Player 2 starts!", Toast.LENGTH_SHORT).show();
            }
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    dismiss();
                    startGame(startingPlayer);
                }
            }, 1200);
        }

        public void onAnimationRepeat(Animation animation) {
            image1.post(new SwapViews(mCurrentView, image1, image2));
        }

        /**
         * A method to return to the game with starting player result
         */
        public void startGame(final int startingPlayer) {
            try {
                listener = (CallBackListener) activity;
            } catch (ClassCastException e) {
                throw new ClassCastException(activity.toString() + "Must implement dialog listener");
            }

            listener.getCallback(startingPlayer);
        }
    }

}
