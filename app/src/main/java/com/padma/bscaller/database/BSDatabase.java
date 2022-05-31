package com.padma.bscaller.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Contacts.class}, version = 1, exportSchema = false)
public abstract class BSDatabase extends RoomDatabase {

    private static BSDatabase instance;

    private static String DB_NAME = "BS";

    public abstract ContactsDao contactsDao();

    public static BSDatabase getInstance(Context context) {

        if(instance == null) {

            instance = Room.databaseBuilder(context, BSDatabase.class, DB_NAME)
                    .fallbackToDestructiveMigration().build();
        }

        return instance;

    }


}


