package com.osepoo.driverapp;

import android.location.Location;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import java.util.Locale;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";

    // Firebase authentication instance
    private final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Firebase Realtime Database instance
    private final FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    // Sign in with email and password
    public void signInWithEmailAndPassword(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = currentUser.getUid();
                            callback.onSuccess(userId);
                        }
                    } else {
                        Log.e(TAG, "signInWithEmailAndPassword: Failed", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Update user location in Firebase
    public void updateUserLocation(String userId, Location location) {
        firebaseDatabase.getReference("users").child(userId).setValue(location);
    }

    // Callback interface for authentication
    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(Exception e);
    }
}
