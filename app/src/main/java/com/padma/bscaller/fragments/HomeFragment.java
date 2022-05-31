package com.padma.bscaller.fragments;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.padma.bscaller.R;
import com.padma.bscaller.listener.ChangeFragmentListener;


public class HomeFragment extends Fragment implements View.OnClickListener {

    private static final int ASK_MULTIPLE_PERMISSION_REQUEST_CODE = 10001;

    ChangeFragmentListener changeFragmentListener;

    CreateContactPatternFragment createContactPatternFragment;

    BSCallerFragment bsCallerFragment;

    BSSendLocationFragment bsSendLocationFragment;

    private CardView call, addContacts, sendLocation, exit;

    Vibrator vibrator;


    public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        call = view.findViewById(R.id.call);
        addContacts = view.findViewById(R.id.add_contacts);

        sendLocation = view.findViewById(R.id.send_location);
        exit = view.findViewById(R.id.exit);

        call.setOnClickListener(this);
        addContacts.setOnClickListener(this);
        sendLocation.setOnClickListener(this);
        exit.setOnClickListener(this);

        changeFragmentListener.textToVoice(getString(R.string.opened_bs_caller));

        vibrator = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);

        checkAndroidVersion();

    }

    private void checkAndroidVersion() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            checkPermission();

        } else {
            // write your logic here
        }

    }

    private void checkPermission() {
        if (ContextCompat.checkSelfPermission(getActivity(),
                Manifest.permission.CALL_PHONE) + ContextCompat
                .checkSelfPermission(getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION) +
                ContextCompat
                        .checkSelfPermission(getActivity(),
                                Manifest.permission.ACCESS_COARSE_LOCATION) +
                ContextCompat
                        .checkSelfPermission(getActivity(),
                                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {


            requestPermissions(new String[]{
                            Manifest.permission.CALL_PHONE,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.SEND_SMS},
                    ASK_MULTIPLE_PERMISSION_REQUEST_CODE);


        } else {
            // Permission Already Granted
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.app_name);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        changeFragmentListener = (ChangeFragmentListener) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        changeFragmentListener = null;
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.call:
                long[] pattern = {100, 500, 100, 500};
                vibrator.vibrate(pattern , -1);
                changeFragmentListener.textToVoice(getString(R.string.call));
                bsCallerFragment = new BSCallerFragment();
                changeFragmentListener.addFragmentWithAnimation(bsCallerFragment, false, true);
                break;
            case R.id.add_contacts:
                changeFragmentListener.textToVoice(getString(R.string.add_contacts));
                createContactPatternFragment = new CreateContactPatternFragment();
                changeFragmentListener.addFragmentWithAnimation(createContactPatternFragment, false, true);
                break;
            case R.id.send_location:
                long[] spattern = {0, 500};
                vibrator.vibrate(spattern , -1);
                changeFragmentListener.textToVoice(getString(R.string.send_location));
                bsSendLocationFragment = new BSSendLocationFragment();
                changeFragmentListener.addFragmentWithAnimation(bsSendLocationFragment, false, true);
                break;
            case R.id.exit:
                changeFragmentListener.textToVoice(getString(R.string.exit));
                getActivity().finishAffinity();
                break;
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case ASK_MULTIPLE_PERMISSION_REQUEST_CODE:
                if (grantResults.length > 0) {
                    boolean sendSMS = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean coarseLocation = grantResults[2] == PackageManager.PERMISSION_GRANTED;
                    boolean fineLocation = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    boolean callPermission = grantResults[0] == PackageManager.PERMISSION_GRANTED;

                    if (callPermission && fineLocation && coarseLocation && sendSMS) {
                        // write your logic here
                    } else {

                        requestPermissions(new String[]{
                                        Manifest.permission.CALL_PHONE,
                                        Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION,
                                        Manifest.permission.SEND_SMS},
                                ASK_MULTIPLE_PERMISSION_REQUEST_CODE);

                    }
                }
                break;
        }
    }


}
