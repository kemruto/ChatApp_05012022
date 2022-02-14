package com.sonnguyen.chatapp_05012022.view;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CONVERSATION_TO_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CONVERSATION_TO_NEW_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_NAME;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_OPEN_USER_INFO;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_USER_TO_MAIN_TO_CHAT;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.View;

import androidx.lifecycle.Observer;

import com.sonnguyen.chatapp_05012022.adapters.ConversationAdapter;
import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentConservationBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.model.Conversation;
import com.sonnguyen.chatapp_05012022.model.User;
import com.sonnguyen.chatapp_05012022.utilities.MessageEvent;
import com.sonnguyen.chatapp_05012022.viewmodel.ConversationViewModel;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

public class ConversationFragment extends BaseFragment<ConversationViewModel, FragmentConservationBinding> implements OnActionCallbackFragment {

    private FragmentConservationBinding binding;
    private OnActionCallbackFragment callbackFragment;
    private ConversationAdapter conversationAdapter;

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
        binding.textName.setText(preferenceManager.getString(KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

        mViewModel.loadUser();
        mViewModel.loadRecentConversation();

        binding.conversationsRecyclerView.setVisibility(View.VISIBLE);
        binding.progressBar.setVisibility(View.INVISIBLE);
        conversationAdapter = new ConversationAdapter(mViewModel.getConversationArrayList(), getContext());
        conversationAdapter.setCallback(ConversationFragment.this);
        binding.conversationsRecyclerView.setAdapter(conversationAdapter);

        mViewModel.getConversations().observe(this, new Observer<ArrayList<Conversation>>() {
            @Override
            public void onChanged(ArrayList<Conversation> conversations) {
                Collections.sort(conversations, (obj1, obj2) -> obj2.getLastMessage().getDateObject().compareTo(obj1.getLastMessage().getDateObject()));
                conversationAdapter.notifyDataSetChanged();
            }
        });
    }

    private void signOut() {
        MessageEvent messageEvent = new MessageEvent("signout");
        EventBus.getDefault().post(messageEvent);
    }


    @Override
    protected void initEvents() {
        binding.imageProfile.setOnClickListener(v->callbackFragment.onActionCallback(KEY_OPEN_USER_INFO,null));
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

    @Override
    public void onActionCallback(String key, Object object) {
        switch (key) {
            case KEY_CONVERSATION_TO_CHAT:
                Conversation conversation = (Conversation) object;
                User user = conversation.getOtherUser();
                callbackFragment.onActionCallback(KEY_USER_TO_MAIN_TO_CHAT, user);
                break;
        }
    }
}