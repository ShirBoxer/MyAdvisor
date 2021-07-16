package com.example.myadvisor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;
import static com.example.myadvisor.AddAdviseFragment.REQUEST_IMAGE_CAPTURE;


public class EditAdviseFragment extends Fragment {
    EditText headerTv;
    EditText descriptionTv;
    ImageView adviseImageIv;
    Button saveBtn;
    FeedViewModel viewModel;
    ImageButton addImgBtn;
    Advise  finalAdvise;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view  = inflater.inflate(R.layout.fragment_edit_advise, container, false);
        headerTv = view.findViewById(R.id.edit_advise_f_header_et);
        descriptionTv = view.findViewById(R.id.edit_advise_f_description_et);
        adviseImageIv = view.findViewById(R.id.edit_advise_f_image_iv);
        saveBtn = view.findViewById(R.id.edit_advise_f_save_btn);
        addImgBtn = view.findViewById(R.id.edit_advise_f_add_pic_btn);
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

        addImgBtn.setOnClickListener((v)->{
            takePicture();
        });

        finalAdvise= advise;
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

    private void takePicture(){
        Intent takePicture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePicture, REQUEST_IMAGE_CAPTURE);

    }

    // request code ~ the number of Operation, (REQUEST_IMAGE_CAPTURE=1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                adviseImageIv.setImageBitmap(imageBitmap);
                Model.instance.uploadImage(imageBitmap, finalAdvise.getId() + "advise_img" , (url)->{
                    Model.instance.setUserProfileImage(url,()->Log.d("USER", "IMAGE SUCCESSFULLY SAVED"));
                    this.finalAdvise.setPhotoUrl(url);
                });
            }
        }


    }
}