package com.padma.bscaller.fragments;


import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.eftimoff.patternview.PatternView;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.padma.bscaller.R;
import com.padma.bscaller.Utils;
import com.padma.bscaller.database.Contacts;
import com.padma.bscaller.database.ContactsRepository;
import com.padma.bscaller.listener.ChangeFragmentListener;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class BSSendLocationFragment extends Fragment implements PatternView.OnPatternDetectedListener, LocationListener {


    private static final long LOCATION_REFRESH_TIME = 5000;

    private static final float LOCATION_REFRESH_DISTANCE = 10;

    static Location mlocation;

    PatternView sendLocationPattern;

    ContactsRepository contactsRepository;

    LocationManager mLocationManager;
    private FusedLocationProviderClient fusedLocationClient;

    ChangeFragmentListener changeFragmentListener;

    public BSSendLocationFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bssendlocation, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        sendLocationPattern = view.findViewById(R.id.send_location_patternView);


        mLocationManager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

        contactsRepository = new ContactsRepository(getContext());

        sendLocationPattern.setOnPatternDetectedListener(this);

        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {


            fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

            mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, LOCATION_REFRESH_TIME,
                    LOCATION_REFRESH_DISTANCE, this);


            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {

                            if (location != null) {
                                mlocation = location;
                            }
                        }
                    });

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeFragmentListener = (ChangeFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_send_location);
    }


    @Override
    public void onPatternDetected() {

        try {
            String patternString = sendLocationPattern.getPatternString();
            Log.i("Pattern", "onPatternDetected: " + patternString);

            Contacts contacts = contactsRepository.getContact(patternString);

            if (contacts != null) {

                String phoneNumber = contacts.getContactNumber();
                sendLocation(phoneNumber);

            } else {
                sendLocationPattern.clearPattern();
                changeFragmentListener.textToVoice(getString(R.string.no_contact));
                Utils.showMessage(getContext(), getString(R.string.warning), getString(R.string.no_contact));
            }

        } catch (Exception e) {
            e.printStackTrace();
            sendLocationPattern.clearPattern();
            changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
            Utils.showMessage(getContext(), getString(R.string.error), getString(R.string.oops));
        }
    }

    private void sendLocation(String phoneNumber) {

        try {

            Double latitude = mlocation.getLatitude();
            Double longitude = mlocation.getLongitude();

            String locationMsg = "Hi, \nMy Current Location is \n ";

            locationMsg += " http://maps.google.com/?q=" + latitude.toString() + "," + longitude.toString();

            locationMsg += " \nBAS Caller App";

            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, locationMsg, null, null);

            changeFragmentListener.textToVoice(getString(R.string.location_shared_sucessfully));

            Toast.makeText(getContext(), getString(R.string.location_shared_sucessfully),
                    Toast.LENGTH_LONG).show();

        } catch (Exception ex) {
            sendLocationPattern.clearPattern();
            Toast.makeText(getContext(), ex.getMessage(),
                    Toast.LENGTH_LONG).show();
            changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
            ex.printStackTrace();
        }

        HomeFragment homeFragment = new HomeFragment();
        changeFragmentListener.addFragmentWithAnimation(homeFragment,true, true);

    }

    @Override
    public void onLocationChanged(Location location) {
        mlocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}
