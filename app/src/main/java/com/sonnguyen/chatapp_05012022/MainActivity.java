package com.sonnguyen.chatapp_05012022;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.sonnguyen.chatapp_05012022.base.BaseActivity;
import com.sonnguyen.chatapp_05012022.databinding.ActivityMainBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.utilities.Constants;
import com.sonnguyen.chatapp_05012022.utilities.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.ByteArrayOutputStream;
import java.net.URL;

;

public class MainActivity extends BaseActivity<ActivityMainBinding> implements OnActionCallbackFragment {

    private ActivityMainBinding binding;
    private GoogleSignInClient googleSignInClient;
    private PreferenceManager preferenceManager;

    @Override
    protected void initData() {
        preferenceManager = new PreferenceManager(getApplicationContext());
        EventBus.getDefault().register(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.defaul_web_client_id))
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(this, gso);
        showConversationFragment();
    }

    @Override
    protected ActivityMainBinding getBinding() {
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        return binding;
    }

    private void showConversationFragment() {
        ConversationFragment conversationFragment = ConversationFragment.newInstance();
        conversationFragment.setCallBack(this);
        showFragment(binding.hostFragment, conversationFragment, false);
    }

    @Override
    protected void initEvents() {

    }

    private String getStringFromByteArray(String url) {
        URL newurl;
        Bitmap bitmap;
        String base64 = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            newurl = new URL(url);
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return base64;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        FirebaseAuth.getInstance().signOut();
        googleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(Constants.TAG, "Sign out success");
                preferenceManager.clear();
                finish();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(Constants.TAG, "Sign out failed");
            }
        });
    }

    private void showFragment(FragmentContainerView containerView, Fragment fragment, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(containerView.getId(), fragment);
        if (addToBackStack) fragmentTransaction.addToBackStack("add");
        fragmentTransaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(TAG, "onDestroy MainActivity: ");
        EventBus.getDefault().unregister(this);
    }


    @Override
    public void onActionCallback(String key, Object object) {
        switch (key) {
            case Constants.KEY_CONVERSATION_TO_NEW_USER:
                UserFragment userFragment = UserFragment.newInstance();
                userFragment.setCallback(this);
                showFragment(binding.hostFragment, userFragment, true);
        }
    }

}