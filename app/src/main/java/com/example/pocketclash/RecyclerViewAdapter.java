package com.example.pocketclash;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        holder.score.setText("Score: " + scores.get(position).getNumOfTurns());
        MyLocation temp = scores.get(position).getLocation();
        holder.location.setText("Lat: " + temp.getLat() + " | Lon: " + temp.getLon());

        /** Set listener for click*/
        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Hi I will display the options of item number"
                        + position, Toast.LENGTH_SHORT).show();
                /**TODO: Create a new fragment with location visible on the map
                 * Intent intent = new intent(context,LocationFragment.class);
                 * intent.putExtra("Location",scores.get(position).getLocation());
                 * context.startActivity(intent);
                 * */


            }
        });
    }

    @Override
    public int getItemCount() {
        return scores.size();
    }


    /**
     * An inner class to specify each row contents
     */
    public class ViewHolder extends RecyclerView.ViewHolder { // To hold each row

        TextView score, location;
        ImageView options;
        ConstraintLayout mainLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            initViews();
        }

        /**
         * A method to initialize the widgets of the row
         */
        private void initViews() {
            score = itemView.findViewById(R.id.row_LBL_score);
            location = itemView.findViewById(R.id.row_LBL_location);
            mainLayout = itemView.findViewById(R.id.row_LAY_mainLayout);
            options = itemView.findViewById(R.id.row_IMG_options);
        }
    }
}
