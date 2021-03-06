package com.armjld.gymzo.datebase;

import android.util.Log;

import androidx.annotation.NonNull;

import com.armjld.gymzo.data.RatingData;
import com.armjld.gymzo.data.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RatingManager {

    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss", Locale.ENGLISH);
    String datee = sdf.format(new Date());

    DatabaseReference gDatabase = FirebaseDatabase.getInstance().getReference().child("gyms");
    DatabaseReference cDatabase = FirebaseDatabase.getInstance().getReference().child("classes");
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(UserInFormation.getId());

    int rate1, rate2, rate3, rate4, rate5;

    public void setComment(String gymID, int Rating, String comment, String classid) {
        String commentID = gDatabase.child("comments").push().getKey();
        assert commentID != null;

        if(!comment.isEmpty()) {
            RatingData ratingData = new RatingData(comment, UserInFormation.getFirstname(), UserInFormation.getId(), datee, String.valueOf(Rating), UserInFormation.getUserURL(), classid);
            gDatabase.child(gymID).child("comments").child(commentID).setValue(ratingData);
        }

        cDatabase.child(classid).child("israted").setValue("true");
        cDatabase.child(classid).child("rateid").setValue(commentID);
        uDatabase.child("ratedgyms").child(commentID).setValue(gymID);

        setRatingForGym(gymID, Rating);
        UserInFormation.getRates().add(gymID);
    }

    public void setRatingForGym(String gymID, int Rating) {
        rate1 = 0;
        rate2 = 0;
        rate3 = 0;
        rate4 = 0;
        rate5 = 0;

        gDatabase.child(gymID).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                switch (Rating)  {
                    case 1 : {
                        if(snapshot.child("one").exists()) {
                            int _rate1 = Integer.parseInt(snapshot.child("one").getValue().toString());
                            gDatabase.child(gymID).child("rating").child("one").setValue(_rate1+1);
                        } else {
                            gDatabase.child(gymID).child("rating").child("one").setValue(1);
                        }
                        break;
                    }

                    case 2 : {
                        if(snapshot.child("two").exists()) {
                            int _rate2 = Integer.parseInt(snapshot.child("two").getValue().toString());
                            gDatabase.child(gymID).child("rating").child("two").setValue(_rate2+1);
                        } else {
                            gDatabase.child(gymID).child("rating").child("two").setValue(1);
                        }
                        break;
                    }

                    case 3 : {
                        if(snapshot.child("three").exists()) {
                            int _rate3 = Integer.parseInt(snapshot.child("three").getValue().toString());
                            gDatabase.child(gymID).child("rating").child("three").setValue(_rate3+1);
                        } else {
                            gDatabase.child(gymID).child("rating").child("three").setValue(1);
                        }
                        break;
                    }

                    case 4 : {
                        if(snapshot.child("four").exists()) {
                            int _rate4 = Integer.parseInt(snapshot.child("four").getValue().toString());
                            gDatabase.child(gymID).child("rating").child("four").setValue(_rate4+1);
                        } else {
                            gDatabase.child(gymID).child("rating").child("four").setValue(1);
                        }
                        break;
                    }

                    case 5 : {
                        if(snapshot.child("five").exists()) {
                            int _rate5 = Integer.parseInt(Objects.requireNonNull(snapshot.child("five").getValue()).toString());
                            gDatabase.child(gymID).child("rating").child("five").setValue(_rate5+1);
                        } else {
                            gDatabase.child(gymID).child("rating").child("five").setValue(1);
                        }
                        break;
                    }
                }

                setGymRatings(gymID);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void setGymRatings(String gId) {
        gDatabase.child(gId).child("rating").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String one = "0";
                String two = "0";
                String three = "0";
                String four = "0";
                String five = "0";
                if(snapshot.child("one").exists()) {
                    one = snapshot.child("one").getValue().toString();
                }
                if(snapshot.child("two").exists()) {
                    two = snapshot.child("two").getValue().toString();
                }
                if(snapshot.child("three").exists()) {
                    three = snapshot.child("three").getValue().toString();
                }
                if(snapshot.child("four").exists()) {
                    four = snapshot.child("four").getValue().toString();
                }
                if(snapshot.child("five").exists()) {
                    five = snapshot.child("five").getValue().toString();
                }

                gDatabase.child(gId).child("rate").setValue(String.valueOf(calculateRating(one,two,three,four,five)));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public int calculateRating(String R1, String R2, String R3, String R4, String R5) {
        int intR1 = Integer.parseInt(R1);
        int intR2 = Integer.parseInt(R2);
        int intR3 = Integer.parseInt(R3);
        int intR4 = Integer.parseInt(R4);
        int intR5 = Integer.parseInt(R5);

        int TotalCount = intR1 + intR2 + intR3 + intR4 + intR5;
        if(TotalCount == 0) {
            TotalCount = 1;
        }

        int Rating5 = 5 * intR5;
        int Rating4 = 4 * intR4;
        int Rating3 = 3 * intR3;
        int Rating2 = 2 * intR2;

        int FinalRate = ((intR1 + Rating2 + Rating3 + Rating4 + Rating5) / TotalCount);

        if(FinalRate == 0) {
            FinalRate = 5;
        }

        return FinalRate;
    }
}
