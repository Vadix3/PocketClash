package com.example.pocketclash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class QuitActivity extends Dialog implements View.OnClickListener {

    private TextView yesButton;
    private TextView noButton;
    private ImageView background;
    private RelativeLayout mainLayout;

    private Activity myActivity;


    public QuitActivity(Activity activity) {
        super(activity);
        this.myActivity = activity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_quit);
        initWidgets();
        this.setCancelable(false);
    }

    /**
     * A method to init widgets
     */
    private void initWidgets() {
        mainLayout = findViewById(R.id.quitDialog_LAY_mainLayout);
        yesButton = findViewById(R.id.quitDialog_TXT_yesText);
        yesButton.setOnClickListener(this);
        noButton = findViewById(R.id.quitDialog_TXT_noText);
        noButton.setOnClickListener(this);
        background = findViewById(R.id.quitDialog_IMG_background);
        Glide.with(background).load(R.drawable.dialog_window).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mainLayout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    @Override
    public void onBackPressed() {
        dismiss();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quitDialog_TXT_yesText:
                System.exit(0);
                break;
            case R.id.quitDialog_TXT_noText:
                dismiss();
                break;
        }
    }
}
