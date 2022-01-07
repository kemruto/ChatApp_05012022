package com.sonnguyen.chatapp_05012022.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class BaseFragment<T extends BaseViewModel> extends Fragment {
    private View mView;
    protected T mViewModel;
    protected Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mView = null;
        if (getLayoutId() != 0) {
            mView = inflater.inflate(getLayoutId(), container, false);
            unbinder = ButterKnife.bind(this, mView);
            mViewModel = new ViewModelProvider(this).get(getClassViewModel());
            initData();
            initEvents();
        }
        return mView;
    }

    protected abstract void initData();

    protected abstract void initEvents();

    protected abstract int getLayoutId();

    protected abstract Class<T> getClassViewModel();

}
