package com.sonnguyen.chatapp_05012022.view;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_COLLECTION_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_USER_ID;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_USER_TO_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_USER_TO_MAIN_TO_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.TAG;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sonnguyen.chatapp_05012022.viewmodel.UserViewModel;
import com.sonnguyen.chatapp_05012022.adapters.UserAdapter;
import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentUserBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserFragment extends BaseFragment<UserViewModel, FragmentUserBinding> implements OnActionCallbackFragment {


    private FragmentUserBinding binding;
    private OnActionCallbackFragment callbackFragment;
    private UserAdapter userAdapter;
    private List<User> listUsers;

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
        mViewModel.setContext(getContext());
        getListUsers();
    }

    private void getListUsers() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference(KEY_COLLECTION_USER);

        listUsers = new ArrayList<>();

        String currentUserID = preferenceManager.getString(KEY_USER_ID);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot querySnapshot : dataSnapshot.getChildren()) {
                    User user = querySnapshot.getValue(User.class);
                    if (user != null) {
                        if (currentUserID.equals(user.getUserID())) {
                            continue;
                        }
                        listUsers.add(user);
                        userAdapter = new UserAdapter(listUsers, getContext());
                        userAdapter.setCallback(UserFragment.this);
                        binding.usersRecyclerView.setAdapter(userAdapter);
                        binding.usersRecyclerView.setVisibility(View.VISIBLE);
                        loading(false);
                    } else {
                        Log.e(TAG, "Get Data from firebase failed");
                        showErrorMessage();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Get Data from firebase failed: " + error.getMessage());
            }
        });
    }

    private void showErrorMessage() {
        loading(false);
        binding.textErrorMessage.setText("No user");
        binding.textErrorMessage.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        binding.imageBack.setOnClickListener(v -> {
            getFragmentManager().popBackStack();
        });
    }

    @Override
    protected Class<UserViewModel> getClassViewModel() {
        return UserViewModel.class;
    }

    public void setCallback(OnActionCallbackFragment callbackFragment) {
        this.callbackFragment = callbackFragment;
    }

    private void loading(Boolean isLoading) {
        if (isLoading) {
            binding.progressBar.setVisibility(View.VISIBLE);
        } else {
            binding.progressBar.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onActionCallback(String key, Object object) {
        switch (key) {
            case KEY_USER_TO_CHAT:
                User user = (User) object;
                callbackFragment.onActionCallback(KEY_USER_TO_MAIN_TO_CHAT, user);
                break;
        }
    }
}