package com.hackathon.livenoisex.interfaces;

import com.google.firebase.firestore.DocumentSnapshot;
import com.hackathon.livenoisex.models.Device;

import java.util.List;

public interface GetDataListener {
    void onGetDataSuccess(List<Device> devices);
}
