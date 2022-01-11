package com.sonnguyen.chatapp_05012022.base;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

public abstract class BaseActivity <Binding extends ViewBinding> extends AppCompatActivity {
    private Binding binding;

    protected abstract Binding getBinding();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initBinding();
        setContentView(binding.getRoot());
        initData();
        initEvents();
    }

    private void initBinding(){
        this.binding = getBinding();
    }


    protected abstract void initData();
    protected abstract void initEvents();
}
