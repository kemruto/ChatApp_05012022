package com.sonnguyen.chatapp_05012022.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewbinding.ViewBinding;

import com.sonnguyen.chatapp_05012022.utilities.PreferenceManager;

public abstract class BaseFragment<T extends BaseViewModel,Binding extends ViewBinding> extends Fragment {
    protected T mViewModel;
    private Binding binding;
    protected PreferenceManager preferenceManager;

    protected abstract Binding getBinding();

    private void initBinding(){
        this.binding = getBinding();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        this.initBinding();
        preferenceManager = new PreferenceManager(getContext());
        mViewModel = new ViewModelProvider(this).get(getClassViewModel());
        initData();
        initEvents();
        return binding.getRoot();
    }

    protected abstract void initData();

    protected abstract void initEvents();

    protected abstract Class<T> getClassViewModel();

}
