package com.armjld.gymzo;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.gymzo.gym.gymData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RatingAdapter extends RecyclerView.Adapter<RatingAdapter.MyViewHolder>{

    static Context mContext;
    ArrayList<RatingData> ratingList;

    public RatingAdapter(Context mContext, ArrayList<RatingData> ratingList) {
        this.mContext = mContext;
        this.ratingList = ratingList;
    }

    @NonNull
    @Override
    public RatingAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_comments, parent, false);
        return new RatingAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RatingAdapter.MyViewHolder holder, int position) {
        RatingData rData = ratingList.get(position);

        holder.txtName.setText(rData.getUname());
        holder.txtComment.setText(rData.getComment());
        holder.rbGymRate.setRating(Float.parseFloat(rData.getRating()));
        Picasso.get().load(Uri.parse(rData.getPpurl())).into(holder.imgUser);

    }

    @Override
    public int getItemCount() { return this.ratingList.size(); }

    @Override
    public int getItemViewType(int position) { return position; }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View view;

        ImageView imgUser;
        TextView txtName,txtComment;
        RatingBar rbGymRate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            imgUser = view.findViewById(R.id.imgUser);
            txtName = view.findViewById(R.id.txtName);
            txtComment = view.findViewById(R.id.txtComment);
            rbGymRate = view.findViewById(R.id.rbGymRate);


        }

    }
}
