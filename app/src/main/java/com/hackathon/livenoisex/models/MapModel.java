package com.hackathon.livenoisex.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hackathon.livenoisex.interfaces.DeviceUpdateListener;
import com.hackathon.livenoisex.interfaces.GetDataListener;

import java.util.List;

public class MapModel extends FirebaseDatabase {
    public MapModel() {

    }

    public void firstRead(final GetDataListener listener) {
        CollectionReference docRef = db.collection("Devices");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshot = task.getResult();
                List<Device> devices = documentSnapshot.toObjects(Device.class);
                listener.onGetDataSuccess(devices);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    public void addOnDataUpdate(final DeviceUpdateListener deviceUpdateListener) {
        db.collection("Devices")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                        @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w("xxx", "listen:error", e);
                            return;
                        }

                        for (DocumentChange dc : snapshots.getDocumentChanges()) {

                            Log.d("1111111111", "DocumentChange: " + dc.getOldIndex() + "----" + dc.getNewIndex() + "-----" + dc.getType());
                            switch (dc.getType()) {
                                case ADDED:
                                    deviceUpdateListener.onAdded(dc.getDocument().toObject(Device.class));
                                    break;
                                case MODIFIED:
                                    deviceUpdateListener.onModified(dc.getOldIndex(), dc.getDocument().toObject(Device.class));
                                    break;
                                case REMOVED:
                                    deviceUpdateListener.onRemoved(dc.getOldIndex());
                                    break;
                            }
                        }

                    }
                });
    }
}
