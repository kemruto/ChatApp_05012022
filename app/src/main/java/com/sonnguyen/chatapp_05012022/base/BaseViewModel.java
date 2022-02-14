package com.sonnguyen.chatapp_05012022.base;

import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public abstract class BaseViewModel extends ViewModel {
    protected final FirebaseDatabase database = FirebaseDatabase.getInstance();
    protected final FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
}
