package com.example.myadvisor;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.myadvisor.model.Model;
import com.example.myadvisor.model.ModelFirebase;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;


public class LoginFragment extends Fragment {
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    Button loginBtn;
    EditText emailEt;
    EditText passwordEt;
    ProgressBar pb;
    TextView registerTv;
    TextView forgotPassTv;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        loginBtn = view.findViewById(R.id.login_f_login_btn);
        emailEt = view.findViewById(R.id.login_f_email);
        passwordEt = view.findViewById(R.id.login_f_password);
        pb = view.findViewById(R.id.login_f_pb);
        registerTv = view.findViewById(R.id.loginfrag_register);
        forgotPassTv = view.findViewById(R.id.loginFrag_forgotPassword);

        //User logged out & rolled back to main fragment
        if (MainActivity.bottomNavigationView != null)
            MainActivity.bottomNavigationView.setVisibility(View.GONE);

        registerTv.setOnClickListener((v)->{
            Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_registerFragment);
        });

        loginBtn.setOnClickListener((v)->{
            pb.setVisibility(View.VISIBLE);
            loginBtn.setEnabled(false);
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
                            loginBtn.setEnabled(true);
                            Toast.makeText(getContext(), "Error! " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            Log.d("TAG", "fail");
                        }
                    });
        });

        forgotPassTv.setOnClickListener((v)->{
            EditText resetMail=new EditText(v.getContext());
            AlertDialog.Builder passwordRestDialog=new AlertDialog.Builder(v.getContext());
            passwordRestDialog.setTitle("Reset password ?");
            passwordRestDialog.setMessage("Enter Your Email To Receive Reset Link");
            passwordRestDialog.setView(resetMail);

            passwordRestDialog.setPositiveButton("Send", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //extract the email and sent reset link
                    String mail=resetMail.getText().toString();
                    Model.instance.getAuthManager().sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getContext(),"Reset Link Sent To Your Email",Toast.LENGTH_LONG).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getContext(),"Error ! Reset Link did not Sent !",Toast.LENGTH_LONG).show();
                            Log.d("PASSWORD", "Reset password  failed: " + e.getMessage());

                        }
                    });

                }
            } );

            passwordRestDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //close the dialog
                }
            });
            passwordRestDialog.create().show();


        });
        return view;
    }
}