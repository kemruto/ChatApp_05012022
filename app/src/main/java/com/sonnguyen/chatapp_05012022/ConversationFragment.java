package com.sonnguyen.chatapp_05012022;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CONVERSATION_TO_NEW_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_NAME;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_PHOTO_URL;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.utilities.Constants;
import com.sonnguyen.chatapp_05012022.utilities.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;

@SuppressLint("NonConstantResourceId")
public class ConversationFragment extends BaseFragment<ConversationViewModel> {

    @BindView(R.id.imageSignOut)
    ImageView imageSignOut;
    @BindView(R.id.fabNewChat)
    FloatingActionButton floatingActionButton;
    @BindView(R.id.imageProfile)
    ImageView imageProfile;
    @BindView(R.id.textName)
    TextView textName;
    private OnActionCallbackFragment callbackFragment;
    private String name, bitmapPhoto;

    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            name = bundle.getString(KEY_NAME);
            bitmapPhoto = bundle.getString(KEY_PHOTO_URL);

            textName.setText(name);
        }
    }


    private void signOut() {
        sendMessageToMain();
    }

    private void sendMessageToMain() {
        MessageEvent messageEvent = new MessageEvent("deleteSuccess");
        EventBus.getDefault().post(messageEvent);
    }

    @Override
    protected void initEvents() {
        imageSignOut.setOnClickListener(v -> signOut());
        floatingActionButton.setOnClickListener(v -> callbackFragment.onActionCallback(KEY_CONVERSATION_TO_NEW_USER, null));
    }

    public void setCallBack(OnActionCallbackFragment callbackFragment) {
        this.callbackFragment = callbackFragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_conservation;
    }

    @Override
    protected Class getClassViewModel() {
        return ConversationViewModel.class;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(Constants.TAG, "onDestroyView Fragment");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
        Log.i(Constants.TAG, "onDestroy fragment");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(Constants.TAG, "onDetach Fragment");
    }
}