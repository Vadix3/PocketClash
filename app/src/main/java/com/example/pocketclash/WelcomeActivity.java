package com.example.pocketclash;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class WelcomeActivity extends AppCompatActivity {

    /**
     * Widgets
     */
    ImageView title;
    ImageView startButton;
    ImageView backGround;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        initWidgets();
        startButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
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
        title = findViewById(R.id.welcome_IMG_title);
        startButton = findViewById(R.id.welcome_IMG_startButton);
        backGround = findViewById(R.id.welcome_IMG_backGround);
        backGround.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(this).load(R.drawable.pocket_clash2).into(title);
        Glide.with(this).load(R.drawable.start_button).into(startButton);
        Glide.with(this).load(R.drawable.background_welcome2).into(backGround);

    }
}