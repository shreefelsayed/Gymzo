package com.armjld.gymzo.user.main;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.login.UserInFormation;
import com.google.zxing.WriterException;
import com.squareup.picasso.Picasso;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class UserProfile extends Fragment {

    private String TAG = "User Profile";
    private ImageView btnQR, imgProfile;
    TextView txtName, txtID,txtRemain,txtAllClasses;

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