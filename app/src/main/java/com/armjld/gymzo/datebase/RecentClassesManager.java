package com.armjld.gymzo.datebase;

import androidx.annotation.NonNull;

import com.armjld.gymzo.data.ClassData;
import com.armjld.gymzo.data.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class RecentClassesManager {

    private static ArrayList<ClassData> recentClasses = new ArrayList<>();

    public static ArrayList<ClassData> getList() {
        return recentClasses;
    }

    public void getClasses() {
        recentClasses.clear();
        recentClasses.trimToSize();

        ArrayList<String> classesIDs = UserInFormation.getClasses();
        DatabaseReference cDataabase = FirebaseDatabase.getInstance().getReference().child("classes");
        for(int i = 0; i < classesIDs.size(); i++) {
            cDataabase.child(classesIDs.get(i)).limitToLast(30).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    ClassData classData =  snapshot.getValue(ClassData.class);
                    assert classData != null;

                    recentClasses.add(classData);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) { }
            });
        }
    }
}
