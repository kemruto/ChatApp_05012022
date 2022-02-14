package com.sonnguyen.chatapp_05012022.view;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CONVERSATION_TO_NEW_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEYBUNDLE_USER_DATA_TO_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_OPEN_USER_INFO;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_USER_TO_MAIN_TO_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.TAG;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.firebase.auth.FirebaseAuth;
import com.sonnguyen.chatapp_05012022.utilities.PreferenceManager;
import com.sonnguyen.chatapp_05012022.base.BaseActivity;
import com.sonnguyen.chatapp_05012022.databinding.ActivityMainBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.model.User;
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
        if (messageEvent.message=="delete account"){
            showToast("Delete Success");
        }else{
            showToast("Signing out.....");
        }
        preferenceManager.clear();
        FirebaseAuth.getInstance().signOut();
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
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
            case KEY_CONVERSATION_TO_NEW_USER:
                UserFragment userFragment = UserFragment.newInstance();
                userFragment.setCallback(this);
                showFragment(binding.hostFragment, userFragment, true);
                break;
            case KEY_USER_TO_MAIN_TO_CHAT:
                User user = (User) object;
                Bundle bundle = new Bundle();
                bundle.putSerializable(KEYBUNDLE_USER_DATA_TO_CHAT, user);
                ChatFragment chatFragment = ChatFragment.newInstance();
                chatFragment.setArguments(bundle);
                showFragment(binding.hostFragment, chatFragment, true);
                break;
            case  KEY_OPEN_USER_INFO:
                UserInfoFragment userInfoFragment = UserInfoFragment.newInstance();
                showFragment(binding.hostFragment,userInfoFragment,true);
                break;
        }
    }

}