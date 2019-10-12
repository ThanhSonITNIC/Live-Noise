package com.hackathon.livenoisex.record;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.hackathon.livenoisex.R;
import com.hackathon.livenoisex.interfaces.AddReportListener;
import com.hackathon.livenoisex.models.Report;
import com.hackathon.livenoisex.models.ReportModel;

public class FragmentRecordInfo extends Fragment {


    private TextView tvDecibel, btnReport;
    private EditText edtPhone, edtDescription;
    private int decibel;
    private double latitude, longtitude;

    public static FragmentRecordInfo newInstance(int decibel, double latitude, double longtitude) {
        FragmentRecordInfo fragmentRecordInfo = new FragmentRecordInfo();
        Bundle args = new Bundle();
        args.putInt(RecordActivity.KEY_DECIBEL, decibel);
        args.putDouble(RecordActivity.KEY_LATITUDE, latitude);
        args.putDouble(RecordActivity.KEY_LONGTITUDE, longtitude);
        fragmentRecordInfo.setArguments(args);
        return fragmentRecordInfo;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_record_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tvDecibel = view.findViewById(R.id.tv_decibel);
        btnReport = view.findViewById(R.id.btn_report);
        edtPhone = view.findViewById(R.id.edt_phone);
        edtDescription = view.findViewById(R.id.edt_description);

        Bundle args = getArguments();
        decibel = args.getInt(RecordActivity.KEY_DECIBEL);
        latitude = args.getDouble(RecordActivity.KEY_LATITUDE);
        longtitude = args.getDouble(RecordActivity.KEY_LONGTITUDE);
        tvDecibel.setText(decibel+"");

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                report();
            }
        });
    }

    public void report() {
        if(isDataInvalid()){
            return;
        }
        Report report = new Report(edtPhone.getText().toString(),
                edtDescription.getText().toString(),
                decibel, latitude, longtitude
                );
        ReportModel reportModel = new ReportModel();
        reportModel.addReport(report, new AddReportListener() {
            @Override
            public void onSuccess() {
                Toast.makeText(getActivity(), R.string.create_report_success, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onFailure() {
                Toast.makeText(getActivity(), R.string.create_report_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isDataInvalid() {
        return false;
    }
}
