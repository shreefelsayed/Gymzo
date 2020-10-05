package com.armjld.gymzo;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.armjld.gymzo.firstrun.ChooseLanguage;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.datebase.LoginManager;
import com.armjld.gymzo.login.SignIn;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Timer;
import java.util.TimerTask;

public class StartUp extends BaseActivity {

    SharedPreferences sharedPreferences = null;
    private FirebaseAuth mAuth;
    private String TAG = "StartUp";
    static DatabaseReference uDatabase;
    boolean doubleBackToExitPressedOnce = false;

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finishAffinity();
            System.exit(0);
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, R.string.str_press_again, Toast.LENGTH_SHORT).show();
        new Handler().postDelayed(() -> doubleBackToExitPressedOnce=false, 2000);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_up);
        FirebaseApp.initializeApp(this);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        whatToDo();
    }

    private void whatToDo() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                sharedPreferences = getSharedPreferences("com.armjld.eb3tly", MODE_PRIVATE);
                if(FirebaseAuth.getInstance().getCurrentUser() != null) {
                    LoginManager _lgnMn = new LoginManager();
                    _lgnMn.setMyInfo(StartUp.this);
                } else {
                    if(sharedPreferences.getBoolean("firstrun", true)) {
                        sharedPreferences.edit().putBoolean("firstrun", false).apply();
                        startActivity(new Intent(StartUp.this, ChooseLanguage.class));
                    } else {
                        startActivity(new Intent(StartUp.this, SignIn.class));
                    }

                }
            }
        }, 1500);
    }
}