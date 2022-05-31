package com.padma.bscaller.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

@Dao
public interface ContactsDao {

    @Insert
    Long createContact(Contacts contacts);

    @Query("SELECT * FROM Contacts WHERE patternString =:pattern")
    Contacts getContact(String pattern);
}
