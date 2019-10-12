package com.hackathon.livenoisex.models;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.hackathon.livenoisex.MyApplication;
import com.hackathon.livenoisex.interfaces.AddReportListener;
import com.hackathon.livenoisex.interfaces.DeviceUpdateListener;
import com.hackathon.livenoisex.interfaces.GetReportsListener;
import com.hackathon.livenoisex.interfaces.ReportUpdateListener;
import com.hackathon.livenoisex.utils.SharedPreferenceHelper;

import java.util.List;

public class ReportModel extends FirebaseDatabase {
    private static final String COLLECTION_NAME = "Reports";

    public ReportModel() {

    }

    public void addReport(Report report, final AddReportListener listener) {
        // Add a new document with a generated ID
        db.collection(COLLECTION_NAME)
                .add(report.toMap())
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        listener.onSuccess();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        listener.onFailure();
                    }
                });
    }

    public void firstRead(final GetReportsListener listener) {
        CollectionReference docRef = db.collection("Reports");
        docRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot documentSnapshot = task.getResult();
                List<Report> reports = documentSnapshot.toObjects(Report.class);
                listener.onGetReportsSuccess(reports);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                listener.onGetReportsFailure();
            }
        });
    }

    public void addOnDataUpdate(final ReportUpdateListener reportUpdateListener) {
        db.collection(COLLECTION_NAME)
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
                                    reportUpdateListener.onAdded(dc.getDocument().toObject(Report.class));
                                    break;
                                case MODIFIED:
                                    reportUpdateListener.onModified(dc.getDocument().toObject(Report.class));
                                    break;
                                case REMOVED:
                                    reportUpdateListener.onRemoved(dc.getDocument().toObject(Report.class));
                                    break;
                            }
                        }

                    }
                });
    }
}
