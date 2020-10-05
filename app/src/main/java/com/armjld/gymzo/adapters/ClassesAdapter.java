package com.armjld.gymzo.adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.data.ClassData;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClassesAdapter extends RecyclerView.Adapter<ClassesAdapter.MyViewHolder>{

    Context mContext;
    ArrayList<ClassData> classList;

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
        holder.txtTime.setText(cDate.getDatee());
        Picasso.get().load(Uri.parse(cDate.getGymurl())).into(holder.imgUser);

        if(cDate.getIsrated().equals("true")) {
            holder.btnRate.setVisibility(View.GONE);
        } else {
            holder.btnRate.setVisibility(View.VISIBLE);
        }

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
