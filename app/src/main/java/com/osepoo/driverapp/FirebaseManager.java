package com.osepoo.driverapp;
import android.util.Log;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseManager {
    private static final String TAG = "FirebaseManager";

    // Firebase authentication instance
    private FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();

    // Counter for generating sequential user and driver IDs
    private int idCounter = 0;

    // Sign in with email and password
    public void signInWithEmailAndPassword(String email, String password, AuthCallback callback) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                        if (currentUser != null) {
                            String userId = generateId();
                            callback.onSuccess(userId);
                        }
                    } else {
                        Log.e(TAG, "signInWithEmailAndPassword: Failed", task.getException());
                        callback.onFailure(task.getException());
                    }
                });
    }

    // Generate sequential user and driver ID
    private String generateId() {
        String formattedId = String.format("%04d", idCounter);
        idCounter++; // Increment counter for the next user and driver
        return formattedId;
    }

    // Callback interface for authentication
    public interface AuthCallback {
        void onSuccess(String userId);
        void onFailure(Exception e);
    }
}
