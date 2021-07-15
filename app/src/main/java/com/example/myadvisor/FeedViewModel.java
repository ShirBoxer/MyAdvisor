package com.example.myadvisor;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;

import java.util.List;

public class FeedViewModel extends ViewModel {
    LiveData<List<Advise>> AdvisesList;

    public FeedViewModel() {
        this.AdvisesList = Model.instance.getAllAdvises();
    }

    public LiveData<List<Advise>> getAdvisesList() {
        return AdvisesList;
    }
}
