package com.armjld.gymzo.user.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.gym.gymData;
import com.armjld.gymzo.gymsAdapter;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.user.FavoritesManager;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class UserFavorites extends BaseActivity {

    RecyclerView favRecycler;
    DatabaseReference gDatabase;
    FavoritesManager favManger = new FavoritesManager();
    ImageView btnBack;
    ArrayList<gymData> favListt = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_favorites);

        gDatabase = FirebaseDatabase.getInstance().getReference().child("gyms");

        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.str_favorites);

        favRecycler = findViewById(R.id.recyclerGym);
        btnBack = findViewById(R.id.btnBack);
        favRecycler = findViewById(R.id.favRecycler);

        favRecycler.setHasFixedSize(true);
        LinearLayoutManager layoutManager= new LinearLayoutManager(this);
        layoutManager.setReverseLayout(true);
        layoutManager.setStackFromEnd(true);
        favRecycler.setLayoutManager(layoutManager);

        btnBack.setOnClickListener(v-> finish());

        getFavs();
    }

    private void getFavs () {
        favListt.clear();
        favRecycler.setAdapter(null);
        for(int i = 0; i < favManger.getfavList().size(); i++) {
            gDatabase.child(favManger.getfavList().get(i)).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    gymData gData = snapshot.getValue(gymData.class);
                    assert gData != null;
                    favListt.add(gData);
                    gymsAdapter gAdapter = new gymsAdapter(UserFavorites.this, favListt);
                    favRecycler.setAdapter(gAdapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }
}