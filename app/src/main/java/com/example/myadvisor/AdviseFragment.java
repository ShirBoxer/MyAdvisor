package com.example.myadvisor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


public class AdviseFragment extends Fragment {
    TextView headerTv;
    TextView descriptionTv;
    ImageView adviseImageIv;
    Button editBtn;
    Button deleteBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_advise, container, false);
        headerTv = view.findViewById(R.id.advise_f_header_tv);
        descriptionTv =  view.findViewById(R.id.advise_f_description_tv);
        adviseImageIv =  view.findViewById(R.id.advise_f_image_iv);
        editBtn =  view.findViewById(R.id.advise_f_edit_btn);
        deleteBtn =  view.findViewById(R.id.advise_f_delete_btn);

        editBtn.setOnClickListener((v)->{

        });

        deleteBtn.setOnClickListener((v)->{

        });



        return view;
    }
}