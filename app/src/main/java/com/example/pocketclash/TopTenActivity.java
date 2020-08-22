package com.example.pocketclash;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class TopTenActivity extends AppCompatActivity implements View.OnLongClickListener, View.OnClickListener {

    /**
     * Views
     */
    private RecyclerView recyclerView;
    private TextView clearButton;
    private RelativeLayout mainLayout;
    private ImageView background;
    private Toast toast;
    MySP mySP;
    RecyclerViewAdapter recyclerViewAdapter;
    /**
     * Variables
     */
    private ArrayList<Score> scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mySP = new MySP(getApplicationContext());
        setContentView(R.layout.activity_topten);
        initViews();
        initScores();
        initRecyclerView();
    }

    /**
     * A method to fill the RecyclerView
     */
    private void initRecyclerView() {
        recyclerViewAdapter = new RecyclerViewAdapter(this, scores);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    /**
     * A method to initialize the widgets
     */
    private void initViews() {
        recyclerView = findViewById(R.id.top10_LST_recyclerView);
        clearButton = findViewById(R.id.top10_LBL_clearList);
        clearButton.setOnLongClickListener(this);
        clearButton.setOnClickListener(this);
        mainLayout = findViewById(R.id.top10_LAY_mainLayout);
        background = findViewById(R.id.top10_IMG_background);
        Glide.with(background).load(R.drawable.top_10_background).into(new CustomTarget<Drawable>() {
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
     * A method to save the top10Array to SP
     */
    private void saveArrayToSP() {
        Gson gson = new Gson();
        String gradeJson = gson.toJson(scores);
        mySP.putString(MySP.KEYS.TOP_10_ARRAY, gradeJson);
    }


    private void initScores() {
        scores = new ArrayList<>();
        Gson gson = new Gson();
        String top10String = mySP.getString(MySP.KEYS.TOP_10_ARRAY, "");
        Type scoreType = new TypeToken<ArrayList<Score>>() {
        }.getType();
        scores = gson.fromJson(top10String, scoreType); // get grades array from json
        if (scores != null) { // If there is something to load
            System.out.println("Scores array: " + scores.toString());
        } else { // If there is nothing to load, init array
            scores = new ArrayList<>();
        }
    }

    @Override
    public boolean onLongClick(View view) {
        if (view.getId() == R.id.top10_LBL_clearList) {
            displayToast("List Cleared");
            scores.clear();
            saveArrayToSP();
            initRecyclerView();
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.top10_LBL_clearList) {
            if (toast == null)
                Toast.makeText(this, "Hold to clear list", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * A function to display Toast with given text
     */
    private void displayToast(final String message) {

        if (toast != null)
            toast.cancel();

        toast = Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT);
        toast.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        Intent intent = new Intent(TopTenActivity.this, WelcomeActivity.class);
        startActivity(intent);
    }
}
