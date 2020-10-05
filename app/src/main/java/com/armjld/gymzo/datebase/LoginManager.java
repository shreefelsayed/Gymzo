package com.armjld.gymzo.datebase;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.annotation.NonNull;

import com.armjld.gymzo.data.UserInFormation;
import com.armjld.gymzo.datebase.FavoritesManager;
import com.armjld.gymzo.login.SignIn;
import com.armjld.gymzo.user.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import java.util.Objects;

public class LoginManager {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    DatabaseReference uDatabase = FirebaseDatabase.getInstance().getReference().child("users");
    public static boolean dataset = false;

    public void setMyInfo(Context mContext) {
        uDatabase.child(Objects.requireNonNull(mAuth.getCurrentUser()).getUid()).keepSynced(true);
        uDatabase.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) mContext, instanceIdResult -> {
                        String deviceToken = instanceIdResult.getToken();
                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("device_token").setValue(deviceToken);
                    });

                    UserInFormation.setId(Objects.requireNonNull(mAuth.getCurrentUser()).getUid());
                    UserInFormation.setFirstname(Objects.requireNonNull(snapshot.child("firstname").getValue()).toString());
                    UserInFormation.setLastname(Objects.requireNonNull(snapshot.child("lastname").getValue()).toString());
                    UserInFormation.setEmail(Objects.requireNonNull(snapshot.child("email").getValue()).toString());
                    UserInFormation.setPass(Objects.requireNonNull(snapshot.child("pass").getValue()).toString());
                    UserInFormation.setuPhone(Objects.requireNonNull(snapshot.child("uPhone").getValue()).toString());
                    UserInFormation.setUserDate(Objects.requireNonNull(snapshot.child("userDate").getValue()).toString());
                    UserInFormation.setUserURL(Objects.requireNonNull(snapshot.child("userURL").getValue()).toString());
                    UserInFormation.setSupType(Objects.requireNonNull(snapshot.child("supType").getValue()).toString());
                    UserInFormation.setSupDate(Objects.requireNonNull(snapshot.child("supDate").getValue()).toString());
                    UserInFormation.setRemainClasses(Objects.requireNonNull(snapshot.child("remainClasses").getValue()).toString());

                    if(snapshot.child("gymdistance").exists()) {
                        UserInFormation.setGymdistance(Objects.requireNonNull(snapshot.child("gymdistance").getValue()).toString());
                    }

                    if(snapshot.child("birthdate").exists()) {
                        UserInFormation.setBirthdate(Objects.requireNonNull(snapshot.child("birthdate").getValue()).toString());
                    }

                    if(snapshot.child("gender").exists()) {
                        UserInFormation.setGender(Objects.requireNonNull(snapshot.child("gender").getValue()).toString());
                    }

                    if(snapshot.child("recivenoti").exists()) {
                        UserInFormation.setRecivenoti(Objects.requireNonNull(snapshot.child("recivenoti").getValue()).toString());
                    }

                    if(snapshot.child("classes").exists()) {
                        for(DataSnapshot ds : snapshot.child("classes").getChildren()) {
                            UserInFormation.getClasses().add(ds.getValue().toString());
                        }
                    }

                    if(snapshot.child("favorites").exists()) {
                        for(DataSnapshot ds : snapshot.child("favorites").getChildren()) {
                            FavoritesManager _fav = new FavoritesManager();
                            _fav.add(ds.getValue().toString());
                        }
                    }

                    dataset = true;

                    try {
                        PackageInfo pInfo = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
                        String version = pInfo.versionName;
                        uDatabase.child(mAuth.getCurrentUser().getUid()).child("app_version").setValue(version);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    mContext.startActivity(new Intent(mContext, MainActivity.class));
                } else {
                    clearInfo(mContext);
                    mContext.startActivity(new Intent(mContext, SignIn.class));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

    }

    public void clearInfo(Context mContext) {
        uDatabase.child(Objects.requireNonNull(mAuth.getUid())).child("device_token").setValue("");
        if(SignIn.mGoogleSignInClient != null) {
            SignIn.mGoogleSignInClient.signOut();
        }
        SignIn.disconnectFromFacebook();
        mAuth.signOut();
        ((Activity)mContext).finish();
        mContext.startActivity(new Intent(mContext, SignIn.class));
        UserInFormation.clearUser();
        dataset = false;
        Toast.makeText(mContext, "تم تسجيل الخروج بنجاح", Toast.LENGTH_SHORT).show();
    }

}
