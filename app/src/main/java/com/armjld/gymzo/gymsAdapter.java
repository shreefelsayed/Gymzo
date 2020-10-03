package com.armjld.gymzo;

import android.content.Context;
import android.content.Intent;
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
import com.armjld.gymzo.user.main.GymProfile;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class gymsAdapter extends RecyclerView.Adapter<gymsAdapter.MyViewHolder> {
    Context mContext;
    ArrayList<gymData> gymsList;

    public gymsAdapter(Context mContext, ArrayList<gymData> gymsList) {
        this.mContext = mContext;
        this.gymsList = gymsList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_gym, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull gymsAdapter.MyViewHolder holder, int position) {
        gymData gDate = gymsList.get(position);
        holder.setMainDate(gDate.getName(), gDate.getAddress(), gDate.getGov() + ", " + gDate.getCity(), gDate.getDistance(), gDate.getType());
        holder.setRating(gDate.getRate());
        holder.setBackGround(gDate.getPhoto());

        holder.view.setOnClickListener(v-> {
            GymProfile.gData = gDate;
            mContext.startActivity(new Intent(mContext, GymProfile.class));
        });
    }

    @Override
    public int getItemCount() {
        return this.gymsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        View view;
        TextView txtTitle,txtAddress,txtCity,txtDistance;
        RatingBar rbGym;
        ImageView btnFav,imgGymFirst,imgBadge;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;
             txtTitle = view.findViewById(R.id.txtTitle);
             txtAddress = view.findViewById(R.id.txtAddress);
             txtCity = view.findViewById(R.id.txtCity);
             rbGym = view.findViewById(R.id.rbGym);
             btnFav = view.findViewById(R.id.btnFav);
             imgGymFirst = view.findViewById(R.id.imgGymFirst);
             txtDistance = view.findViewById(R.id.txtDistance);
             imgBadge = view.findViewById(R.id.imgBadge);
        }

        public void setMainDate(String name, String address, String city, double distance, String Type) {
            txtTitle.setText(name);
            txtAddress.setText(address);
            txtCity.setText(city);
            DecimalFormat decimalFormat = new DecimalFormat("#.#");
            float twoDigitsF = Float.valueOf(decimalFormat.format(distance));
            txtDistance.setText(twoDigitsF + " km away.");

            if(Type.equals("1")){
                imgBadge.setVisibility(View.VISIBLE);
            } else if(Type.equals("2")) {
                imgBadge.setVisibility(View.VISIBLE);
            }else if (Type.equals("3")) {
                imgBadge.setVisibility(View.VISIBLE);
            } else {
                imgBadge.setVisibility(View.GONE);

            }
        }

        public void setRating(String rate) {
            rbGym.setRating(Float.parseFloat(rate));
        }

        public void setBackGround(String photo) {
            Picasso.get().load(Uri.parse(photo)).into(imgGymFirst);
        }
    }
}
