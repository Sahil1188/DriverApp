package com.osepoo.driverapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private TextInputEditText emailEditText;
    private Button resetPasswordButton , btnback;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        firebaseAuth = FirebaseAuth.getInstance();

        emailEditText = findViewById(R.id.forgot_email);
        resetPasswordButton = findViewById(R.id.reset_password_button);
        btnback= findViewById(R.id.btn_Back);
        btnback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ForgotPasswordActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Retrieve the entered email address
                String email = emailEditText.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    // Ensure the email field is filled
                    Toast.makeText(ForgotPasswordActivity.this, "Enter your email address",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Send a password reset email to the provided email address
                    firebaseAuth.sendPasswordResetEmail(email)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        // Password reset email sent successfully
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                "Password reset email sent. Check your email inbox.",
                                                Toast.LENGTH_SHORT).show();
                                    } else {
                                        // Failed to send password reset email
                                        Toast.makeText(ForgotPasswordActivity.this,
                                                "Failed to send password reset email. Check the email address.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}
