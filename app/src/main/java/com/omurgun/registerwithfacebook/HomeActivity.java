package com.omurgun.registerwithfacebook;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.squareup.picasso.Picasso;

public class HomeActivity extends AppCompatActivity {

    private TextView txtUsername;
    private ImageView imageViewProfile;
    private FirebaseUser firebaseUser;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private AccessTokenTracker accessTokenTracker;
    private String getName;
    private static final String TAG = "FacebookAuthentication";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        if(!checkCurrentUser()) {
            getName = getIntent().getExtras().getString("accesstoken");
            handleFacebookToken(getName);
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();

                if(firebaseUser!=null)
                {
                    updateUI(firebaseUser);
                }
                else
                {
                    updateUI(null);
                }
            }
        };


        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                if(currentAccessToken == null) {
                    firebaseAuth.signOut();
                    goRegister();
                }
            }
        };
    }
    private void handleFacebookToken(String accessToken) {
        Log.d(TAG,"handleFacebookToken"+accessToken);
        AuthCredential authCredential = FacebookAuthProvider.getCredential(accessToken);

        firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful())
                {
                    Log.d(TAG,"sign in with credential : successful");
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    updateUI(user);
                }
                else
                {
                    Log.d(TAG,"sign in with credential : failture"+task.getException());
                    Toast.makeText(HomeActivity.this, "Login Failed!", Toast.LENGTH_LONG).show();
                    updateUI(null);

                }
            }
        });
    }

    private void updateUI(FirebaseUser firebaseUser) {
        if(firebaseUser != null)
        {
            txtUsername.setText(firebaseUser.getDisplayName());
            if(firebaseUser.getPhotoUrl() != null)
            {
                String photoUrl = firebaseUser.getPhotoUrl().toString();
                photoUrl = photoUrl +"?type=large";
                Picasso.get().load(photoUrl).into(imageViewProfile);
            }
        }

    }
    private boolean checkCurrentUser() {

        boolean result = false;
        if(firebaseUser!= null)
        {
            result =true;
        }
        return result;
    }
    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(authStateListener != null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    private void init() {
        txtUsername = findViewById(R.id.username);
        imageViewProfile = findViewById(R.id.imageViewProfile);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();


    }
    private void goRegister() {
        Intent intentHome = new Intent(HomeActivity.this, RegisterActivity.class);
        startActivity(intentHome);
        finish();
    }
}


