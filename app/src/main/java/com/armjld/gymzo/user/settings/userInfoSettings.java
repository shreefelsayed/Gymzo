package com.armjld.gymzo.user.settings;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.armjld.gymzo.R;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.login.UserInFormation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.shreyaspatil.MaterialDialog.MaterialDialog;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;

import static com.armjld.gymzo.SignUp.resizeBitmap;

public class userInfoSettings extends BaseActivity {

    EditText txtFirstName,txtLastName,txtBirthday,txtGender;
    Button btnEditInfo;
    private ImageView UserImage;

    String email;
    int TAKE_IMAGE_CODE = 10001;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private Bitmap bitmap;
    private ProgressDialog mdialog;
    private String ppURL = "";
    String oldPass = "";
    private static String TAG = "User Settings";
    private static final int READ_EXTERNAL_STORAGE_CODE = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info_settings);

        mdialog = new ProgressDialog(this);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");


        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.str_edit_info);
        txtFirstName  = findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtBirthday = findViewById(R.id.txtBirthday);
        txtGender = findViewById(R.id.txtGender);
        btnEditInfo = findViewById(R.id.btnEditInfo);
        UserImage = findViewById(R.id.UserImage);

        UserImage.setOnClickListener(v-> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(this.getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

        btnEditInfo.setOnClickListener(v-> {
            String strFirstname = txtFirstName.getText().toString();
            String strLastname = txtLastName.getText().toString();
            String strBirthdate = txtBirthday.getText().toString();
            String strGender = txtGender.getText().toString();

            if(strFirstname.length() < 3) {

                return;
            }
            if(strLastname.length() < 3) {
                return;
            }

            if(strBirthdate.length() < 2) {
                return;
            }

            if(strGender.length() < 2) {

                return;
            }

            uDatabase.child(UserInFormation.getId()).child("firstname").setValue(strFirstname);
            UserInFormation.setFirstname(strFirstname);

            uDatabase.child(UserInFormation.getId()).child("lastnaame").setValue(strLastname);
            UserInFormation.setLastname(strLastname);

            uDatabase.child(UserInFormation.getId()).child("birthdate").setValue(strBirthdate);
            UserInFormation.setBirthdate(strBirthdate);

            uDatabase.child(UserInFormation.getId()).child("gender").setValue(strGender);
            UserInFormation.setGender(strGender);

            if(bitmap != null) {
                handleUpload(bitmap);
            } else {
                Toast.makeText(this, R.string.str_info_changed, Toast.LENGTH_SHORT).show();
                finish();
            }

        });

        setUserInfo();
    }


    private void setUserInfo() {
        txtFirstName.setText(UserInFormation.getFirstname());
        txtLastName.setText(UserInFormation.getLastname());
        Picasso.get().load(Uri.parse(UserInFormation.getUserURL())).into(UserImage);
        txtGender.setText(UserInFormation.getGender());
        txtBirthday.setText(UserInFormation.getBirthdate());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap(source, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(this, photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri , photoUri);
            }
            assert uri != null;

            UserImage.setImageBitmap(bitmap);
        }
    }

    @SuppressLint("NewApi")
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (Build.VERSION.SDK_INT >= 19 && DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }

    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }


    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    private Bitmap rotateImage(Bitmap bitmap , Uri uri , Uri photoUri){
        ExifInterface exifInterface =null;
        try {
            if(uri==null){
                return bitmap;
            }
            exifInterface = new ExifInterface(String.valueOf(uri));
        }
        catch (IOException e){
            e.printStackTrace();
        }

        if(exifInterface != null) {
            int orintation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION ,ExifInterface.ORIENTATION_UNDEFINED);
            if(orintation == 6 || orintation == 3 || orintation == 8) {
                Matrix matrix = new Matrix();
                if (orintation == 6) {
                    matrix.postRotate(90);
                }
                else if (orintation == 3) {
                    matrix.postRotate(180);
                }
                else if (orintation == 8) {
                    matrix.postRotate(270);
                }
                Bitmap rotatedmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                return rotatedmap;
            } else {
                return bitmap;
            }
        } else {
            return bitmap;
        }

    }

    private void handleUpload (Bitmap bitmap) {
        mdialog.setMessage(String.valueOf(R.string.str_uploading));
        mdialog.show();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(UserInFormation.getId() + ".jpeg");
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> { getDownUrl(reference);
        }).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
    }

    private void getDownUrl(StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            uDatabase.child(UserInFormation.getId()).child("userURL").setValue(uri.toString());
            UserInFormation.setUserURL(uri.toString());
            mdialog.dismiss();
            Toast.makeText(this, R.string.str_info_changed, Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}