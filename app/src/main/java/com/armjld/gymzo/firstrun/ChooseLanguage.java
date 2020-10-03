package com.armjld.gymzo.firstrun;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.armjld.gymzo.R;
import com.armjld.gymzo.StartUp;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.language.LocaleManager;
import com.armjld.gymzo.login.SignIn;

public class ChooseLanguage extends BaseActivity {

    Button btnArabic, btnEnglish;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_language);

        btnArabic = findViewById(R.id.btnArabic);
        btnEnglish =  findViewById(R.id.btnEnglish);


        btnArabic.setOnClickListener(v-> {
            setNewLocale(this, LocaleManager.ARABIC);
            startActivity(new Intent(this, StartUp.class));
            finish();
        });

        btnEnglish.setOnClickListener(v-> {
            setNewLocale(this, LocaleManager.ENGLISH);
            startActivity(new Intent(this, StartUp.class));
            finish();
        });
    }

    private void setNewLocale(AppCompatActivity mContext, @LocaleManager.LocaleDef String language) {
        LocaleManager.setNewLocale(this, language);
        Intent intent = mContext.getIntent();
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK));
    }
}