package com.example.myadvisor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.myadvisor.model.Advise;
import com.example.myadvisor.model.Model;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import java.util.List;


public class UserAdvisesFragment extends Fragment {
    LiveData<List<Advise>> userAdvisesList;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefresh;
    UserAdvisesViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_user_advises, container, false);
        viewModel = new ViewModelProvider(this).get(UserAdvisesViewModel.class);

        RecyclerView recyclerView = view.findViewById(R.id.user_advises_f_recycler);
        //better performance
        recyclerView.setHasFixedSize(true);

        RecyclerView.LayoutManager manager = new LinearLayoutManager(this.getContext());
        recyclerView.setLayoutManager(manager);

        UserAdvisesFragment.MyAdapter adapter = new UserAdvisesFragment.MyAdapter();
        recyclerView.setAdapter(adapter);
        userAdvisesList = viewModel.getUserAdvisesList();

        adapter.setOnItemClickListener((int position)->{
            String adviseId = userAdvisesList.getValue().get(position).getId();
            UserAdvisesFragmentDirections.ActionUserAdvisesFragmentToAdviseFragment action =
                    UserAdvisesFragmentDirections.actionUserAdvisesFragmentToAdviseFragment(adviseId);
            Navigation.findNavController(view).navigate(action);
        });



        BottomNavigationView navBar = getActivity().findViewById(R.id.bottom_navigation);
        navBar.setVisibility(View.VISIBLE);
        swipeRefresh = view.findViewById(R.id.user_advises_f_swiperefresh);
        swipeRefresh.setOnRefreshListener(() -> {
            Model.instance.getAllAdvises();
            ; //TODO: CREATE REFRESH FUNCTION IN THE VIEWMODEL OBJECT
        });
        // set progressBar and swipeRefresh states
        setupProgressListener();
        // observe liveData object on start and resume
        // notify adapter when posts list arrive
        viewModel.getUserAdvisesList()
                .observe(getViewLifecycleOwner(), (aPList) -> adapter.notifyDataSetChanged());


        return view;
    }

    private void setupProgressListener() {
        Model.instance.advisesLoadingState.observe(getViewLifecycleOwner(), (state) -> {
            switch (state) {
                case loaded:
                    //progressBar.setVisibility(View.GONE);
                    swipeRefresh.setRefreshing(false);
                    break;
                case loading:
                    //progressBar.setVisibility(View.VISIBLE);
                    swipeRefresh.setRefreshing(true);
                    break;
                case error:
                    //TODO: display error message (toast)
            }
        });
    }

    // Breaking inner connection between parent to this class
    // Saving layout components for further use.
    static class MyViewHolder extends RecyclerView.ViewHolder {
        UserAdvisesFragment.OnItemClickListener listener;
        TextView stateTv;
        TextView descriptionTv;
        ImageView picIv;


        public MyViewHolder(@NonNull View itemView, UserAdvisesFragment.OnItemClickListener listener) {
            super(itemView);
            stateTv = itemView.findViewById(R.id.advise_list_row_header);
            descriptionTv = itemView.findViewById(R.id.advise_list_row_description);
            picIv = itemView.findViewById(R.id.advise_list_row_img);
            this.listener = listener;
            itemView.setOnClickListener((v)->{
                if(listener != null){
                    int position = getAdapterPosition();
                    if(position != RecyclerView.NO_POSITION){
                        listener.onClick(position);
                    }
                }
            });



        }

        public void bind(Advise advise) {
            stateTv.setText(advise.getName());
            descriptionTv.setText(advise.getDescription());
            picIv.setImageResource(R.drawable.ic_menu_gallery);
            String url = advise.getPhotoUrl();
            if ((url != null) && (!url.equals(""))) {
                Picasso.get()
                        .load(url)
                        .placeholder(R.drawable.ic_menu_gallery)
                        .error(R.drawable.ic_menu_gallery)
                        .into(picIv);
            }
        }
    }

    public interface OnItemClickListener{
        void onClick(int position);
    }
    // Managing View logic.
    class MyAdapter extends RecyclerView.Adapter<UserAdvisesFragment.MyViewHolder> {
        UserAdvisesFragment.OnItemClickListener listener;

        public void setOnItemClickListener(UserAdvisesFragment.OnItemClickListener listener){
            this.listener = listener;
        }

        @NonNull
        @Override
        public UserAdvisesFragment.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = getLayoutInflater().inflate(R.layout.advise_list_row, parent, false);
            UserAdvisesFragment.MyViewHolder holder = new UserAdvisesFragment.MyViewHolder(view, listener);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull UserAdvisesFragment.MyViewHolder holder, int position) {
            Advise advise = userAdvisesList.getValue().get(position);
            holder.bind(advise);

        }

        // get number of
        @Override
        public int getItemCount() {
            List<Advise> sl = userAdvisesList.getValue();
            return (sl == null) ? 0 : sl.size();
        }
    }

}