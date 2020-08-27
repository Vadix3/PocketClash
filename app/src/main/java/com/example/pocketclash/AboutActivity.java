package com.example.pocketclash;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

import org.w3c.dom.Text;

//TODO: Songs by:Rami Kazour - God, Syria And Bashar ; Lior Harari - Ha'Licudnikim
//      Pics by Wikipedia - Bashar al assad
//              Facebook - Benjamin Netanyahu Official
//              Jihadi John - NY Times
//              Hassan Rouani - Britanica

public class AboutActivity extends Dialog {
    private RelativeLayout mainLayout;
    private TextView versionText;
    private Context context;
    private String version = "v1.0.0";
    Toast feedBackToast;
    private int tapCounter = 0;

    public AboutActivity(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        versionText = findViewById(R.id.about_LBL_version);
        versionText.setText(version);
        mainLayout = findViewById(R.id.about_LAY_aboutLayout);

        Glide.with(mainLayout).load(R.drawable.about_dialog_window2).into(new CustomTarget<Drawable>() {
            @Override
            public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                mainLayout.setBackground(resource);
            }

            @Override
            public void onLoadCleared(@Nullable Drawable placeholder) {
            }
        });
    }

    /**
     * A function to display Toast with given text
     */
    private void displayToast(final String message) {

        if (feedBackToast != null)
            feedBackToast.cancel();

        feedBackToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        feedBackToast.show();
    }
}
