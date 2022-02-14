package com.sonnguyen.chatapp_05012022.viewmodel;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_COLLECTION_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;

import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DatabaseReference;
import com.sonnguyen.chatapp_05012022.base.BaseViewModel;
import com.sonnguyen.chatapp_05012022.model.User;

import java.util.Objects;

public class UserInfoViewModel extends BaseViewModel {
    private DatabaseReference databaseRef;
    private MutableLiveData<User> userMutableLiveData;

    public UserInfoViewModel() {
        userMutableLiveData = new MutableLiveData<>();
        databaseRef = database.getReference().child(KEY_COLLECTION_USER);
    }

    public void updateData(String image) {
        databaseRef.child(firebaseAuth.getUid()).child(KEY_IMAGE).setValue(image);
    }

    public MutableLiveData<User> getUserImage() {
        return userMutableLiveData;
    }

    public void deleteAccount() {
        Objects.requireNonNull(firebaseAuth.getCurrentUser()).delete();
        databaseRef.child(Objects.requireNonNull(firebaseAuth.getUid())).setValue(null);
    }
}
