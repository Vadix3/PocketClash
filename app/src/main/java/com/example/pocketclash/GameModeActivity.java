package com.example.pocketclash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class GameModeActivity extends Dialog implements View.OnTouchListener {

    private Activity myActivity;
    private RelativeLayout mainLayout;
    private ImageView soloImage;
    private ImageView vsAIImage;
    private ImageView autoImage;
    private ImageView background;
    private Vibrator vb;

    public GameModeActivity(Activity activity) {
        super(activity);
        this.myActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_mode);
        initWidgets();
    }

    private void initWidgets() {
        vb = (Vibrator) myActivity.getSystemService(Context.VIBRATOR_SERVICE);
        mainLayout = findViewById(R.id.mode_LAY_mainLayout);
        soloImage = findViewById(R.id.mode_IMG_solo);
        vsAIImage = findViewById(R.id.mode_IMG_vsAI);
        autoImage = findViewById(R.id.mode_IMG_auto);
        background = findViewById(R.id.mode_IMG_background);

        soloImage.setOnTouchListener(this);
        autoImage.setOnTouchListener(this);
        vsAIImage.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        Intent intent = new Intent(myActivity, MainActivity.class);

        switch (view.getId()) {
            case R.id.mode_IMG_solo:
                vb.vibrate(5);
                intent.putExtra("GameType", 0);
                myActivity.startActivity(intent);
                myActivity.finish();
                return false;
            case R.id.mode_IMG_vsAI:
                vb.vibrate(5);
                intent.putExtra("GameType", 1);
                myActivity.startActivity(intent);
                myActivity.finish();
                return false;
            case R.id.mode_IMG_auto:
                vb.vibrate(5);
                intent.putExtra("GameType", 2);
                myActivity.startActivity(intent);
                myActivity.finish();
                return false;
            default:
                return false;
        }
    }
}
