package com.padma.bscaller.fragments;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;

import com.eftimoff.patternview.PatternView;
import com.padma.bscaller.R;
import com.padma.bscaller.Utils;
import com.padma.bscaller.database.Contacts;
import com.padma.bscaller.database.ContactsRepository;
import com.padma.bscaller.listener.ChangeFragmentListener;

import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 */
public class CreateContactPatternFragment extends Fragment implements View.OnClickListener, PatternView.OnPatternDetectedListener {

    AppCompatTextView tvCreateContact;

    PatternView patternView;

    AppCompatButton btnContinue;

    String patternString, confirmPatternString;

    ChangeFragmentListener changeFragmentListener;

    ContactsRepository contactsRepository;

    public CreateContactPatternFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_contact, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        changeFragmentListener.textToVoice(getString(R.string.pattern_create_contact));
        tvCreateContact = view.findViewById(R.id.create_contact_title);
        btnContinue = view.findViewById(R.id.btn_pattern_confirm);
        patternView = view.findViewById(R.id.cc_patternView);

        btnContinue.setOnClickListener(this);
        patternView.setOnPatternDetectedListener(this);

        contactsRepository = new ContactsRepository(getContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().setTitle(R.string.title_create_contact);
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

        if(v.getId() == R.id.btn_pattern_confirm) {

            if(btnContinue.getText().equals(getString(R.string.pattern_continue))) {

                try {
                    // Check Pattern already exits
                    Contacts contacts = contactsRepository.getContact(patternString);

                    if(contacts == null) {
                        changeFragmentListener.textToVoice(getString(R.string.pattern_confirm_contact));
                        tvCreateContact.setText(getString(R.string.pattern_confirm_contact));
                        btnContinue.setText((getString(R.string.pattern_confirm)));
                        patternView.clearPattern();
                    } else {
                        patternView.clearPattern();
                        changeFragmentListener.textToVoice(getString(R.string.contact_already_exists));
                        Utils.showMessage(getContext(),getString(R.string.error), getString(R.string.contact_already_exists));
                    }

                } catch (ExecutionException e) {
                    e.printStackTrace();
                    changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
                    Utils.showMessage(getContext(),getString(R.string.error),getString(R.string.oops));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
                    Utils.showMessage(getContext(),getString(R.string.error),getString(R.string.oops));
                }


            } else {

                if(patternString.equals(confirmPatternString)) {

                    CreateContactNumberFragment createContactNumberFragment = new CreateContactNumberFragment(patternString);
                    changeFragmentListener.addFragmentWithAnimation(createContactNumberFragment,false, true);


                } else {
                    patternView.clearPattern();
                    changeFragmentListener.textToVoice(getString(R.string.confirm_pattern_error));
                    Utils.showMessage(getContext(),getString(R.string.error), getString(R.string.confirm_pattern_error));
                }

            }

        }

    }

    @Override
    public void onPatternDetected() {

        if(tvCreateContact.getText().equals(getString(R.string.pattern_create_contact))) {
            patternString = patternView.getPatternString();
            Log.i("Pattern", "onPatternCellAdded: " + patternString);
        } else {
            confirmPatternString = patternView.getPatternString();
            Log.i("Pattern", "onPatternCellAdded: " + confirmPatternString);
        }
    }
}
