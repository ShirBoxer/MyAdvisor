package com.example.myadvisor.model;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.LinkedList;
import java.util.List;

public class ModelFirebase {
    final static String adviseCollection = "advises";
    final static String PHOTOS = "photos";


    private ModelFirebase(){} // mono state class

    public static FirebaseAuth getFirebaseAuth(){
        return FirebaseAuth.getInstance();
    }

    /* ################################# ---  User CRUD  --- ################################# */

    public static void addUser(User user, final Model.OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(user.getId())
                .set(user.toMap()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d("TAG","user added successfully");
                listener.onComplete();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG","fail adding student");
                listener.onComplete();
            }
        });
    }

    public static void getUser(final Model.GetUserListener listener){
        FirebaseFirestore.getInstance().collection(adviseCollection)
                .document(ModelFirebase.getFirebaseAuth().getCurrentUser().getEmail())
                .get().addOnCompleteListener((@NonNull Task<DocumentSnapshot> task)->{
            if(task.isSuccessful() && (task.getResult() != null)){
                User user = new User();
                user.fromMap(task.getResult().getData());
                listener.onComplete(user);
                return;
            }
            listener.onComplete(null);
        });
    }




    /* ################################# ---  Advise CRUD  --- ################################# */

    public interface GetAllAdvisesListener{
        public void onComplete(List<Advise> advises);
    }

    public static void getAllAdvises(Long since, GetAllAdvisesListener listener){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection(adviseCollection)
                .whereGreaterThanOrEqualTo(Advise.LAST_UPDATED, new Timestamp(since,0))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>(){
                    List<Advise> list = new LinkedList<Advise>();
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task){
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()){
                                list.add(Advise.createAdvise(document.getData()));
                            }
                        }else{

                        }
                        listener.onComplete(list);
                    }
                });
    }

    public static void saveAdvise(Advise advise, Model.OnCompleteListener listener) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(adviseCollection).document(advise.getId())
                .set(advise.toJson())
                .addOnSuccessListener((v) -> listener.onComplete())
                .addOnFailureListener((e) -> listener.onComplete());
    }
    public static void deleteAdvise(Advise advise, Model.OnCompleteListener listener) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection(adviseCollection).document(advise.getId()).delete()
                .addOnSuccessListener((v) -> listener.onComplete())
                .addOnFailureListener((e) -> listener.onComplete());;
    }


    /* ################################# ---  Utils  --- ################################# */


    public static void uploadImage(Bitmap imageBmp, String name, final Model.UploadImageListener listener){
        // get firebase storage instance (singleton)
        FirebaseStorage storage = FirebaseStorage.getInstance();
        // Create the path
        final StorageReference imagesRef = storage.getReference().child(PHOTOS).child(name);
        //
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //Compressing
        imageBmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();
        // Upload with Task
        UploadTask uploadTask = imagesRef.putBytes(data);
        uploadTask.addOnFailureListener((exception)->listener.onComplete(null))
                .addOnSuccessListener((taskSnapshot)-> {
                    imagesRef.getDownloadUrl().addOnSuccessListener((uri) -> {
                        Uri downloadUrl = uri;
                        listener.onComplete(downloadUrl.toString());
                    });
                });
    }



}
