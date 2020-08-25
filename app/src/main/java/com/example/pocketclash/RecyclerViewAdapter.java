package com.example.pocketclash;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.Inflater;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    Context context;
    ArrayList<Score> scores;

    public RecyclerViewAdapter(Context context, ArrayList<Score> scores) {
        this.context = context;
        this.scores = scores;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recyclerview_row, parent, false);
        return new ViewHolder(view);
    }

    /**
     * Updating dynamically the contents of a row in the Recycler view, using given position
     */
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder.score.setText("Score: " + scores.get(position).getNumOfTurns());
        holder.place.setText("" + (position + 1));
        holder.displayLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(view.getContext(), "I will display the location of item " +
                        (position + 1), Toast.LENGTH_SHORT).show();
                displayGivenLocation(scores.get(position).getLocation());

            }
        });
        MyLocation temp = scores.get(position).getLocation();
        holder.location.setText("Lat: " + temp.getLat() + " | Lon: " + temp.getLon());
    }

    /**
     * A method to create a map fragment that will show the score location on the map
     */
    private void displayGivenLocation(MyLocation location) {
        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);
        if (available == ConnectionResult.SUCCESS) {
            Log.d("pttt", "isServiceOK: Google Play Services is working");
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            Log.d("pttt", "isServicesOK: an error occured but we can fix it");
        } else Log.d("pttt", "Cant make map requests");



        MapFragment dialog = new MapFragment(context, location);
        Objects.requireNonNull(dialog.getWindow()).requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.8);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.5);
        dialog.getWindow().setLayout(width, height);
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL);
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }


    /**
     * An inner class to specify each row contents
     */
    public class ViewHolder extends RecyclerView.ViewHolder { // To hold each row

        TextView score, location, place;
        ImageView displayLocation;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        /**
         * A method to initialize the widgets of the row
         */
        private void initViews() {
            displayLocation = itemView.findViewById(R.id.row_IMG_openLocation);

            score = itemView.findViewById(R.id.row_LBL_score);
            place = itemView.findViewById(R.id.row_LBL_position);
            location = itemView.findViewById(R.id.row_LBL_location);
            mainLayout = itemView.findViewById(R.id.row_LAY_mainLayout);
        }
    }
}
