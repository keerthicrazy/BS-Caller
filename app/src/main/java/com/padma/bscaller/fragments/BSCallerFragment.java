package com.padma.bscaller.fragments;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.eftimoff.patternview.PatternView;
import com.eftimoff.patternview.cells.Cell;
import com.padma.bscaller.R;
import com.padma.bscaller.Utils;
import com.padma.bscaller.database.Contacts;
import com.padma.bscaller.database.ContactsRepository;
import com.padma.bscaller.listener.ChangeFragmentListener;

import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * A simple {@link Fragment} subclass.
 */
public class BSCallerFragment extends Fragment implements  PatternView.OnPatternDetectedListener {


    PatternView dialPatternView;

    ContactsRepository contactsRepository;

    ChangeFragmentListener changeFragmentListener;

    public BSCallerFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bscaller, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dialPatternView = view.findViewById(R.id.dial_patternView);

        contactsRepository = new ContactsRepository(getContext());

        dialPatternView.setOnPatternDetectedListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_call);
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
    public void onPatternDetected() {

        try {
            String patternString = dialPatternView.getPatternString();
            Log.i("Pattern", "onPatternDetected: "+patternString);

            Contacts contacts = contactsRepository.getContact(patternString);

            if(contacts != null) {

                String phoneNumber = contacts.getContactNumber();
                String callStr = "Calling "+contacts.getContactName();
                changeFragmentListener.textToVoice(callStr);
                dialPhoneNumber(phoneNumber);

            } else {
                dialPatternView.clearPattern();
                changeFragmentListener.textToVoice(getString(R.string.no_contact));
                Utils.showMessage(getContext(),getString(R.string.warning),getString(R.string.no_contact));
            }

        } catch (Exception e) {
            e.printStackTrace();
            changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
            Utils.showMessage(getContext(),getString(R.string.error),getString(R.string.oops));
        }
    }

    private void dialPhoneNumber(String phoneNumber) {

            phoneNumber = "tel:"+phoneNumber;

            final Intent callIntent = new Intent(Intent.ACTION_CALL);
            callIntent.setData(Uri.parse(phoneNumber));

            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.CALL_PHONE},
                        10);
                return;
            }else {
                try{

                    final Handler handler = new Handler();

                    final Runnable r = new Runnable() {
                        public void run() {

                            HomeFragment homeFragment = new HomeFragment();
                            changeFragmentListener.addFragmentWithAnimation(homeFragment,true, true);
                            startActivity(callIntent);  //call activity and make phone call
                        }
                    };

                    handler.postDelayed(r, 1000);

                }
                catch (android.content.ActivityNotFoundException ex){
                    dialPatternView.clearPattern();
                    Toast.makeText(getContext(),"yourActivity is not founded", Toast.LENGTH_SHORT).show();
                }
            }

    }
}
