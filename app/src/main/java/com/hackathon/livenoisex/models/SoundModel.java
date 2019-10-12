package com.hackathon.livenoisex.models;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class SoundModel extends FirebaseDatabase {

    private static final String COLLECTION_NAME  = "Devices";

    private String idDevice;

    public SoundModel(){

    }

    public void writeInsentity(Device device){
        // Add a new document with a generated ID
        db.collection(COLLECTION_NAME)
            .add(device.toMap())
            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    idDevice = documentReference.getId();
                }
            })
            .addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });
    }

    public void updateInsensity(Device device){
        db.collection(COLLECTION_NAME).document(idDevice)
            .update(device.toMap())
            .addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.println(Log.ERROR, "xxx", "updated");
                }
            }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.println(Log.ERROR, "xxx", "error");
            }
        });
    }

}
