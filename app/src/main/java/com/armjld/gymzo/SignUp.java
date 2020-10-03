package com.armjld.gymzo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.airbnb.lottie.LottieAnimationView;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.login.LoginManager;
import com.armjld.gymzo.login.SignIn;
import com.armjld.gymzo.login.UserInFormation;
import com.armjld.gymzo.login.userData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.mukesh.OtpView;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import at.markushi.ui.CircleButton;

public class SignUp extends BaseActivity {

    private String TAG = "Sign Up";
    public static String provider = "Email";
    public static String defultPP = "https://firebasestorage.googleapis.com/v0/b/gymzo-16f7e.appspot.com/o/ppUsers%2Fdefault.jpg?alt=media&token=8e29f25b-f19d-4287-805d-250b6948e4f5";
    public static String newFirstName = "";
    public static String newLastName = "";
    public static String newEmail = "";
    public static String newPass ="";

    public static AuthCredential googleCred;
    public static AuthCredential faceCred;

    EditText txtFirstName, txtLastName, txtEmail, txtPass1, txtPass2, txtPhone;
    TextInputLayout tlFirstName, tlLastName, tlPass1, tlPass2, tlEmail, tlPhone;
    private ImageView imgSetPP;

    OtpView txtCode;

    private ProgressDialog mdialog;
    private ViewFlipper viewFlipper;
    ImageView btnBack;
    SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy/MM/dd", Locale.ENGLISH);
    String acDate = sdf2.format(new Date());

    TextView txtPrivacy;

    public String mVerificationId = "";
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private Bitmap bitmap;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    CircleButton btnMale, btnFemale;

    private static final int READ_EXTERNAL_STORAGE_CODE = 101;
    int TAKE_IMAGE_CODE = 10001;
    String phoneNumb;

    LottieAnimationView animationView;
    String gender = "male";
    TextView txtGenderName, txtGenderAge;
    Button btnNext0, btnNext1;


    @Override
    public void onBackPressed() {
        showPrev();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        viewFlipper = findViewById(R.id.viewFlipper);
        mdialog = new ProgressDialog(this);
        mAuth = FirebaseAuth.getInstance();
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        TextView tbTitle = findViewById(R.id.toolbar_title);
        tbTitle.setText(R.string.str_signup_toolbar);

        btnBack = findViewById(R.id.btnBack);

        txtFirstName= findViewById(R.id.txtFirstName);
        txtLastName = findViewById(R.id.txtLastName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass1 = findViewById(R.id.txtPass1);
        txtPass2 = findViewById(R.id.txtPass2);
        imgSetPP = findViewById(R.id.imgEditPhoto);
        txtPhone = findViewById(R.id.txtPhone);
        tlFirstName = findViewById(R.id.tlFirstName);
        tlLastName = findViewById(R.id.tlLastName);
        tlPass1 = findViewById(R.id.tlPass1);
        tlPass2 = findViewById(R.id.tlPass2);
        tlEmail = findViewById(R.id.tlEmail);
        tlPhone = findViewById(R.id.tlPhone);
        btnNext0 = findViewById(R.id.btnNext0);

        txtCode = findViewById(R.id.txtCode);

        viewFlipper.setDisplayedChild(0);

        Picasso.get().load(Uri.parse(defultPP)).into(imgSetPP);
        txtEmail.setText(newEmail);
        txtLastName.setText(newLastName);
        txtFirstName.setText(newFirstName);

        animationView = findViewById(R.id.lottieAnimationView);
        btnMale = findViewById(R.id.btnMale);
        btnFemale = findViewById(R.id.btnFemale);
        txtGenderName = findViewById(R.id.txtGenderName);
        txtGenderAge = findViewById(R.id.txtGenderAge);
        btnNext1 = findViewById(R.id.btnNext1);


        btnNext0.setOnClickListener(v-> showNext());
        btnNext1.setOnClickListener(v-> showNext());

        btnMale.setOnClickListener(v-> {
            if(!gender.equals("male")) {
                gender = "male";
                btnMale.setColor(R.color.colorAccentLight);
                btnFemale.setColor(Color.parseColor("#ffffff"));
                animationView.setAnimation(R.raw.male);
                startCheckAnimation();
            }
        });

        btnFemale.setOnClickListener(v-> {
            if(!gender.equals("female")) {
                gender = "female";
                btnFemale.setColor(R.color.colorAccentLight);
                btnMale.setColor(Color.parseColor("#ffffff"));
                animationView.setAnimation(R.raw.female);
                startCheckAnimation();
            }
        });

        if(provider.equals("Google") || provider.equals("facebook")) {
            txtEmail.setEnabled(false);
            txtEmail.setFocusable(false);
            txtEmail.setKeyListener(null);
        }

        txtCode.setOtpCompletionListener(otp -> verifyPhoneNumberWithCode(mVerificationId, otp));
        btnBack.setOnClickListener(v-> showPrev());

        //Set PP
        imgSetPP.setOnClickListener(v -> {
            checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE, READ_EXTERNAL_STORAGE_CODE);
            if (ContextCompat.checkSelfPermission(SignUp.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if(intent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(intent, TAKE_IMAGE_CODE);
                }
            }
        });

    }

    private void showPrev() {
        hideKeyboard(this);
        switch (viewFlipper.getDisplayedChild()) {
            case 0 : {
                startActivity(new Intent(this, SignIn.class));
                break;
            }

            case 1 :
            case 2 : {
                viewFlipper.showPrevious();
                break;
            }
        }
    }

    private void showNext() {
        hideKeyboard(this);
        switch (viewFlipper.getDisplayedChild()) {
            case 0: {
                if(!checkText()) {
                    return;
                }
                viewFlipper.showNext();
                startCheckAnimation();
                txtGenderName.setText(txtFirstName.getText().toString() + " " + txtLastName.getText().toString());
                break;
            }

            case 1 : {
                checkForCode();
            }
            case 2: {

                break;
            }
        }
    }

    private boolean checkText() {
        String phone = txtPhone.getText().toString();
        if(txtFirstName.getText().toString().isEmpty()) {
            tlFirstName.setError("الرجاء ادخال الاسم الاول");
            txtFirstName.requestFocus();
            return false;
        }

        if(txtLastName.getText().toString().isEmpty()) {
            tlLastName.setError("الرجاء ادخال الاسم الاخير");
            txtLastName.requestFocus();
            return false;
        }

        if(txtEmail.getText().toString().isEmpty()) {
            tlEmail.setError("الرجاء ادخال البريد الالكتروني");
            txtEmail.requestFocus();
            return false;
        }

        if(txtPhone.getText().toString().isEmpty()) {
            tlPhone.setError("الرجاء ادخال رقم الهاتف");
            txtPhone.requestFocus();
            return false;
        }

        if(phone.charAt(0) == '1' && phone.length() != 10) {
            tlPhone.setError("رقم الهاتف غير صحيح");
            txtPhone.requestFocus();
            return false;
        }

        if(phone.charAt(0) == '0' && phone.length() != 11) {
            tlPhone.setError("رقم الهاتف غير صحيح");
            txtPhone.requestFocus();
            return false;
        }

        if(txtPass1.getText().toString().isEmpty()) {
            tlPass1.setError("الرجاء ادخال كلمه السر");
            txtPass1.requestFocus();
            return false;
        }

        if(txtPass1.getText().toString().length() < 6) {
            tlPass1.setError("الرجاء ادخال كلمه سر من 6 ارقام علي الاقل");
            txtPass1.requestFocus();
            return false;
        }

        if(!txtPass1.getText().toString().equals(txtPass2.getText().toString())) {
            tlPass2.setError("تاكد من تطابق كلمة السر");
            txtPass2.requestFocus();
            return false;
        }
        return true;
    }

    private void checkForCode() {
        if(!checkText()) {
            return;
        }
        String phone = txtPhone.getText().toString();
        newEmail = txtEmail.getText().toString();
        newPass = txtPass1.getText().toString();
        newFirstName = txtFirstName.getText().toString();
        newLastName = txtLastName.getText().toString();

        mdialog.setMessage(String.valueOf(R.string.str_check_phone));
        mdialog.show();

        if(phone.length() == 10) {
            phoneNumb = phone;
        } else {
            phoneNumb = phone.substring(1,11);
        }

        uDatabase.orderByChild("phone").equalTo("0"+phoneNumb).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    Log.i(TAG ,"Phone Number is Already Exist For uID : " + snapshot.getValue().toString());
                    mdialog.dismiss();
                    Toast.makeText(SignUp.this, R.string.str_phone_exist, Toast.LENGTH_SHORT).show();
                } else {
                    Log.i(TAG ,"Phone Number isn't Exist, Let's Continue");
                    mdialog.setMessage(String.valueOf(R.string.str_sending_code));
                    mCallBack();
                    sendCode(phoneNumb);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                mdialog.dismiss();
            }});
    }

    private void sendCode(String uPhone) {
        if(mCallbacks == null) {
            mCallBack();
            Log.i(TAG, "mCallbacks was null");
        }
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+20" + uPhone,
                60,
                TimeUnit.SECONDS,
                this,
                mCallbacks);
    }

    private void verifyPhoneNumberWithCode(String verificationId, String code) {
        Log.i(TAG, "verifyPhoneNumberWithCode : " + verificationId);
        mdialog.setMessage(String.valueOf(R.string.str_checking_code));
        mdialog.show();
        if(verificationId.equals("")) {
            mdialog.dismiss();
            return;
        }

        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        if(provider.equals("Google")) {
            linkGoogle(credential);
        } else if(provider.equals("facebook")) {
            linkFace(credential);
        } else {
            signUp(credential);
        }
    }

    private void linkGoogle(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(googleCred).addOnCompleteListener(SignUp.this, googleSign -> {
            if(googleSign.isSuccessful() && mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(SignUp.this, taskPhone -> {
                    if(taskPhone.isSuccessful()) {
                        AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                        Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(emailCred).addOnCompleteListener(SignUp.this, taskEmail -> {
                            if(taskEmail.isSuccessful()) {
                                setUserData();
                            } else {
                                mdialog.dismiss();
                                Toast.makeText(this, R.string.str_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, R.string.str_wrong_otp, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void linkFace(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(faceCred).addOnCompleteListener(SignUp.this, faceSign -> {
            if(faceSign.isSuccessful() && mAuth.getCurrentUser() != null) {
                mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(SignUp.this, taskPhone -> {
                    if(taskPhone.isSuccessful()) {
                        AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                        mAuth.getCurrentUser().linkWithCredential(emailCred).addOnCompleteListener(SignUp.this, taskEmail -> {
                            if(taskEmail.isSuccessful()) {
                                setUserData();
                            } else {
                                mdialog.dismiss();
                                Toast.makeText(this, R.string.str_error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {
                        if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            Toast.makeText(this, R.string.str_wrong_otp, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }


    private void signUp(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential).addOnCompleteListener(SignUp.this, taskPhone -> {
            if(taskPhone.isSuccessful()) {
                AuthCredential emailCred = EmailAuthProvider.getCredential(newEmail, newPass);
                Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(emailCred).addOnCompleteListener(SignUp.this, taskEmail -> {
                    if(taskEmail.isSuccessful()) {
                        setUserData();
                    } else {
                        Toast.makeText(this, R.string.str_error, Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                if (taskPhone.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(this, R.string.str_wrong_otp, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // --------------------------------- Phone Number Functions -------------------------- //
    private void mCallBack() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                txtCode.setText(credential.getSmsCode());
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                mdialog.dismiss();
                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    Toast.makeText(SignUp.this, R.string.str_wrong_phone, Toast.LENGTH_SHORT).show();
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    Snackbar.make(findViewById(android.R.id.content), R.string.str_error, Snackbar.LENGTH_LONG).show();
                }
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                super.onCodeSent(verificationId, token);
                Toast.makeText(SignUp.this, R.string.str_code_sent, Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
                mVerificationId = verificationId;
                Log.i(TAG, "Code has been sent to : " + phoneNumb + " and Verf id has been set : " + mVerificationId);
                viewFlipper.setDisplayedChild(2);
            }
        };
    }

    private void setUserData() {
        String id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
        String memail = txtEmail.getText().toString().trim().toLowerCase();
        String mpass = txtPass1.getText().toString().trim();
        String mPhone = "0"+phoneNumb;

        userData uDate = new userData(id, newFirstName, newLastName, mpass, memail, mPhone, acDate, "0", defultPP, "", "0");
        uDatabase.child(id).setValue(uDate);

        uDatabase.child(id).child("gender").setValue(gender);
        uDatabase.child(id).child("recivenoti").setValue("true");

        SimpleDateFormat sdf0 = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        String userDate = sdf0.format(new Date());

        DatabaseReference newUser = FirebaseDatabase.getInstance().getReference().child("userByDay").child(userDate).child(id);
        newUser.child("phone").setValue(mPhone);
        newUser.child("name").setValue(newFirstName + " " + newLastName);
        newUser.child("id").setValue(id);
        newUser.child("gymdistance").setValue("10");

        // ------------------ Set Device Token ----------------- //
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(SignUp.this, instanceIdResult -> {
            String deviceToken = instanceIdResult.getToken();
            uDatabase.child(id).child("device_token").setValue(deviceToken);
        });

        if(bitmap != null) {
            handleUpload(bitmap);
        } else {
            uDatabase.child(id).child("userURL").setValue(defultPP);
            mdialog.dismiss();
        }

        LoginManager _lgnMn = new LoginManager();
        _lgnMn.setMyInfo(SignUp.this);

        Toast.makeText(getApplicationContext(), R.string.str_account_created , Toast.LENGTH_LONG).show();
        mdialog.dismiss();
    }

    public static Bitmap resizeBitmap(Bitmap source, int maxLength) {
        try {
            if (source.getHeight() >= source.getWidth()) {
                int targetHeight = maxLength;
                if (source.getHeight() <= targetHeight) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = (double) source.getWidth() / (double) source.getHeight();
                int targetWidth = (int) (targetHeight * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                Log.i("SignUp", "Returned a Resized Photo");
                return result;
            } else {
                int targetWidth = maxLength;
                if (source.getWidth() <= targetWidth) { // if image already smaller than the required height
                    return source;
                }

                double aspectRatio = ((double) source.getHeight()) / ((double) source.getWidth());
                int targetHeight = (int) (targetWidth * aspectRatio);

                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                Log.i("SignUp", "Returned a Resized Photo");
                return result;
            }
        }
        catch (Exception e)
        {
            Log.i("SignUp", "Returned the source Photo");
            return source;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            Uri photoUri = data.getData();
            try {
                Bitmap source = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoUri);
                bitmap = resizeBitmap (source, 500);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Uri uri = null;
            try {
                uri = Uri.parse(getFilePath(SignUp.this, photoUri));
            }
            catch (URISyntaxException e) {
                e.printStackTrace();
            }
            if(uri != null) {
                bitmap = rotateImage(bitmap , uri , photoUri);
            }
            assert uri != null;
            Log.i(TAG,"uri : " + uri.toString());
            imgSetPP.setImageBitmap(bitmap);
        }
    }

    @SuppressLint({"NewApi", "Recycle"})
    public static String getFilePath(Context context, Uri uri) throws URISyntaxException {
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        if (DocumentsContract.isDocumentUri(context.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.parseLong(id));
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
            Cursor cursor;
            try {
                cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs, null);
                assert cursor != null;
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception ignored) {
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
                } else if (orintation == 3) {
                    matrix.postRotate(180);
                } else if (orintation == 8) {
                    matrix.postRotate(270);
                }
                Bitmap rotatedmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getHeight(),matrix,true);
                return rotatedmap;
            }
            else {
                return bitmap;
            }
        }
        else {
            return bitmap;
        }
    }
    private void handleUpload (Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        String uID = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        final StorageReference reference = FirebaseStorage.getInstance().getReference().child("ppUsers").child(uID + ".jpeg");
        final String did = uID;
        reference.putBytes(baos.toByteArray()).addOnSuccessListener(taskSnapshot -> {
            getDownUrl(did, reference);
            Log.i("Sign UP", " onSuccess");
        }).addOnFailureListener(e -> Log.e("Upload Error: ", "Fail:", e.getCause()));
        Log.i("Sign UP", " Handel Upload");
    }

    private void getDownUrl(final String uIDd, StorageReference reference) {
        reference.getDownloadUrl().addOnSuccessListener(uri -> {
            Log.i("Sign UP", " add Profile URL");
            uDatabase.child(uIDd).child("userURL").setValue(uri.toString());
            UserInFormation.setUserURL(uri.toString());
            mdialog.dismiss();
        });
    }

    // ------------------- CHEECK FOR PERMISSIONS -------------------------------//
    public void checkPermission(String permission, int requestCode) {
        if (ContextCompat.checkSelfPermission(SignUp.this, permission) == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(SignUp.this, new String[] { permission }, requestCode);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == READ_EXTERNAL_STORAGE_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(SignUp.this, "Storage Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SignUp.this, "Storage Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm= (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = activity.getCurrentFocus();
        if(view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void clearTexts() {
        if(provider.equals("Email")) {
            txtFirstName.setText("");
            txtEmail.setText("");
            txtLastName.setText("");
        }
        txtPhone.setText("");
        txtPass1.setText("");
        txtPass2.setText("");
    }

    private void startCheckAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, 1f).setDuration(5000);
        animator.addUpdateListener(valueAnimator -> {
            animationView.setProgress((Float) valueAnimator.getAnimatedValue());
        });

        if (animationView.getProgress() == 0f) {
            animator.start();
        } else {
            animationView.setProgress(0f);
        }
    }
}