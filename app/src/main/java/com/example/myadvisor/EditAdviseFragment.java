package com.example.myadvisor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;
import com.squareup.picasso.Picasso;


public class EditAdviseFragment extends Fragment {
    EditText headerTv;
    EditText descriptionTv;
    ImageView adviseImageIv;
    Button saveBtn;
    FeedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_edit_advise, container, false);
        headerTv = view.findViewById(R.id.edit_advise_f_header_et);
        descriptionTv = view.findViewById(R.id.edit_advise_f_description_et);
        adviseImageIv = view.findViewById(R.id.edit_advise_f_image_iv);
        saveBtn = view.findViewById(R.id.edit_advise_f_save_btn);
        viewModel = new FeedViewModel();
        String adviseId = AdviseFragmentArgs.fromBundle(getArguments()).getAdviseId();
        Advise advise = null;
        for(Advise a: viewModel.getAdvisesList().getValue()){
            if(a.getId().equals(adviseId)){
                advise = a;
                break;
            }
        }
        if (advise == null){
            //TOAST
            //LOG
            //BACK TO FEED
            return view;
        }

        headerTv.setText(advise.getName());
        descriptionTv.setText(advise.getDescription());
        Picasso.get().load(advise.getPhotoUrl()).into(adviseImageIv);

        Advise finalAdvise = advise;
        saveBtn.setOnClickListener((v)->{
            finalAdvise.setName(headerTv.getText().toString());
            finalAdvise.setDescription(descriptionTv.getText().toString());
            Model.instance.saveAdvise(finalAdvise, ()->{
                Log.d("EDIT_ADVICE", "Advise " + finalAdvise.getId() + " Edited");
            });
            Navigation.findNavController(view).navigateUp();
        });


        return view;
    }
}