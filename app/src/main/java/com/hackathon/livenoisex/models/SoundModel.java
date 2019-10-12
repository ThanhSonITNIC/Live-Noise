package com.hackathon.livenoisex.models;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.GeoPoint;
import com.hackathon.livenoisex.MyApplication;
import com.hackathon.livenoisex.utils.SharedPreferenceHelper;

import java.util.HashMap;
import java.util.Map;

public class SoundModel extends FirebaseDatabase {

    private static final String COLLECTION_NAME  = "Devices";

    public SoundModel(){
    }

    public void writeInsentity(Device device){
       if(TextUtils.isEmpty(MyApplication.idDevice)){
           addInsensity(device);
       } else {
           updateInsensity(device);
       }
    }

    public void addInsensity(Device device){
        // Add a new document with a generated ID
        db.collection(COLLECTION_NAME)
                .add(device.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        MyApplication.idDevice = documentReference.getId();
                        SharedPreferenceHelper.getInstance().setSharedPreferenceString(SharedPreferenceHelper.KEY_DEVICE_ID, documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }

    public void updateInsensity(Device device){
        db.collection(COLLECTION_NAME).document(MyApplication.idDevice)
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
