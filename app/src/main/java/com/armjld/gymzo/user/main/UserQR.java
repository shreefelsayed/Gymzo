package com.armjld.gymzo.user.main;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.armjld.gymzo.R;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.login.UserInFormation;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class UserQR extends BaseActivity {

    ImageView btnBack, imgQR;
    public static Bitmap qrBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_q_r);
        btnBack = findViewById(R.id.btnBack);
        imgQR = findViewById(R.id.imgQR);

        //Title Bar
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.str_qrCode);

        QRGEncoder qrgEncoder = new QRGEncoder(UserInFormation.getId(), null, QRGContents.Type.TEXT, 800);
        qrgEncoder.setColorBlack(R.color.colorAccent);
        qrgEncoder.setColorWhite(Color.WHITE);
        qrBitmap = qrgEncoder.getBitmap();
        imgQR.setImageBitmap(qrBitmap);

        btnBack.setOnClickListener(v-> finish());
    }
}