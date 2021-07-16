package com.example.myadvisor.model;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.myadvisor.MyApplication;

@Database(entities = {Advise.class}, version = 2)
abstract class AppLocalDbRepository extends RoomDatabase {
    public abstract AdviseDao adviseDao();
}

public class AppLocalDB{
    final static public AppLocalDbRepository db =
            Room.databaseBuilder(MyApplication.context,
                    AppLocalDbRepository.class,
                    "myAdvisor.db")
                    .fallbackToDestructiveMigration()
                    .build();
}
