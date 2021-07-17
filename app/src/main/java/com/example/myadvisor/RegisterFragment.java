package com.example.myadvisor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.myadvisor.model.Model;
import com.example.myadvisor.model.ModelFirebase;
import com.example.myadvisor.model.User;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class RegisterFragment extends Fragment {

    EditText fullName;
    EditText email;
    EditText password;
    EditText password2;
    EditText phone;
    Button regBtn;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    ProgressBar pb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        fullName=view.findViewById(R.id.register_f_full_name);
        email=view.findViewById(R.id.register_f_email);
        password=view.findViewById(R.id.register_f_password);
        password2=view.findViewById(R.id.register_f_password2);
        regBtn=view.findViewById(R.id.register_f_reg_btn);
        phone=view.findViewById(R.id.register_f_phone);
        pb = view.findViewById(R.id.register_f_pb);
        //User logged out & rolled back to main fragment
        if (MainActivity.bottomNavigationView != null)
            MainActivity.bottomNavigationView.setVisibility(View.GONE);

        regBtn.setOnClickListener((v) -> {
            String name=fullName.getText().toString().trim();
            String mail=email.getText().toString().trim();
            String pass1=password.getText().toString().trim();
            String pass2=password2.getText().toString().trim();
            String phoneNum=phone.getText().toString().trim();
            //Input validation
            if(TextUtils.isEmpty(name))
            {
                fullName.setError("please enter full name");
                return;
            }
            if(TextUtils.isEmpty(phoneNum))
            {
                phone.setError("please enter a phone number");
                return;
            }
            if(TextUtils.isEmpty(mail) || !mail.matches(emailPattern))
            {
                email.setError("please enter  correct   email");
                return;
            }
            if(TextUtils.isEmpty(pass1))
            {
                password.setError("please enter a  password");
                return;
            }

            if((pass1.compareTo(pass2))!=0)
            {

                password.setError("Please enter identical passwords");
                return;
            }
            if(pass1.length()<6)
            {
                password.setError("Password must be at least 6 characters long");
                return;
            }
            pb.setVisibility(View.VISIBLE);
            regBtn.setEnabled(false);


            //TODO: more checks

            ModelFirebase.getFirebaseAuth().createUserWithEmailAndPassword(mail, pass1).addOnCompleteListener((@NonNull Task<AuthResult> task)->{
                if(task.isSuccessful()) {
                    User user = new User();
                    user.setId(mail);
                    user.setName(name);
                    user.setPhoneNumber(phoneNum);
                    Model.instance.addUser(user, () -> {
                        Log.d("TAG", "A new user was asserted to the db " + mail);
                    });
                    Toast.makeText(getContext(), "user registered", Toast.LENGTH_LONG).show();
                    Navigation.findNavController(view).navigate(R.id.action_registerFragment_to_loginFragment);

                }

                else {
                    regBtn.setEnabled(true);
                    Log.d("TAG", "failed to creat a new user");
                    Toast.makeText(getContext(), "Aawwww.. something went wrong, please try again", Toast.LENGTH_LONG).show();
                }
            });

        });

        return view;
    }
}