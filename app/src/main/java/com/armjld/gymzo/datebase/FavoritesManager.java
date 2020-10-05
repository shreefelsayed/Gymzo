package com.armjld.gymzo.datebase;

import androidx.annotation.NonNull;

import com.armjld.gymzo.data.UserInFormation;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class FavoritesManager {

    private static ArrayList<String> favList = new ArrayList<String>();
    private static long num_Blocked_users=0;
    private static Boolean firstTime = false;
    String uId = UserInFormation.getId();


    public static Boolean getFirstTime() {
        return firstTime;
    }

    public static void setFirstTime(Boolean firstTime) {
        FavoritesManager.firstTime = firstTime;
    }

    public static long getNum_Blocked_users() {
        return num_Blocked_users;
    }

    public static void setNum_Blocked_users(long num_Blocked_users) {
        FavoritesManager.num_Blocked_users = num_Blocked_users;
    }

    public ArrayList<String> getfavList() {
        return favList;
    }

    public void clear(){
        favList.clear();
        num_Blocked_users =0;
    }


    public Boolean addGym(String gid){
        final boolean[] f = {false};
        
        String uId = UserInFormation.getId();
        if(uId == null ||uId.isEmpty()){
            return false;
        }

        if(gid == null ||gid.isEmpty()){
            return false;
        }

        favList.add(gid);
        FirebaseDatabase.getInstance().getReference().child("users").child(uId).child("favorites").child(gid).setValue(gid);
        addToGym(gid);
        return true;
    }

    private void addToGym(String gid) {
        DatabaseReference gymFav = FirebaseDatabase.getInstance().getReference().child("gyms").child(gid).child("favcount");
        gymFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String favCount = snapshot.getValue().toString();
                    String newCount = String.valueOf(Integer.parseInt(favCount) + 1);
                    gymFav.setValue(newCount);
                } else {
                    gymFav.setValue("1");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    private void removeFromGym(String gid) {
        DatabaseReference gymFav = FirebaseDatabase.getInstance().getReference().child("gyms").child(gid).child("favcount");
        gymFav.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    String favCount = snapshot.getValue().toString();
                    String newCount = String.valueOf(Integer.parseInt(favCount) - 1);
                    gymFav.setValue(newCount);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }

    public void add(String ids){
        favList.add(ids);
    }

    public boolean remove(String ids) {
        FirebaseDatabase.getInstance().getReference().child("users").child(uId).child("favorites").child(ids).removeValue();
        importFav();
        removeFromGym(ids);
        return true;
    }

    public Boolean check(String id){
        for (String ids:favList) {
            if(ids.equals(id)){
                return true;
            }
        }
        return false;
    }

    public void importFav() {
        favList.clear();
        favList.trimToSize();
        FirebaseDatabase.getInstance().getReference().child("users").child(UserInFormation.getId()).child("favorites").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot ds : snapshot.getChildren()){
                        add(ds.getValue().toString());
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });
    }
}
