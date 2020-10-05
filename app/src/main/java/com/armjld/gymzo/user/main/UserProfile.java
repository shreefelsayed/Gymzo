package com.armjld.gymzo.user.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.adapters.ClassesAdapter;
import com.armjld.gymzo.data.UserInFormation;
import com.armjld.gymzo.datebase.ClassesManager;
import com.armjld.gymzo.datebase.RecentClassesManager;
import com.squareup.picasso.Picasso;

public class UserProfile extends Fragment {

    private String TAG = "User Profile";
    private ImageView btnQR, imgProfile;
    TextView txtName, txtID,txtRemain,txtAllClasses;
    RecyclerView recyclerClasses;
    ClassesAdapter classesAdapter;
    TextView txtEmpty;

    public UserProfile() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_profile, container, false);
        btnQR = view.findViewById(R.id.btnQR);
        imgProfile= view.findViewById(R.id.imgProfile);
        txtName= view.findViewById(R.id.txtName);
        txtID= view.findViewById(R.id.txtID);
        txtRemain= view.findViewById(R.id.txtRemain);
        txtAllClasses= view.findViewById(R.id.txtAllClasses);
        recyclerClasses = view.findViewById(R.id.recyclerClasses);
        txtEmpty = view.findViewById(R.id.txtEmpty);

        if(RecentClassesManager.getList().size() != 0) {
            txtEmpty.setVisibility(View.GONE);
        }
        recyclerClasses.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(getActivity());
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        recyclerClasses.setLayoutManager(layoutManager);
        recyclerClasses.setAdapter(null);
        classesAdapter = new ClassesAdapter(getActivity(), RecentClassesManager.getList());
        recyclerClasses.setAdapter(classesAdapter);

        btnQR.setOnClickListener(v-> {
            startActivity(new Intent(getActivity(), UserQR.class));
        });

        setUserData();
        return view;
    }

    private void setUserData() {
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgProfile);
        txtName.setText(UserInFormation.getFirstname() + " " + UserInFormation.getLastname());
        txtID.setText("#"+UserInFormation.getId());
        txtRemain.setText(UserInFormation.getRemainClasses() + " of 30 classes remain.");
    }
}