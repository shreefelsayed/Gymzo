package com.armjld.gymzo.user.main;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.adapters.RatingAdapter;
import com.armjld.gymzo.data.RatingData;
import com.armjld.gymzo.SliderAdapterExample;
import com.armjld.gymzo.data.gymData;
import com.armjld.gymzo.language.BaseActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.smarteist.autoimageslider.SliderView;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

public class GymProfile extends BaseActivity {

    public static gymData gData;
    SliderView sliderView;
    ArrayList<String> photoList = new ArrayList<>();
    TextView txtTitle,txtAddress,txtCity,txtDistance,txtFavCount;
    RatingBar rbGym;
    ImageView btnFav,imgBadge, btnBack;
    DatabaseReference gymDatabase = FirebaseDatabase.getInstance().getReference().child("gyms");
    LinearLayout linerExpand;
    RecyclerView listComments;
    private ArrayList<String> mArraylistSectionLessons = new ArrayList<>();
    int comments = 0;
    TextView txtCommentsCount;
    ImageButton btnExpand;
    LinearLayout cardView;

    RatingAdapter ratingAdapter;

    @SuppressLint("UseCompatLoadingForDrawables")
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
        txtFavCount = findViewById(R.id.txtFavCount);
        txtCommentsCount = findViewById(R.id.txtCommentsCount);
        btnExpand = findViewById(R.id.btnExpand);
        cardView = findViewById(R.id.comments);
        listComments = findViewById(R.id.listComments);
        linerExpand = findViewById(R.id.linerExpand);

        listComments.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        listComments.setLayoutManager(layoutManager);

        btnBack.setOnClickListener(v-> finish());

        btnExpand.setOnClickListener(v-> {
            showComments();
        });
        linerExpand.setOnClickListener(v-> {
            showComments();
        });

        setGymData();
        setGymPhotos();
        getRatings();
    }

    private void showComments() {
        if(listComments.getVisibility() == View.GONE) {
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            listComments.setVisibility(View.VISIBLE);
            btnExpand.setImageDrawable(getResources().getDrawable(R.drawable.ic_up));
        } else {
            TransitionManager.beginDelayedTransition(cardView, new AutoTransition());
            listComments.setVisibility(View.GONE);
            btnExpand.setImageDrawable(getResources().getDrawable(R.drawable.ic_down));
        }
    }

    private void getRatings() {
        listComments.setAdapter(null);
        ArrayList<RatingData> rateList = new ArrayList<>();
        gymDatabase.child(gData.getGid()).child("comments").orderByChild("timestamp").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    for (DataSnapshot cData : dataSnapshot.getChildren()) {
                        RatingData ratingData = cData.getValue(RatingData.class);
                        assert ratingData != null;

                        String tempComment = ratingData.getComment();
                        if(!tempComment.equals("")) {
                            rateList.add(ratingData);
                            comments ++;
                        }
                    }
                    txtCommentsCount.setText(String.valueOf(comments));
                    ratingAdapter = new RatingAdapter(GymProfile.this, rateList);
                    listComments.setAdapter(ratingAdapter);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });
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
        NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
        DecimalFormat decimalFormat = (DecimalFormat) nf;
        decimalFormat.applyPattern("#.#");
        float twoDigitsF = Float.valueOf(decimalFormat.format(gData.getDistance()));
        txtDistance.setText(twoDigitsF + " km away.");
        txtFavCount.setText(gData.getFavcount());

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

    /*
            */
}