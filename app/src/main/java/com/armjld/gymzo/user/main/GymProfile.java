package com.armjld.gymzo.user.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.SliderAdapterExample;
import com.armjld.gymzo.gym.gymData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GymProfile extends AppCompatActivity {

    public static gymData gData;
    SliderView sliderView;
    ArrayList<String> photoList = new ArrayList<>();
    TextView txtTitle,txtAddress,txtCity,txtDistance;
    RatingBar rbGym;
    ImageView btnFav,imgBadge, btnBack;
    DatabaseReference gymDatabase = FirebaseDatabase.getInstance().getReference().child("gyms");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gym_profile);

        sliderView = findViewById(R.id.imageSlider);
        txtTitle = findViewById(R.id.txtTitle);
        txtAddress = findViewById(R.id.txtAddress);
        txtCity = findViewById(R.id.txtCity);
        rbGym = findViewById(R.id.rbGym);
        //btnFav = findViewById(R.id.btnFav);
        txtDistance = findViewById(R.id.txtDistance);
        imgBadge = findViewById(R.id.imgBadge);
        btnBack = findViewById(R.id.btnBack);

        btnBack.setOnClickListener(v-> finish());

        setGymData();
        setGymPhotos();

    }

    private void setGymPhotos() {
        photoList.add(gData.getPhoto());
        gymDatabase.child(gData.getGid()).child("photos").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot photos:snapshot.getChildren()) {
                        photoList.add(Objects.requireNonNull(photos.getValue()).toString());
                    }
                }
                sliderView.setSliderAdapter(new SliderAdapterExample(GymProfile.this, photoList));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void setGymData() {
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText(gData.getName());
        txtTitle.setText(gData.getName());
        txtAddress.setText(gData.getAddress());
        txtCity.setText(gData.getGov() + ", " + gData.getCity());
        rbGym.setRating(Integer.parseInt(gData.getRate()));
        DecimalFormat decimalFormat = new DecimalFormat("#.#");
        float twoDigitsF = Float.valueOf(decimalFormat.format(gData.getDistance()));
        txtDistance.setText(twoDigitsF + " km away.");

        if(gData.getType().equals("1")){
            imgBadge.setVisibility(View.VISIBLE);
        } else if(gData.getType().equals("2")) {
            imgBadge.setVisibility(View.VISIBLE);
        }else if (gData.getType().equals("3")) {
            imgBadge.setVisibility(View.VISIBLE);
        } else {
            imgBadge.setVisibility(View.GONE);
        }
    }
}