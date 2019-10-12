package com.hackathon.livenoisex.interfaces;

import com.hackathon.livenoisex.models.Report;

import java.util.List;

public interface GetReportsListener {
    void onGetReportsSuccess(List<Report> reports);

    void onGetReportsFailure();
}
