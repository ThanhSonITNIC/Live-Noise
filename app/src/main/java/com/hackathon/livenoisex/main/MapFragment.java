package com.hackathon.livenoisex.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlay;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.google.maps.android.heatmaps.WeightedLatLng;
import com.hackathon.livenoisex.R;
import com.hackathon.livenoisex.interfaces.DeviceUpdateListener;
import com.hackathon.livenoisex.interfaces.GetDataListener;
import com.hackathon.livenoisex.interfaces.GetReportsListener;
import com.hackathon.livenoisex.interfaces.ReportUpdateListener;
import com.hackathon.livenoisex.models.Device;
import com.hackathon.livenoisex.models.MapModel;
import com.hackathon.livenoisex.models.Report;
import com.hackathon.livenoisex.models.ReportModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CopyOnWriteArrayList;

public class MapFragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener {

    private MapModel mMapModel;

    private GoogleMap mMap;

    private CameraPosition mCameraPosition;


    // The entry point to the Fused Location Provider.
    private FusedLocationProviderClient mFusedLocationProviderClient;

    // A default location (Da Nang, Viet Nam) and default zoom to use when location permission is not granted.
    private final LatLng mDefaultLocation = new LatLng(16.071661, 108.223093);
    private static final int DEFAULT_ZOOM = 14;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 31;
    private boolean mLocationPermissionGranted;

    // The geographical location where the device is currently located. That is, the last-known location retrieved by the Fused Location Provider.
    private Location mLastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private HeatmapTileProvider mProvider;
    private TileOverlay mOverlay;
    private List<Device> mDeviceList;
    private List<Report> mReportList;
    private List<Marker> mReportMakerList;
    private float mZoom = DEFAULT_ZOOM;


    public static MapFragment newInstance() {
        return new MapFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        mMapModel = new MapModel();


        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.map_fragment, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Use the MapModel

    }

    private ImageView btnRefresh;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getSoundData();
            }
        });
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        if (mMap != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, mMap.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, mLastKnownLocation);
            super.onSaveInstanceState(outState);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add markers to the map and do other map setup.
        // Set a listener for info window events.
        mMap.setOnInfoWindowClickListener(this);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Report report = (Report) marker.getTag();
                createReportDialog(report).show();
            }
        });


        // Customise the styling of the base map using a JSON object defined
        // in a raw resource file.
        mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getContext(), R.raw.style_json));
        mMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {
                if (mZoom != cameraPosition.zoom) {
                    addHeatMap();
                }
            }
        });
        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        getSoundData();

        getReportData();

    }

    private void getReportData() {
        ReportModel reportModel = new ReportModel();
        reportModel.firstRead(new GetReportsListener() {
            @Override
            public void onGetReportsSuccess(List<Report> reports) {
                mReportList = reports;
            }

            @Override
            public void onGetReportsFailure() {

            }
        });

        reportModel.addOnDataUpdate(new ReportUpdateListener() {
            @Override
            public void onAdded(Report report) {
                if (mReportList == null) {
                    mReportList = new CopyOnWriteArrayList<>();
                }
                mReportList.add(report);
                updateReportMarkers();
            }

            @Override
            public void onModified(Report report) {
                for (int i = 0, n = mReportList.size(); i < n; ++i) {
                    Report report1 = mReportList.get(i);
                    if (report1.getLatitude() == report.getLatitude() && report1.getLongtitude() == report.getLongtitude()) {
                        mReportList.set(i, report);
                        break;
                    }
                }
                updateReportMarkers();
            }

            @Override
            public void onRemoved(Report report) {
                for (int i = 0, n = mReportList.size(); i < n; ++i) {
                    Report report1 = mReportList.get(i);
                    if (report1.getLatitude() == report.getLatitude() && report1.getLongtitude() == report.getLongtitude()) {
                        mReportList.remove(i);
                        break;
                    }
                }
                updateReportMarkers();
            }
        });
    }

    private void updateReportMarkers() {
        if (mReportList == null || mReportList.size() == 0) {
            return;
        }
        if (mReportMakerList != null) {
            for (Marker marker : mReportMakerList) {
                marker.remove();
            }
        }
        mReportMakerList = new CopyOnWriteArrayList<>();
        for (int i = 0, n = mReportList.size(); i < n; ++i) {
            Report report = mReportList.get(i);
            Marker marker = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_warning))
                    .title("" + report.getDecibel())
                    .position(new LatLng(report.getLatitude(),
                            report.getLongtitude())));
            marker.setTag(report);
            mReportMakerList.add(marker);
        }
    }

    private void getSoundData() {
        mMapModel.firstRead(new GetDataListener() {
            @Override
            public void onGetDataSuccess(List<Device> devices) {
                if (devices != null) {
                    mDeviceList = devices;
                    addHeatMap();
                } else {
                    mDeviceList = new CopyOnWriteArrayList<>();
                }

            }
        });

        mMapModel.addOnDataUpdate(new DeviceUpdateListener() {
            @Override
            public void onAdded(Device device) {
                if (mDeviceList == null) {
                    mDeviceList = new CopyOnWriteArrayList<>();
                }
                mDeviceList.add(device);
                addHeatMap();
            }

            @Override
            public void onModified(int oldindex, Device newDevice) {

            }

            @Override
            public void onRemoved(int oldindex) {

            }
        });
    }


    public void addHeatMap() {
        if (mDeviceList == null) {
            return;
        }
        if (mOverlay != null) {
            mOverlay.remove();
        }
        List<WeightedLatLng> weightedLatLngList = new CopyOnWriteArrayList<>();
        for (Device device : mDeviceList) {
            WeightedLatLng weightedLatLng = new WeightedLatLng(new LatLng(device.getLatitude(), device.getLongtitude()),
                    device.getInsensity());
            weightedLatLngList.add(weightedLatLng);
        }
        int[] colors = {
                Color.rgb(102, 225, 0), // green
                Color.rgb(255, 0, 0)    // red
        };

        float[] startPoints = {
                0.2f, 1f
        };
        Gradient gradient = new Gradient(colors, startPoints);

        // Create a heat map tile provider, passing it the latlngs of the police stations.
        mProvider = new HeatmapTileProvider.Builder()
                .weightedData(weightedLatLngList)
                .gradient(gradient)
                .radius(50 - (int) mZoom)
                .build();
        // Add a tile overlay to the map, using the heat map tile provider.
        mOverlay = mMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }

    private ArrayList<WeightedLatLng> readItems(int resource) throws JSONException {
        ArrayList<WeightedLatLng> list = new ArrayList<WeightedLatLng>();
        InputStream inputStream = getResources().openRawResource(resource);
        String json = new Scanner(inputStream).useDelimiter("\\A").next();
        JSONArray array = new JSONArray(json);
        for (int i = 0; i < array.length(); i++) {
            JSONObject object = array.getJSONObject(i);
            double lat = object.getDouble("lat");
            double lng = object.getDouble("lng");
            double intensity = object.getDouble("intensity");
            list.add(new WeightedLatLng(new LatLng(lat, lng), intensity));
        }
        return list;
    }


    /**
     * Handles the result of the request for location permissions.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                    getDeviceLocation();
                    updateLocationUI();
                }
            }
        }
    }

    /**
     * Gets the current location of the device, and positions the map's camera.
     */
    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(getActivity(), new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                        } else {
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(getContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            requestPermissions(
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION
            );
        }
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public void onInfoWindowClick(Marker marker) {

    }

    public Dialog createReportDialog(final Report report) {
        Dialog dialog = null;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Set text

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        View view = inflater.inflate(R.layout.dialog_resource, null);
        builder.setView(view);

        ((TextView)view.findViewById(R.id.tv_decibel)).setText(report.getDecibel()+"");
        ((TextView)view.findViewById(R.id.tv_desc)).setText(report.getDescription());
        ((TextView)view.findViewById(R.id.tv_location)).setText(String.format("%.2f - %.2f",
                report.getLatitude(), report.getLongtitude()));
        ((TextView)view.findViewById(R.id.tv_phone)).setText(report.getPhone()+"");

        ((TextView)view.findViewById(R.id.btn_call)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String uri = "tel:" + report.getPhone() ;
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse(uri));
                startActivity(intent);
            }
        });

        // Add action buttons;
        dialog = builder.create();
        final Dialog finalDialog = dialog;
        ((TextView)view.findViewById(R.id.btn_close)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finalDialog.cancel();
            }
        });
        return dialog;
    }
}
