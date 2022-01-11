package com.sonnguyen.chatapp_05012022;

import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentChatBinding;

public class ChatFragment extends BaseFragment<ChatViewModel,FragmentChatBinding> {

    private FragmentChatBinding binding;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    protected FragmentChatBinding getBinding() {
        binding = FragmentChatBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected Class<ChatViewModel> getClassViewModel() {
        return ChatViewModel.class;
    }

}