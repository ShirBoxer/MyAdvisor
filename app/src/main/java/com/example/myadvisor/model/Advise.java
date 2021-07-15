package com.example.myadvisor.model;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.example.myadvisor.MyApplication;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FieldValue;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Advise {
    @PrimaryKey
    @NonNull
    String id;
    String name;
    String description;
    String photoUrl;
    Long lastUpdated;

    final static String ID = "id";
    final static String NAME = "name";
    final static String DESCRIPTION = "description";
    final static String PHOTO_URL = "photoUrl";
    final static String LAST_UPDATED = "lastUpdated";
    public final static String ADVISE_LAST_UPDATED = "AdviseLastUpdated";

    public Advise(){}
    @Ignore
    public Advise(@NonNull String id, String name, String description, String photoUrl) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.photoUrl = photoUrl;
        this.lastUpdated = System.currentTimeMillis();
    }

    static public void setLocalLastUpdateTime(Long timestamp){
        SharedPreferences.Editor editor = MyApplication.context.getSharedPreferences("TAG", Context.MODE_PRIVATE).edit();
        editor.putLong(ADVISE_LAST_UPDATED, timestamp);
        editor.commit();
    }

    static public Long getLocalLastUpdateTime(){
        return MyApplication
                .context
                .getSharedPreferences("TAG", Context.MODE_PRIVATE)
                .getLong(ADVISE_LAST_UPDATED, 0);
    }

    public Map<String, Object> toJson(){
        Map<String, Object> json = new HashMap<>();
        json.put(NAME, name);
        json.put(DESCRIPTION, description);
        json.put(PHOTO_URL, photoUrl);
        json.put(ID, id);
        json.put(LAST_UPDATED, FieldValue.serverTimestamp());
        return json;
    }

    static public Advise createAdvise(Map<String, Object> json){ //TODO: input checking
        Advise a = new Advise();
        a.setId((String) json.get(ID));
        a.setDescription((String) json.get(DESCRIPTION));
        a.setPhotoUrl((String) json.get(PHOTO_URL));
        a.setName((String) json.get(NAME));
        Timestamp ts = (Timestamp) json.get(LAST_UPDATED);
        if (ts != null)
            a.setLastUpdated(ts.getSeconds());
        else{
            a.setLastUpdated(new Long(0));
        }
        return a;

    }

    @NonNull
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public Long getLastUpdated() {
        return lastUpdated;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public void setLastUpdated(Long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
