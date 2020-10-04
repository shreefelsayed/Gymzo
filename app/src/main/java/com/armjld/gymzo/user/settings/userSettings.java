package com.armjld.gymzo.user.settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.firstrun.ChooseLanguage;
import com.armjld.gymzo.login.LoginManager;
import com.armjld.gymzo.login.UserInFormation;
import com.google.android.material.slider.Slider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

@SuppressLint("UseSwitchCompatOrMaterialCode")
public class userSettings extends Fragment {

    Button btnAccountInfo,btnChangePass,btnLanguage,btnLogout;
    Switch switchNoti;
    ImageView imgPPP;
    TextView txtName, txtEmail,txtDistance;
    DatabaseReference uDatabase;
    Slider distanceSlider;

    public userSettings() { }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_settings, container, false);

        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        btnAccountInfo = view.findViewById(R.id.btnAccountInfo);
        btnChangePass = view.findViewById(R.id.btnChangePass);
        btnLanguage = view.findViewById(R.id.btnLanguage);
        btnLogout = view.findViewById(R.id.btnLogout);
        switchNoti = view.findViewById(R.id.switchNoti);
        imgPPP = view.findViewById(R.id.imgPPP);
        txtName = view.findViewById(R.id.txtName);
        txtEmail = view.findViewById(R.id.txtEmail);
        distanceSlider = view.findViewById(R.id.distanceSlider);
        txtDistance = view.findViewById(R.id.txtDistance);


        txtName.setText(UserInFormation.getFirstname() + " " + UserInFormation.getLastname());
        txtEmail.setText(UserInFormation.getEmail());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(imgPPP);
        distanceSlider.setValue(Float.parseFloat(UserInFormation.getGymdistance()));
        txtDistance.setText(getResources().getString(R.string.str_max_distance) + " " + UserInFormation.getGymdistance() + " " + getResources().getString(R.string.str_km));


        btnAccountInfo.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), userInfoSettings.class));
        });

        btnChangePass.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), userPassSettings.class));
        });

        btnLanguage.setOnClickListener(v -> {
            startActivity(new Intent(getActivity(), ChooseLanguage.class));
        });

        if(UserInFormation.getRecivenoti().equals("true")) {
            switchNoti.setChecked(true);
        } else {
            switchNoti.setChecked(false);
        }

        switchNoti.setOnCheckedChangeListener((compoundButton, b) -> {
            if(b) {
                UserInFormation.setRecivenoti("true");
                uDatabase.child(UserInFormation.getId()).child("recivenoti").setValue("true");
            } else {
                UserInFormation.setRecivenoti("false");
                uDatabase.child(UserInFormation.getId()).child("recivenoti").setValue("false");
            }
        });

        distanceSlider.addOnSliderTouchListener(new Slider.OnSliderTouchListener() {
            @Override
            public void onStartTrackingTouch(@NonNull Slider slider) {
            }

            @Override
            public void onStopTrackingTouch(@NonNull Slider slider) {
                uDatabase.child(UserInFormation.getId()).child("gymdistance").setValue(slider.getValue());
                UserInFormation.setGymdistance(String.valueOf(slider.getValue()));
                txtDistance.setText(getResources().getString(R.string.str_max_distance) + " " + slider.getValue() + " " + getResources().getString(R.string.str_km));
            }
        });

        btnLogout.setOnClickListener(v-> {
            LoginManager _lgn = new LoginManager();
            _lgn.clearInfo(getActivity());
        });

        return view;
    }
}