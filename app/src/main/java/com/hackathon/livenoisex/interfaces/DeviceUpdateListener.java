package com.hackathon.livenoisex.interfaces;

import com.hackathon.livenoisex.models.Device;

public interface DeviceUpdateListener {
    void onAdded(Device device);

    void onModified(int oldindex, Device newDevice);

    void onRemoved(int oldindex);
}
