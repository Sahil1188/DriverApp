package com.osepoo.driverapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.osepoo.driverapp.FirebaseManager.AuthCallback;

public class MainActivity extends AppCompatActivity {

    TextInputEditText editTextEmail, editTextPassword;
    Button signIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
    private static final String EXTRA_DRIVER_ID = "DRIVER_ID";

    // Initialize FirebaseManager
    FirebaseManager firebaseManager = new FirebaseManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        signIn = findViewById(R.id.signin);

        signIn.setOnClickListener(view -> {
            String email = String.valueOf(editTextEmail.getText());
            String password = String.valueOf(editTextPassword.getText());

            if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Enter both email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            // Use FirebaseManager for authentication
            firebaseManager.signInWithEmailAndPassword(email, password, new AuthCallback() {
                @Override
                public void onSuccess(String userId) {
                    // Now you have a unique user ID (userId)
                    // Proceed with storing the user ID and handling driver IDs.

                    // Use FirebaseHelper to store user and driver data
                    FirebaseHelper firebaseHelper = new FirebaseHelper();
                    int driverId = Integer.parseInt(userId); // Assuming user ID and driver ID are the same
                    firebaseHelper.storeUserAndLocationData(userId, driverId, 0.0, 0.0); // Set initial location coordinates

                    Intent intent = new Intent(MainActivity.this, HomePage.class);
                    intent.putExtra(EXTRA_DRIVER_ID, driverId);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void onFailure(Exception e) {
                    Toast.makeText(MainActivity.this, "Login Failed", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}
