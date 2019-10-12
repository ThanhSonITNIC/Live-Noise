package com.hackathon.livenoisex.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;

public interface GetDataListener {
    void onGetDataSuccess(List<DocumentSnapshot> snapshotList);
}
