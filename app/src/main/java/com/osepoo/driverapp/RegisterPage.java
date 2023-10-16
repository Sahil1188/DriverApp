package com.osepoo.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterPage extends AppCompatActivity {
    TextInputEditText editTextUserId, editTextPassword;
    Button signUp;
    TextView signIn;
    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_page);
        getSupportActionBar().hide();
        editTextUserId = findViewById(R.id.userId);
        editTextPassword = findViewById(R.id.password1);
        signIn = findViewById(R.id.signin);
        signUp = findViewById(R.id.signup1);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                startActivity(intent);
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userId, password;
                userId = String.valueOf(editTextUserId.getText());
                password = String.valueOf(editTextPassword.getText());

                if (TextUtils.isEmpty(userId) || TextUtils.isEmpty(password)) {
                    Toast.makeText(RegisterPage.this, "Enter both User ID and Password", Toast.LENGTH_SHORT).show();
                    return;
                }

                firebaseAuth.createUserWithEmailAndPassword(userId, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // Registration successful
                                    Log.d("Registration", "Registration successful for user: " + userId);

                                    // Additional logic if needed

                                    Toast.makeText(RegisterPage.this, "Registration Successfully.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(RegisterPage.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Registration failed, display an error message
                                    Log.e("Registration", "Registration failed: " + task.getException());
                                    Toast.makeText(RegisterPage.this, "Registration Failed.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }
}
