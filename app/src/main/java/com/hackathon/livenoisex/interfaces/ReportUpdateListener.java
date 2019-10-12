package com.hackathon.livenoisex.interfaces;

import com.hackathon.livenoisex.models.Device;
import com.hackathon.livenoisex.models.Report;

public interface ReportUpdateListener {
    void onAdded(Report report);

    void onModified(Report report);

    void onRemoved(Report report);
}
