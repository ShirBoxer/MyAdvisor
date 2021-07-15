package com.example.myadvisor;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;

import static android.app.Activity.RESULT_OK;


public class AddAdviseFragment extends Fragment {
    ImageButton addPictureBtn;
    EditText nameEt;
    EditText descriptionEt;
    Button createBtn;
    ProgressBar pb;
    static final int REQUEST_IMAGE_CAPTURE = 1;
    Bitmap imageBitmap;

    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_advise, container, false);
        addPictureBtn = view.findViewById(R.id.add_advise_f_img_btn);
        nameEt = view.findViewById(R.id.add_advise_f_name_et);
        descriptionEt = view.findViewById(R.id.add_advise_f_description_et);
        createBtn = view.findViewById(R.id.add_advise_f_create_btn);


        addPictureBtn.setOnClickListener((v)->{
            takePicture();
        });


        createBtn.setOnClickListener((v)->{
            if(nameEt.getText().toString().isEmpty()) {
                nameEt.setError("Please Enter your advise header");
                return;
            }
            save();

        });

        return view;
    }
    private void takePicture(){

        Intent takePictureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

    }

    // request code ~ the number of Operation, (REQUEST_IMAGE_CAPTURE=1)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        if (requestCode == REQUEST_IMAGE_CAPTURE){
            if (resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                imageBitmap = (Bitmap) extras.get("data");
                addPictureBtn.setImageBitmap(imageBitmap);
            }
        }

    }

    void save(){
        //pb.setVisibility(View.VISIBLE);
        createBtn.setEnabled(false);
        addPictureBtn.setEnabled(false);

        if(imageBitmap != null){
            Model.instance.uploadImage(imageBitmap, nameEt.getText().toString(), (url) ->{
                saveAdvise(url);
            });
        }else{
            saveAdvise(null);
        }


    }

    void saveAdvise(String url){
        String name = nameEt.getText().toString().trim();
        String description = descriptionEt.getText().toString().trim();
        String photoUrl;
        String id = System.currentTimeMillis() + "";
        if (url == null)
            photoUrl = "";
        else
            photoUrl = url;

        //TODO: take uid from ModelFirebase!!!!
        Advise advise = new Advise(id, name, description, photoUrl);
        //Post post = new Post(id, description, "New Advise Created By" + FirebaseAuth.getInstance().getCurrentUser().getDisplayName(), photoUrl);

        Model.instance.saveAdvise(advise, ()->{
            //TODO TOAST
            Navigation.findNavController(view).navigate(R.id.action_addAdviseFragment_to_feedFragment);
        });

    }


}