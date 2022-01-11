package com.sonnguyen.chatapp_05012022;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_EMAIL;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_NAME;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.RC_SIGN_IN;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.TAG;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sonnguyen.chatapp_05012022.base.BaseActivity;
import com.sonnguyen.chatapp_05012022.databinding.ActivitySignInBinding;

public class SignInActivity extends BaseActivity<ActivitySignInBinding> {

    private ActivitySignInBinding binding;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private Intent signInIntent;
    private PreferenceManager preferenceManager;

    @Override
    protected ActivitySignInBinding getBinding() {
        binding = ActivitySignInBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        signInIntent = new Intent(getApplicationContext(), MainActivity.class);
        createRequest();
    }

    private void createRequest() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.defaul_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    protected void initEvents() {
        binding.textLoginWithGmail.setOnClickListener(v -> signInWithGmail());
        binding.buttonSignIn.setOnClickListener(v->{
            if (isValidSignInDetail()){
                signIn();
            }
        });
        binding.textCreateNewAcount.setOnClickListener(v->{
            Intent signUpIntent = new Intent(getApplicationContext(),SignUpActivity.class);
            startActivity(signUpIntent);
        });
    }

    private void signIn() {
        String email = binding.inputEmail.getText().toString();
        String password = binding.inputPassword.getText().toString();
        loading(true);
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    FirebaseUser user = mAuth.getCurrentUser();
                    updateUI(user);
                    Intent signInIntent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(signInIntent);
                }else{
                    showToast("Sign In Failed: "+task.getException().getMessage());
                    loading(false);
                }

            }
        });
    }

    private void loading(Boolean isLoading){
        if(isLoading){
            binding.buttonSignIn.setVisibility(View.INVISIBLE);
            binding.progressBar.setVisibility(View.VISIBLE);
        }else{
            binding.buttonSignIn.setVisibility(View.VISIBLE);
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        loading(false);
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            updateUI(currentUser);
            startActivity(signInIntent);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                String idToken = account.getIdToken();
                firebaseAuthWithGoogle(idToken);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.i(TAG, "Google sign in failed", e);
                Toast.makeText(getApplicationContext(), "Google sign in failed", Toast.LENGTH_SHORT).show();
                updateUI(null);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.i(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            updateUI(user);
                            startActivity(signInIntent);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.i(TAG, "signInWithCredential:failure", task.getException());
                            updateUI(null);
                        }
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        String url = null;
        Uri urlPhoto = user.getPhotoUrl();
        if (urlPhoto != null) {
            url = urlPhoto.toString();
        }else{

        }
        preferenceManager.putString(KEY_NAME,user.getDisplayName());
        preferenceManager.putString(KEY_IMAGE,url);
        preferenceManager.putString(KEY_EMAIL,user.getEmail());
    }

    private Boolean isValidSignInDetail(){
        if(binding.inputEmail.getText().toString().trim().isEmpty()){
            showToast("Enter email");
            binding.inputEmail.requestFocus();
            return false;
        }else if(!Patterns.EMAIL_ADDRESS.matcher(binding.inputEmail.getText().toString()).matches()){
            showToast("Enter valid email");
            binding.inputEmail.requestFocus();
            return false;
        }else if(binding.inputPassword.getText().toString().trim().isEmpty()){
            showToast("Enter password");
            binding.inputPassword.requestFocus();
            return false;
        }
        return true;
    }

    private void showToast(String message){
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    private void signInWithGmail() {
        Intent intent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(intent, RC_SIGN_IN);
    }
}