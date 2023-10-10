package com.osepoo.driverapp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseHelper {

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

    // Store user data and location coordinates in the database
    public void storeUserAndLocationData(String userId, int driverId, double latitude, double longitude) {
        DatabaseReference usersReference = databaseReference.child("users").child(userId);
        usersReference.child("driverId").setValue(driverId);
        usersReference.child("location").child("latitude").setValue(latitude);
        usersReference.child("location").child("longitude").setValue(longitude);
    }

    // Add more methods as needed for your specific use case
}
