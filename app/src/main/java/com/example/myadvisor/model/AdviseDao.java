package com.example.myadvisor.model;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface AdviseDao {
    @Query("select * from Advise ORDER BY lastUpdated")
    LiveData<List<Advise>> getAll();

    @Query("SELECT * FROM Advise WHERE owner LIKE '%' || :filter || '%'")
    LiveData<List<Advise>> getAllByOwner(String filter);

    //if id already exist replace her.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAll(Advise... advises);
    @Delete
    void delete(Advise advise);



}