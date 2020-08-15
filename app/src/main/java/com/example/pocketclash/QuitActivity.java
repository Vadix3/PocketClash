package com.example.pocketclash;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

public class QuitActivity extends DialogFragment {

    private TextView yesButton;
    private TextView noButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_quit, container, false);
        initWidgets(view);
        initListeners();
        this.setCancelable(false);
        return view;
    }

    /**
     * A method to init listeners
     */
    private void initListeners() {
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.exit(0);
            }
        });
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

    /**
     * A method to init widgets
     */
    private void initWidgets(View view) {
        yesButton = view.findViewById(R.id.quitDialog_TXT_yesText);
        noButton = view.findViewById(R.id.quitDialog_TXT_noText);
    }


}
