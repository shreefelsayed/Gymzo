package com.armjld.gymzo.login;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.armjld.gymzo.language.BaseActivity;
import com.armjld.gymzo.language.LocaleManager;
import com.armjld.gymzo.R;
import com.armjld.gymzo.SignUp;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;
import java.util.Objects;

import at.markushi.ui.CircleButton;

public class SignIn extends BaseActivity {

    private static final int RC_SIGN_IN = 500;
    Button btnLogin;
    CircleButton btnGoogle,btnFacebook;
    boolean doubleBackToExitPressedOnce = false;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;
    private DatabaseReference uDatabase;
    private String TAG = "Login Options";
    private ProgressDialog mdialog;
    public static GoogleSignInClient mGoogleSignInClient;
    boolean isLinked = false;
    boolean fbLink = false;
    String fbEmail;
    EditText txtEmail, txtPass;
    TextInputLayout tlEmail, tlPass;
    ImageView btnLang;
    TextView txtNewAccount;

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
        setContentView(R.layout.activity_sign_in);

        btnGoogle = findViewById(R.id.btnGoogle);
        btnFacebook = findViewById(R.id.btnFacebook);
        btnLogin = findViewById(R.id.btnLogin);
        txtEmail = findViewById(R.id.txtEmail);
        txtPass = findViewById(R.id.txtPass);
        tlEmail = findViewById(R.id.tlEmail);
        tlPass = findViewById(R.id.tlPass);
        txtNewAccount = findViewById(R.id.txtNewAccount);

        btnLang = findViewById(R.id.btnLang);

        mdialog = new ProgressDialog(this);
        uDatabase = FirebaseDatabase.getInstance().getReference().child("users");

        mAuth = FirebaseAuth.getInstance();

        callbackManager = CallbackManager.Factory.create();
        
        FacebookSdk.sdkInitialize(getApplicationContext());
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        // ---------------- implement Google Account ---------------- //
        btnGoogle.setOnClickListener(v -> {
            mGoogleSignInClient.signOut();
            signInGoogle();
        });

        // --------------- implement Facebook Login -----------------//
        btnFacebook.setOnClickListener(v-> {
            connectToFacebook();
        });

        txtNewAccount.setOnClickListener(v-> {
            SignUp.provider = "Email";
            SignUp.newEmail = "";
            SignUp.newFirstName = "";
            SignUp.newLastName = "";
            SignUp.defultPP = "https://firebasestorage.googleapis.com/v0/b/gymzo-16f7e.appspot.com/o/ppUsers%2Fdefault.jpg?alt=media&token=8e29f25b-f19d-4287-805d-250b6948e4f5";
            SignUp.faceCred = null;
            SignUp.googleCred = null;
            startActivity(new Intent(this, SignUp.class));
        });

        // ------------ Login ------------------ //
        btnLogin.setOnClickListener(v -> {
            String memail = txtEmail.getText().toString().trim();
            String mpass = txtPass.getText().toString().trim();
            login(memail, mpass);
        });

        textWatchers();
    }

    private void textWatchers() {
        txtEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tlEmail.setErrorEnabled(false);
            }
        });

        txtPass.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                tlPass.setErrorEnabled(false);
            }
        });
    }

    private void login(String memail, String mpass) {
        if (TextUtils.isEmpty(memail)) {
            tlEmail.setError("يجب ادخال اسم المستخدم");
            txtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(mpass)) {
            tlPass.setError("يجب ادخال كلمه المرور");
            txtPass.requestFocus();
            return;
        }
        mdialog.setMessage("جاري تسجيل الدخول..");
        mdialog.show();

        mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                com.armjld.gymzo.login.LoginManager _lgnMn = new com.armjld.gymzo.login.LoginManager();
                _lgnMn.setMyInfo(SignIn.this);
            } else {
                Toast.makeText(getApplicationContext(), R.string.str_wrong_pass, Toast.LENGTH_LONG).show();
                mdialog.dismiss();
            }
        });
    }

    protected void connectToFacebook() {
        ArrayList<String> fbList = new ArrayList<>();
        fbList.add("email");
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("email", "public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                GraphRequest request = GraphRequest.newMeRequest(loginResult.getAccessToken(), (json, response) -> {
                    // Application code
                    if (response.getError() != null) {

                    } else {
                        String jsonresult = String.valueOf(json);
                        System.out.println("JSON Result" + jsonresult);
                        String fbUserId = json.optString("id");
                        String fbUserFirstName = json.optString("first_name");
                        String fbUserLastName = json.optString("last_name");
                        String fbUserProfilePics = "https://graph.facebook.com/" + fbUserId + "/picture?type=small";
                        fbEmail = json.optString("email");
                        handleFacebookToken(loginResult.getAccessToken(), fbEmail,fbUserFirstName,fbUserLastName,fbUserProfilePics);
                        Log.i(TAG, "First Name : " + fbUserFirstName + " : " + fbUserLastName);
                    }
                    Log.v("FaceBook Response :", response.toString());
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,first_name,last_name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException exception) {
            }
        });
    }

    private void handleFacebookToken(AccessToken accessToken, String fbEmail, String fbUserFirstName, String fbUserLastName, String fbUserProfilePics) {
        AuthCredential credential = FacebookAuthProvider.getCredential(accessToken.getToken());
        uDatabase.orderByChild("email").equalTo(fbEmail).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()) {
                    for(DataSnapshot ds : snapshot.getChildren()) {
                        linkFacebook(credential, ds.child("email").getValue().toString(), ds.child("pass").getValue().toString());
                        break;
                    }
                } else {
                    fbNewAccount(fbEmail,fbUserFirstName,fbUserLastName,fbUserProfilePics, credential);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) { }
        });

    }

    private void linkFacebook(AuthCredential credential, String memail, String mpass) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(UserInfo user: Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData()) {
                    if(user.getProviderId().equals("facebook.com")) {
                        fbLink = true;
                    }
                }
                LoginExistFacebook(credential);
            } else {
                Toast.makeText(this, R.string.str_face_Failed, Toast.LENGTH_SHORT).show();
                mdialog.dismiss();
            }
        });
    }

    private void LoginExistFacebook(AuthCredential credential) {
        if(!fbLink) {
            Log.i(TAG, "Linking with Facebook Credential");
            mAuth.getCurrentUser().linkWithCredential(credential).addOnCompleteListener(this, linkFace -> {
                if(linkFace.isSuccessful()) {
                    Log.i(TAG, "Linked to Facebook Succesfully, Logging in ..");
                    Toast.makeText(this, R.string.str_face_connect, Toast.LENGTH_SHORT).show();
                    letsGo();
                } else {
                    Log.i(TAG, "Linked to Facebook Failed");
                    Toast.makeText(this, R.string.str_face_Failed, Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.i(TAG, "Account is Linked to Facebook, Logging ..");
            letsGo();
        }
        mdialog.dismiss();
    }

    private void fbNewAccount(String fbEmail, String fbFirstName, String fbLastName, String fbPP, AuthCredential credential) {
        SignUp.provider = "facebook";
        SignUp.newEmail = fbEmail;
        SignUp.newFirstName = fbFirstName;
        SignUp.newLastName = fbLastName;
        SignUp.defultPP = fbPP;
        SignUp.faceCred = credential;
        finish();
        startActivity(new Intent(SignIn.this, SignUp.class));
    }

    private void signInGoogle() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                mdialog.setMessage("جاري تسجيل الدخول ..");
                mdialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                uDatabase.orderByChild("email").equalTo(account.getEmail()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(snapshot.exists()) {
                            for(DataSnapshot ds : snapshot.getChildren()) {
                                linkGoogleAccount(credential, ds.child("email").getValue().toString(), ds.child("pass").getValue().toString());
                                break;
                            }
                        } else {
                            SignUp.provider = "Google";
                            SignUp.newEmail = account.getEmail();
                            SignUp.newFirstName = account.getGivenName();
                            SignUp.newLastName = account.getFamilyName();
                            SignUp.defultPP = Objects.requireNonNull(account.getPhotoUrl()).toString();
                            SignUp.googleCred = credential;
                            finish();
                            startActivity(new Intent(SignIn.this, SignUp.class));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) { }
                });


            } catch (ApiException e) {
                mdialog.dismiss();
                Log.w(TAG, "Google sign in failed", e);
            }
        }
    }

    private void linkGoogleAccount(AuthCredential credential, String memail, String mpass) {
        Log.i(TAG, memail + " : " + mpass);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(memail, mpass).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                for(UserInfo user: Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getProviderData()) {
                    if(user.getProviderId().equals("google.com")) {
                        isLinked = true;
                    }
                }
                if(!isLinked) {
                    Objects.requireNonNull(mAuth.getCurrentUser()).linkWithCredential(credential).addOnCompleteListener(this, linkGoogle -> {
                        if(linkGoogle.isSuccessful()) {
                            letsGo();
                        }
                        mdialog.dismiss();
                    });
                    mdialog.dismiss();
                } else {
                    mdialog.dismiss();
                    letsGo();
                }
            } else {
                mdialog.dismiss();
            }
        });
    }

    private void letsGo() {
        com.armjld.gymzo.login.LoginManager _lgnMn = new com.armjld.gymzo.login.LoginManager();
        _lgnMn.setMyInfo(SignIn.this);
    }

    public static void disconnectFromFacebook() {
        if (AccessToken.getCurrentAccessToken() == null) {
            return;
        }
        new GraphRequest(AccessToken.getCurrentAccessToken(), "/me/permissions/", null, HttpMethod.DELETE, graphResponse -> LoginManager.getInstance().logOut()).executeAsync();
    }
}