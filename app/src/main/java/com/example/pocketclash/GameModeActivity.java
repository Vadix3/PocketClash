package com.example.pocketclash;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;

public class GameModeActivity extends Dialog implements View.OnClickListener {

    private Context mContext;
    private RelativeLayout mainLayout;
    private ImageView soloImage;
    private ImageView vsAIImage;
    private ImageView autoImage;
    private ImageView background;
    private Vibrator vb;
    private boolean basharMode;

    public GameModeActivity(Context context, boolean basharMode) {
        super(context);
        this.mContext = context;
        this.basharMode = basharMode;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_game_mode);
        initViews();
    }

    private void initViews() {
        vb = (Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE);
        mainLayout = findViewById(R.id.mode_LAY_mainLayout);
        soloImage = findViewById(R.id.mode_IMG_solo);
        Glide.with(soloImage).load(R.drawable.solo_start_button).into(soloImage);
        vsAIImage = findViewById(R.id.mode_IMG_vsAI);
        Glide.with(vsAIImage).load(R.drawable.vsai_start_button).into(vsAIImage);
        autoImage = findViewById(R.id.mode_IMG_auto);
        Glide.with(autoImage).load(R.drawable.auto_button).into(autoImage);

        soloImage.setOnClickListener(this);
        autoImage.setOnClickListener(this);
        vsAIImage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(mContext, MainActivity.class);

        switch (view.getId()) {
            case R.id.mode_IMG_solo:
                dismiss();
                vb.vibrate(5);
                intent.putExtra("GameType", 0);
                intent.putExtra("basharMode", basharMode);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
                break;
            case R.id.mode_IMG_vsAI:
                dismiss();
                vb.vibrate(5);
                intent.putExtra("GameType", 1);
                intent.putExtra("basharMode", basharMode);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
                break;
            case R.id.mode_IMG_auto:
                dismiss();
                vb.vibrate(5);
                intent.putExtra("GameType", 2);
                intent.putExtra("basharMode", basharMode);
                mContext.startActivity(intent);
                ((Activity) mContext).finish();
                break;
            default:
                Log.d("pttt", "Problem");
        }
    }
}
