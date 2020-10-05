package com.armjld.gymzo.user.main;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.armjld.gymzo.R;
import com.armjld.gymzo.gym.GymsArea;
import com.armjld.gymzo.adapters.gymsAdapter;
import com.armjld.gymzo.data.UserInFormation;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Gyms extends Fragment {
    ImageView btnMap,btnFavorites;
    static TextView txtNone;
    DatabaseReference gymDatabase;
    static RecyclerView recyclerGym;
    static gymsAdapter gAdapter;
    SwipeRefreshLayout refresh;

    public Gyms() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gyms, container, false);

        if(Integer.parseInt(UserInFormation.getRemainClasses()) == 0) {
            LinearLayout payment_card = view.findViewById(R.id.payment_card);
            payment_card.setVisibility(View.VISIBLE);
        }

        gymDatabase = FirebaseDatabase.getInstance().getReference().child("gyms");
        btnMap = view.findViewById(R.id.btnMap);
        recyclerGym = view.findViewById(R.id.recyclerGym);
        refresh = view.findViewById(R.id.refresh);
        btnFavorites =view.findViewById(R.id.btnFavorites);

        btnFavorites.setOnClickListener(v-> startActivity(new Intent(getActivity(), UserFavorites.class)));

        recyclerGym.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerGym.setLayoutManager(layoutManager);
        recyclerGym.setAdapter(null);

        txtNone = view.findViewById(R.id.txtNone);
        Button btnPay = view.findViewById(R.id.btnPay);

        TextView tbTitle = view.findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.str_available_gyms);

        btnMap.setOnClickListener(v-> {
            startActivity(new Intent(getActivity(), GymsMap.class));
        });

        refresh.setOnRefreshListener(() -> {
            refresh.setRefreshing(true);
            recyclerGym.setAdapter(null);
            GymsArea.getGyms(getActivity());
            refresh.setRefreshing(false);
        });

        return view;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onStart() {
        super.onStart();
        pobulateRecycler(getActivity());
    }

    public static void pobulateRecycler(Context mContext) {
        if(GymsArea.gymsList.size() > 0) {
            txtNone.setVisibility(View.GONE);
            gAdapter = new gymsAdapter(mContext, GymsArea.gymsList);
            recyclerGym.setAdapter(gAdapter);
        } else {
            txtNone.setVisibility(View.VISIBLE);
        }
    }

}