package com.example.pocketclash;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import org.w3c.dom.Text;

public class WinActivity extends DialogFragment {
    private TextView headLine;
    private TextView againButton;
    private TextView quitButton;

    private Player winner;

    public WinActivity(Player winner) {
        this.winner = winner;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_gameover, container, false);
        initWidgets(view);
        initListeners(view);
        return view;
    }

    /**
     * A method to init listeners
     */
    private void initListeners(View view) {
        againButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Intent intent = new Intent(view.getContext(), MainActivity.class);
                startActivity(intent);
                return false;
            }
        });

        quitButton.setOnClickListener(new View.OnClickListener() { // Quit game
            @Override
            /** TODO:Problem here*/
            public void onClick(View view) {
                System.exit(0);
            }
        });
    }

    /**
     * A method to init widgets
     */
    private void initWidgets(View view) {
        againButton = view.findViewById(R.id.gameOver_TXT_again);
        quitButton = view.findViewById(R.id.gameOver_TXT_quit);
        headLine = view.findViewById(R.id.gameOver_LBL_title);
        headLine.setText(winner.getName()+" has won!");
    }
}
