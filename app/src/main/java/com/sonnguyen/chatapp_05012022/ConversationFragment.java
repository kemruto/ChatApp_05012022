package com.sonnguyen.chatapp_05012022;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CONVERSATION_TO_NEW_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_NAME;

import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentConservationBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.utilities.MessageEvent;
import com.squareup.picasso.Picasso;

import org.greenrobot.eventbus.EventBus;

public class ConversationFragment extends BaseFragment<ConversationViewModel, FragmentConservationBinding> {

    private FragmentConservationBinding binding;
    private OnActionCallbackFragment callbackFragment;
    private String name, bitmapPhoto;
    private PreferenceManager preferenceManager;

    public static ConversationFragment newInstance() {
        return new ConversationFragment();
    }

    @Override
    protected FragmentConservationBinding getBinding() {
        binding = FragmentConservationBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {
        preferenceManager = new PreferenceManager(getContext());
        binding.textName.setText(preferenceManager.getString(KEY_NAME));
        Picasso.get().load(preferenceManager.getString(KEY_IMAGE)).into(binding.imageProfile);
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
        binding.imageSignOut.setOnClickListener(v -> signOut());
        binding.fabNewChat.setOnClickListener(v -> callbackFragment.onActionCallback(KEY_CONVERSATION_TO_NEW_USER, null));
    }

    public void setCallBack(OnActionCallbackFragment callbackFragment) {
        this.callbackFragment = callbackFragment;
    }

    @Override
    protected Class getClassViewModel() {
        return ConversationViewModel.class;
    }

}