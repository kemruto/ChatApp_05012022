package com.sonnguyen.chatapp_05012022.view;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_COLLECTION_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_EMAIL;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_NAME;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_PASSWORD;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_USER_ID;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.TAG;

import android.content.Intent;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sonnguyen.chatapp_05012022.base.BaseActivity;
import com.sonnguyen.chatapp_05012022.databinding.ActivitySignInBinding;
import com.sonnguyen.chatapp_05012022.model.User;
import com.sonnguyen.chatapp_05012022.utilities.PreferenceManager;

public class SignInActivity extends BaseActivity<ActivitySignInBinding> {

    private ActivitySignInBinding binding;
    private FirebaseAuth mAuth;
    private PreferenceManager preferenceManager;
    private String email,password;

    @Override
    protected ActivitySignInBinding getBinding() {
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void initEvents() {
        binding.buttonSignIn.setOnClickListener(v -> {
            if (isValidSignInDetail()) {
                signIn();
            }
        });
        binding.textCreateNewAcount.setOnClickListener(v -> {
            Intent signUpIntent = new Intent(getApplicationContext(), SignUpActivity.class);
            startActivity(signUpIntent);
        });
    }

    private void signIn() {
        loading(true);
        email = binding.inputEmail.getText().toString();
        password = binding.inputPassword.getText().toString();

//        checkDataOnFirebase();
        logInWithGmailAuth();

    }

    private void logInWithGmailAuth() {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(task -> {
            if (task.isSuccessful()){
                FirebaseUser user = mAuth.getCurrentUser();
                updateUI(user);
            }else{
                showToast("Sign In Failed: "+task.getException().getMessage());
                loading(false);
            }
        });
    }

    private void checkDataOnFirebase() {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        database.collection(KEY_COLLECTION_USER)
                .whereEqualTo(KEY_EMAIL, email)
                .whereEqualTo(KEY_PASSWORD, password)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()
                            && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0) {
                        DocumentSnapshot documentSnapshot = task.getResult().getDocuments().get(0);
                        preferenceManager.putString(KEY_USER_ID, documentSnapshot.getId());
                        preferenceManager.putString(KEY_NAME, documentSnapshot.getString(KEY_NAME));
                        preferenceManager.putString(KEY_IMAGE, documentSnapshot.getString(KEY_IMAGE));
                        Log.i(TAG, "uid in sign in 1st: " +preferenceManager.getString(KEY_USER_ID));

                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    } else {
                        loading(false);
                        showToast("Can't load user's data from firebase");
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        loading(false);
        if (preferenceManager.getString(KEY_USER_ID)!=null){
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

    private void updateUI(FirebaseUser user) {
        checkDataOnRealtime(user);
//        checkDataOnFirebase();
    }

    private void checkDataOnRealtime(FirebaseUser user) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(KEY_COLLECTION_USER+"/"+user.getUid());

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User localUser = snapshot.getValue(User.class);
                if (localUser!=null){
                    preferenceManager.putString(KEY_USER_ID,localUser.getUserID());
                    preferenceManager.putString(KEY_NAME,localUser.getName());
                    preferenceManager.putString(KEY_IMAGE,localUser.getImage());
                    preferenceManager.putString(KEY_EMAIL,localUser.getEmail());
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                }else{
                    Log.e(TAG, "Get Data from firebase failed" );
                    mAuth.signOut();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Get Data from firebase failed: " +error.getMessage());
            }
        });
    }

    private Boolean isValidSignInDetail() {
        if (binding.inputEmail.getText().toString().trim().isEmpty()) {
            showToast("Enter email");
            binding.inputEmail.requestFocus();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()) {
            showToast("Enter valid email");
            binding.inputEmail.requestFocus();
            return false;
        } else if (binding.inputPassword.getText().toString().trim().isEmpty()) {
            showToast("Enter password");
            binding.inputPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }
}