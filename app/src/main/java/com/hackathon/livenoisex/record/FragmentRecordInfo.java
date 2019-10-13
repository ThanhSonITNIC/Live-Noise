package com.hackathon.livenoisex.record;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hackathon.livenoisex.R;
import com.hackathon.livenoisex.interfaces.AddReportListener;
import com.hackathon.livenoisex.models.Report;
import com.hackathon.livenoisex.models.ReportModel;

import java.io.File;

public class FragmentRecordInfo extends Fragment {


    private TextView tvDecibel, btnReport;
    private EditText edtPhone, edtDescription;
    private int decibel;
    private double latitude, longtitude;
    private UploadTask uploadTask;
    private ProgressDialog mProgressDialog;
    private String childPath;

    public static FragmentRecordInfo newInstance(int decibel, double latitude, double longtitude, String childPath) {
        FragmentRecordInfo fragmentRecordInfo = new FragmentRecordInfo();
        Bundle args = new Bundle();
        args.putInt(RecordActivity.KEY_DECIBEL, decibel);
        args.putDouble(RecordActivity.KEY_LATITUDE, latitude);
        args.putDouble(RecordActivity.KEY_LONGTITUDE, longtitude);
        args.putString(RecordActivity.KEY_CHILD_PATH, childPath);
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
        childPath = args.getString(RecordActivity.KEY_CHILD_PATH, "");
        tvDecibel.setText(decibel + "");

        btnReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submit();
            }
        });
    }

    private void submit() {
        Uri file = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                childPath));
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        final StorageReference riversRef = storageRef.child("reports/" + file.getLastPathSegment());
        StorageMetadata metadata = new StorageMetadata.Builder()
                .setContentType("audio/mpeg3")
                .build();
        uploadTask = riversRef.putFile(file);
        showProgressDialog();
        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    hideProgressDialog();
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    report(task.getResult().toString());
                } else {
                    hideProgressDialog();
                    Toast.makeText(getActivity(), R.string.upload_file_failed, Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.upload_file_failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.hide();
        }
    }

    private void showProgressDialog() {
        hideProgressDialog();
        mProgressDialog = new ProgressDialog(getActivity());
        mProgressDialog.show();
    }

    public void report(String url) {
        if (isDataInvalid()) {
            hideProgressDialog();
            Toast.makeText(getActivity(), "Input data is not valid", Toast.LENGTH_SHORT).show();
            return;
        }
        Report report = new Report(edtPhone.getText().toString(),
                url,
                edtDescription.getText().toString(),
                decibel, latitude, longtitude
        );
        ReportModel reportModel = new ReportModel();
        reportModel.addReport(report, new AddReportListener() {
            @Override
            public void onSuccess() {
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.create_report_success, Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void onFailure() {
                hideProgressDialog();
                Toast.makeText(getActivity(), R.string.create_report_error, Toast.LENGTH_SHORT).show();
            }
        });

    }

    private boolean isDataInvalid() {
        return edtDescription.getText().toString().isEmpty()
                || edtPhone.getText().toString().isEmpty();
    }
}
