package com.padma.bscaller.fragments;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.fragment.app.Fragment;

import com.padma.bscaller.R;
import com.padma.bscaller.Utils;
import com.padma.bscaller.database.Contacts;
import com.padma.bscaller.database.ContactsRepository;
import com.padma.bscaller.listener.ChangeFragmentListener;

import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateContactNumberFragment extends Fragment implements View.OnClickListener {

    String pattern;

    AppCompatEditText contactName, contactNumber;

    AppCompatButton saveContact;

    ContactsRepository contactsRepository;

    ChangeFragmentListener changeFragmentListener;


    public CreateContactNumberFragment() {
        // Required empty public constructor
    }

    public CreateContactNumberFragment(String pattern) {
        this.pattern = pattern;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_contact_number, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactName = view.findViewById(R.id.contact_name);
        contactNumber = view.findViewById(R.id.contact_number);

        saveContact = view.findViewById(R.id.btn_create_contact);

        saveContact.setOnClickListener(this);

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

        if (v.getId() == R.id.btn_create_contact) {

            try {

                if (!contactName.getText().toString().isEmpty()) {

                    if (isPhoneNumber(contactNumber.getText().toString())) {

                        Contacts contacts = new Contacts();
                        contacts.setPatternString(pattern);
                        contacts.setContactName(contactName.getText().toString());
                        contacts.setContactNumber(contactNumber.getText().toString());
                        contacts.setCreatedAt(new Date());

                        Long i = contactsRepository.createContact(contacts);

                        if (i > 0) {
                            changeFragmentListener.textToVoice(getString(R.string.contact_saved_successfully));
                            Toast.makeText(getContext(), getString(R.string.contact_saved_successfully), Toast.LENGTH_LONG).show();

                            HomeFragment homeFragment = new HomeFragment();
                            changeFragmentListener.addFragmentWithAnimation(homeFragment, true, true);

                        } else {
                            changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
                            Utils.showMessage(getContext(), getString(R.string.error), getString(R.string.oops));
                        }


                    } else {
                        changeFragmentListener.textToVoice(getString(R.string.phone_number_error));
                        Utils.showMessage(getContext(), getString(R.string.error), getString(R.string.phone_number_error));
                    }

                } else {
                    changeFragmentListener.textToVoice(getString(R.string.contact_name_error));
                    Utils.showMessage(getContext(), getString(R.string.error), getString(R.string.contact_name_error));

                }


            } catch (Exception e) {
                changeFragmentListener.textToVoice(getString(R.string.something_went_wrong));
                Utils.showMessage(getContext(), getString(R.string.error), getString(R.string.oops));
            }


        }
    }

    public boolean isPhoneNumber(String mobileNumber) {
        return mobileNumber.length() >= 8 && mobileNumber.length() <= 10;
    }
}
