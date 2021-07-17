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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myadvisor.model.Model;
import com.example.myadvisor.model.User;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;
import static com.example.myadvisor.AddAdviseFragment.REQUEST_IMAGE_CAPTURE;


public class ProfileFragment extends Fragment {
    TextView nameTv;
    TextView phoneNumTv;
    Button myAdvisesBtn;
    Button logoutBtn;
    Button deleteAccountBtn;
    ImageView userImgIv;
    ImageButton addImgBtn;

    User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_profile, container, false);

        nameTv = view.findViewById(R.id.profile_f_user_name_tv);
        phoneNumTv = view.findViewById(R.id.profile_f_phone_tv);
        myAdvisesBtn = view.findViewById(R.id.profile_f_advises_btn);
        logoutBtn = view.findViewById(R.id.profile_f_logout_btn);
        deleteAccountBtn = view.findViewById(R.id.profile_f_delete_btn);
        userImgIv = view.findViewById(R.id.profile_f_user_img_iv);
        addImgBtn = view.findViewById(R.id.profile_f_img_btn_btn);

        Model.instance.getUser((User user)->{
            this.user = user;
            nameTv.setText(user.getName());
            phoneNumTv.setText(user.getPhoneNumber());
            Picasso.get()
                    .load(user.getImageUrl())
                    .placeholder(R.drawable.circle)
                    .error(R.drawable.ic_menu_gallery)
                    .into(userImgIv);
        });

        addImgBtn.setOnClickListener((v)->{
            takePicture();
        });

        myAdvisesBtn.setOnClickListener((v)->{
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_userAdvisesFragment);
        });


        logoutBtn.setOnClickListener((v)->{
            Model.instance.signOut();
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_mainFragment);
        });

        deleteAccountBtn.setOnClickListener((v)->{
            Model.instance.deleteAccount();
            Navigation.findNavController(view).navigate(R.id.action_profileFragment_to_mainFragment);

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
                userImgIv.setImageBitmap(imageBitmap);
                Model.instance.uploadImage(imageBitmap, user.getId() + "_profile_img" , (url)->{
                    Model.instance.setUserProfileImage(url,()->Log.d("USER", "IMAGE SUCCESSFULLY SAVED"));
                    this.user.setImageUrl(url);
                });
            }
        }


    }
}