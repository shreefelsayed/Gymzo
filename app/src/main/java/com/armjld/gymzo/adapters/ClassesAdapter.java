package com.armjld.gymzo.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.data.ClassData;
import com.armjld.gymzo.data.UserInFormation;
import com.armjld.gymzo.data.gymData;
import com.armjld.gymzo.datebase.RatingManager;
import com.armjld.gymzo.local.caculateTime;
import com.armjld.gymzo.user.main.GymProfile;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.MyViewHolder>{

    Context mContext;
    ArrayList<ClassData> classList;
    caculateTime _caculateTime = new caculateTime();

    public ClassesAdapter(Context mContext, ArrayList<ClassData> classList) {
        this.mContext = mContext;
        this.classList = classList;
    }

    @NonNull
    @Override
    public ClassesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.card_history, parent, false);
        return new ClassesAdapter.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClassesAdapter.MyViewHolder holder, int position) {
        ClassData cDate = classList.get(position);

        holder.txtName.setText(cDate.getGymname());
        holder.txtTime.setText(_caculateTime.setPostDate(cDate.getDatee()));
        Picasso.get().load(Uri.parse(cDate.getGymurl())).into(holder.imgUser);

        if(UserInFormation.getRates().contains(cDate.getGid())) {
            holder.btnRate.setVisibility(View.GONE);
        } else {
            holder.btnRate.setVisibility(View.VISIBLE);
        }

        holder.view.setOnClickListener(v-> FirebaseDatabase.getInstance().getReference().child("gyms").child(cDate.getGid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                GymProfile.gData = snapshot.getValue(gymData.class);
                mContext.startActivity(new Intent(mContext, GymProfile.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        }));

        
        holder.btnRate.setOnClickListener(v-> {
            AlertDialog.Builder myRate = new AlertDialog.Builder(mContext);
            LayoutInflater inflater = LayoutInflater.from(mContext);
            final View dialogRate = inflater.inflate(R.layout.dialograte, null);
            myRate.setView(dialogRate);
            final AlertDialog dialog = myRate.create();
            dialog.show();

            TextView tbTitle = dialogRate.findViewById(R.id.toolbar_title);
            tbTitle.setText("تقييم المندوب");

            ImageView btnClose = dialogRate.findViewById(R.id.btnClose);

            btnClose.setOnClickListener(v14 -> dialog.dismiss());

            Button btnSaveRate = dialogRate.findViewById(R.id.btnSaveRate);
            final EditText txtRate = dialogRate.findViewById(R.id.drComment);
            final RatingBar drStar = dialogRate.findViewById(R.id.drStar);

            // -------------- Make suer that the minmum rate is 1 star --------------------//
            drStar.setOnRatingBarChangeListener((drStar1, rating, fromUser) -> {
                if(rating<1.0f) {
                    drStar1.setRating(1.0f);
                }
            });

            btnSaveRate.setOnClickListener(v1-> {
                RatingManager ratingManager = new RatingManager();
                ratingManager.setComment(cDate.getGid(), (int) drStar.getRating(), txtRate.getText().toString(), cDate.getClassid());
                holder.btnRate.setVisibility(View.GONE);
                dialog.dismiss();
            });
        });

    }

    @Override
    public int getItemCount() { return this.classList.size(); }

    @Override
    public int getItemViewType(int position) { return position; }

    public static class MyViewHolder extends RecyclerView.ViewHolder{
        View view;

        ImageView imgUser;
        TextView txtName,txtTime;
        Button btnRate;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView;

            imgUser = view.findViewById(R.id.imgUser);
            txtName = view.findViewById(R.id.txtName);
            txtTime = view.findViewById(R.id.txtTime);
            btnRate = view.findViewById(R.id.btnRate);


        }

    }
}
