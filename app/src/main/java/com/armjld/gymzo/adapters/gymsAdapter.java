package com.armjld.gymzo.adapters;

import android.annotation.SuppressLint;
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

import com.armjld.gymzo.R;
import com.armjld.gymzo.data.gymData;
import com.armjld.gymzo.datebase.FavoritesManager;
import com.armjld.gymzo.user.main.GymProfile;
import com.squareup.picasso.Picasso;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

public class gymsAdapter extends RecyclerView.Adapter<gymsAdapter.MyViewHolder> {
    static Context mContext;
    ArrayList<gymData> gymsList;
    static FavoritesManager favMang = new FavoritesManager();

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

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    public void onBindViewHolder(@NonNull gymsAdapter.MyViewHolder holder, int position) {
        gymData gDate = gymsList.get(position);
        holder.setMainDate(gDate.getName(), gDate.getAddress(), gDate.getGov() + ", " + gDate.getCity(), gDate.getDistance(), gDate.getType());
        holder.setRating(gDate.getRate());
        holder.setBackGround(gDate.getPhoto());
        holder.setFav(gDate.getGid());

        holder.view.setOnClickListener(v-> {
            GymProfile.gData = gDate;
            mContext.startActivity(new Intent(mContext, GymProfile.class));
        });

        holder.btnFav.setOnClickListener(v-> {
            if(isFav(gDate.getGid())) {
                favMang.remove(gDate.getGid());
                holder.btnFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_star_border_24));

            } else {
                favMang.addGym(gDate.getGid());
                holder.btnFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_gold));
            }
        });
    }

    private boolean isFav(String gid) {
        if(favMang.check(gid)) {
            return true;
        }
        return false;
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
            NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
            DecimalFormat decimalFormat = (DecimalFormat) nf;
            decimalFormat.applyPattern("#.#");
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

        @SuppressLint("UseCompatLoadingForDrawables")
        public void setFav(String gid) {
            if(favMang.check(gid)) {
                btnFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_star_gold));
            } else {
                btnFav.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_baseline_star_border_24));
            }
        }
    }
}
