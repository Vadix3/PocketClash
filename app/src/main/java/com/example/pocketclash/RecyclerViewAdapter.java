package com.example.pocketclash;

import android.app.Activity;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;
import java.util.Objects;
import java.util.zip.Inflater;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Score> scores;
    public static final String TAG = "pttt";
    private static final int ERROR_DIALOG_REQUEST = 9001;


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
                MyLocation temp = scores.get(position).getLocation();
                if (temp.getLon() == 0 && temp.getLat() == 0) {
                    Toast.makeText(context, "Location is off!", Toast.LENGTH_SHORT).show();
                } else if (isServicesOK()) {
                    initMap(scores.get(position).getLocation());
                }
            }
        });
        MyLocation temp = scores.get(position).getLocation();
        holder.location.setText("Lat: " + temp.getLat() + " | Lon: " + temp.getLon());
    }

    /**
     * A method to initialize the map fragment
     */
    private void initMap(MyLocation location) {
        Log.d(TAG, "initMap: Trying to init map adapter");
        MapsActivity dialog = new MapsActivity(context, location);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.activity_maps);
        dialog.show();
        dialog.getWindow().setDimAmount(0.8f);
        int width = (int) (context.getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = (int) (context.getResources().getDisplayMetrics().heightPixels * 0.7);
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

    /**
     * A method to check if google play services are running fine
     */
    public boolean isServicesOK() {
        Log.d(TAG, "isServicesOK: checking google services version");

        int available = GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(context);

        if (available == ConnectionResult.SUCCESS) {
            //Everything is cool and user can make map requests
            Log.d(TAG, "isServicesOK: Google play services is working");
            return true;
        } else if (GoogleApiAvailability.getInstance().isUserResolvableError(available)) {
            //An error occurred but we can resolve it
            Log.d(TAG, "isServicesOK: An error occured but we can fix it");
            Dialog dialog = GoogleApiAvailability.getInstance().getErrorDialog((Activity) context, available, ERROR_DIALOG_REQUEST);
            dialog.show();
        } else {
            Toast.makeText(context, "You cant make map requests", Toast.LENGTH_SHORT).show();
        }
        return false;
    }
}
