package com.omurgun.registerwithfacebook;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

public class RegisterActivity extends AppCompatActivity {

    private CallbackManager callbackManager;
    private LoginButton loginButton;

    private static final String TAG = "FacebookAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG,"onSuccess"+loginResult);
                goHome(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG,"onCancel");
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG,"onError"+error);

            }

        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }
    private void init() {
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        loginButton = findViewById(R.id.login_button);
        loginButton.setReadPermissions("email","public_profile");

    }
    private void goHome(AccessToken accessToken) {
        Intent intentHome = new Intent(RegisterActivity.this, HomeActivity.class);
        intentHome.putExtra("accesstoken", accessToken.getToken());
        startActivity(intentHome);
        finish();
    }

}
