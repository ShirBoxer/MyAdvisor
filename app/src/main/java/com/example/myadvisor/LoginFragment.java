package com.example.myadvisor;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.myadvisor.model.ModelFirebase;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class LoginFragment extends Fragment {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button loginBtn;
    EditText emailEt;
    EditText passwordEt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn = view.findViewById(R.id.login_f_login_btn);
        emailEt = view.findViewById(R.id.login_f_email);
        passwordEt = view.findViewById(R.id.login_f_password);

        loginBtn.setOnClickListener((v)->{
            String email = emailEt.getText().toString().trim();
            String password = passwordEt.getText().toString().trim();
            if(email.isEmpty() || !email.matches(emailPattern)){
                emailEt.setError("Please enter correct email.");
                return;
            }
            if(password.length() <6){
                passwordEt.setError("Please enter the right password.");
                return;
            }

            ModelFirebase.getFirebaseAuth()
                    .signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener((@NonNull Task<AuthResult> task)-> {
                        if (task.isSuccessful()) {
                            CharSequence text = "Logged in Successfully";
                            Log.d("TAG", "success");
                            Toast.makeText(getContext(),text , Toast.LENGTH_LONG).show();
                            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_feedFragment);
                        } else{
                            //TODO : toast?
                            Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("TAG", "fail");
                        }
                    });
        });
        return view;
    }
}