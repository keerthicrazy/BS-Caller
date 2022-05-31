package com.padma.bscaller.database;

import android.content.Context;
import android.os.AsyncTask;

import java.util.concurrent.ExecutionException;

public class ContactsRepository {

    BSDatabase bsDatabase;

    private ContactsDao contactsDao;

    public ContactsRepository(Context context) {
        bsDatabase = BSDatabase.getInstance(context);
        contactsDao = bsDatabase.contactsDao();
    }

    public Long createContact(Contacts contacts) throws ExecutionException, InterruptedException  {
        return new CreateContact(contactsDao).execute(contacts).get();
    }

    public Contacts getContact(String pattern) throws ExecutionException, InterruptedException {
        return new GetContact(contactsDao).execute(pattern).get();
    }

    static class CreateContact extends AsyncTask<Contacts, Void, Long> {

        ContactsDao contactsDao;

        CreateContact(ContactsDao contactsDao) {
            this.contactsDao = contactsDao;
        }

        @Override
        protected Long doInBackground(Contacts... contacts) {
            return contactsDao.createContact(contacts[0]);
        }

    }

    static class GetContact extends AsyncTask<String, Void, Contacts> {

        ContactsDao contactsDao;

        GetContact(ContactsDao contactsDao) {
            this.contactsDao = contactsDao;
        }

        @Override
        protected Contacts doInBackground(String... pattern) {
            return contactsDao.getContact(pattern[0]);
        }

    }

}
