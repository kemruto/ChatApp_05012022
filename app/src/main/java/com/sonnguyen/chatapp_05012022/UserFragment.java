package com.sonnguyen.chatapp_05012022;

import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentUserBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;

public class UserFragment extends BaseFragment<UserViewModel, FragmentUserBinding> {

    private FragmentUserBinding binding;
    private PreferenceManager preferenceManager;
    private OnActionCallbackFragment callbackFragment;

    public static UserFragment newInstance() {
        return new UserFragment();
    }


    @Override
    protected FragmentUserBinding getBinding() {
        binding = FragmentUserBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {
        preferenceManager = new PreferenceManager(getContext());
    }

    @Override
    protected void initEvents() {

    }

    @Override
    protected Class<UserViewModel> getClassViewModel() {
        return UserViewModel.class;
    }

    public void setCallback(OnActionCallbackFragment callbackFragment){
        this.callbackFragment = callbackFragment;
    }
}