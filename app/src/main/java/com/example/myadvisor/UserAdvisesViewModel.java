package com.example.myadvisor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;

import java.util.List;

public class UserAdvisesViewModel extends ViewModel {
    LiveData<List<Advise>> UserAdvisesList;

    public UserAdvisesViewModel() {
        this.UserAdvisesList = Model.instance.getAllUserAdvises(Model.instance.getAuthManager().getCurrentUser().getEmail());
    }

    public LiveData<List<Advise>> getUserAdvisesList() {
        return UserAdvisesList;
    }
}
