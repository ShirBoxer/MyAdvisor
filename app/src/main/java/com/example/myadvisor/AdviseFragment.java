package com.example.myadvisor;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;
import com.squareup.picasso.Picasso;


public class AdviseFragment extends Fragment {
    TextView headerTv;
    TextView descriptionTv;
    ImageView adviseImageIv;
    Button editBtn;
    Button deleteBtn;
    FeedViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view =  inflater.inflate(R.layout.fragment_advise, container, false);
        headerTv = view.findViewById(R.id.edit_advise_f_header_et);
        descriptionTv =  view.findViewById(R.id.edit_advise_f_description_et);
        adviseImageIv =  view.findViewById(R.id.advise_f_image_iv);
        editBtn =  view.findViewById(R.id.advise_f_edit_btn);
        deleteBtn =  view.findViewById(R.id.advise_f_delete_btn);
        viewModel = new ViewModelProvider(this).get(FeedViewModel.class);

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
        String owner = Model.instance.getAuthManager().getCurrentUser().getEmail();
        if(owner != null && advise.getOwner().equals(owner)){
            editBtn.setVisibility(View.VISIBLE);
            deleteBtn.setVisibility(View.VISIBLE);

            editBtn.setOnClickListener((v)->{
                AdviseFragmentDirections.ActionAdviseFragmentToEditAdviseFragment2 action =
                        AdviseFragmentDirections.actionAdviseFragmentToEditAdviseFragment2(adviseId);
                Navigation.findNavController(view).navigate(action);
            });

            Advise finalAdvise = advise;
            deleteBtn.setOnClickListener((v)->{
                Model.instance.deleteAdvise(finalAdvise,()-> Log.d("ADVISE", "Advise " + adviseId + "deleted "));
                Navigation.findNavController(view).navigateUp();

            });
        }


        headerTv.setText(advise.getName());
        descriptionTv.setText(advise.getDescription());
        Picasso.get().load(advise.getPhotoUrl()).into(adviseImageIv);



        return view;
    }
}