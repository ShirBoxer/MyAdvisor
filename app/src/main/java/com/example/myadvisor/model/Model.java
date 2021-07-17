package com.example.myadvisor.model;

import android.graphics.Bitmap;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Model {
    public static final Model instance = new Model();
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

    private Model() {}


    public enum LoadingState {
        loading,
        loaded,
        error
    }

    /* Data Members & Interfaces */
    public MutableLiveData<LoadingState> advisesLoadingState = new MutableLiveData<LoadingState>(LoadingState.loaded);
    LiveData<List<Advise>> allAdvises = AppLocalDB.db.adviseDao().getAll();

    public MutableLiveData<LoadingState> userAdvisesLoadingState = new MutableLiveData<LoadingState>(LoadingState.loaded);
    LiveData<List<Advise>> allUserAdvises = AppLocalDB.db.adviseDao().getAllByOwner(getAuthManager().getCurrentUser().getEmail());




    /* ################################# ---  Interfaces  --- ################################# */

    public interface OnCompleteListener{
        void onComplete();
    }

    public interface GetUserListener{
        void onComplete(User user);
    }

    public interface UploadImageListener{
        void onComplete(String url);
    }


    /* ################################# ---  User CRUD  --- ################################# */

    public void addUser(final User user, final OnCompleteListener listener){
        ModelFirebase.addUser(user, listener);
    }

    public void getUser(final Model.GetUserListener listener){
        ModelFirebase.getUser(listener);
    }

    public void setUserProfileImage(String url, OnCompleteListener listener) {
        ModelFirebase.setUserProfileImage(url, listener);
    }

    /* ################################# ---  Advise CRUD  --- ################################# */

    public LiveData<List<Advise>> getAllAdvises(){
        advisesLoadingState.setValue(LoadingState.loading);
        // Read the local last update time
        Long localLastUpdate = Advise.getLocalLastUpdateTime();
        // get all updates from firebase
        ModelFirebase.getAllAdvises(localLastUpdate,null, (advises)->{
            executorService.execute(()->{
                Long lastUpdate = new Long(0);
                for(Advise a: advises) {
                    // update the local db with the new records
                    AppLocalDB.db.adviseDao().insertAll(a);
                    //update the local last update time
                    if (lastUpdate < a.getLastUpdated()){
                        lastUpdate = a.getLastUpdated();
                    }
                }
                Advise.setLocalLastUpdateTime(lastUpdate);
                // post => update on the main thread for the observers
                advisesLoadingState.postValue(LoadingState.loaded);
                //read all the data from the local DB already happen while insertion.
                //LiveData gets update automatically
            });
        });
        return allAdvises;
    }

    public LiveData<List<Advise>> getAllUserAdvises(){
        userAdvisesLoadingState.setValue(LoadingState.loading);
        // Read the local last update time
        Long localLastUpdate = Advise.getLocalLastUpdateTime();
        // get all updates from firebase
        ModelFirebase.getAllAdvises(localLastUpdate,getAuthManager().getCurrentUser().getEmail(),(advises)->{
            executorService.execute(()-> {
                Long lastUpdate = new Long(0);
                for (Advise a : advises) {
                    // update the local db with the new records
                    AppLocalDB.db.adviseDao().insertAll(a);
                    //update the local last update time
                    if (lastUpdate < a.getLastUpdated()) {
                        lastUpdate = a.getLastUpdated();
                    }
                }
                Advise.setLocalLastUpdateTime(lastUpdate);
                // post => update on the main thread for the observers
                userAdvisesLoadingState.postValue(LoadingState.loaded);
                //read all the data from the local DB already happen while insertion.
                //LiveData gets update automatically
            });
        });
        return allUserAdvises;
    }

    public void saveAdvise(Advise advise, OnCompleteListener listener){
        advisesLoadingState.setValue(LoadingState.loading);
        ModelFirebase.saveAdvise(advise, ()->{
            getAllAdvises();
            listener.onComplete();
        });

    }

    public void deleteAdvise(Advise advise, OnCompleteListener listener){
        advisesLoadingState.setValue(LoadingState.loading);
        ModelFirebase.deleteAdvise(advise, ()->{
            getAllAdvises();
            listener.onComplete();
        });
        executorService.execute(()->{
            AppLocalDB.db.adviseDao().delete(advise);
        });
        advisesLoadingState.setValue(LoadingState.loaded);
    }

    public void updateAdvise(Advise advise, OnCompleteListener listener){
        advisesLoadingState.setValue(LoadingState.loading);
        ModelFirebase.saveAdvise(advise, ()->{
            getAllAdvises();
            listener.onComplete();
        });
        executorService.execute(()->{
            AppLocalDB.db.adviseDao().insertAll(advise);
        });

        advisesLoadingState.setValue(LoadingState.loaded);
    }


    /* ################################# ---  Utils  --- ################################# */

    public void uploadImage(Bitmap imageBmp, String name, final UploadImageListener listener) {
        ModelFirebase.uploadImage(imageBmp, name, listener);
    }

    public FirebaseAuth getAuthManager(){
        return ModelFirebase.getFirebaseAuth();
    }


}
