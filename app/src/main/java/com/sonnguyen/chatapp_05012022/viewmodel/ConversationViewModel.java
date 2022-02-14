package com.sonnguyen.chatapp_05012022.viewmodel;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_COLLECTION_USER;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_LAST_MESS;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sonnguyen.chatapp_05012022.base.BaseViewModel;
import com.sonnguyen.chatapp_05012022.model.ChatMessage;
import com.sonnguyen.chatapp_05012022.model.Conversation;
import com.sonnguyen.chatapp_05012022.model.User;

import java.util.ArrayList;

public class ConversationViewModel extends BaseViewModel {
    private ArrayList<User> userArrayList;
    private ArrayList<Conversation> listConversation;
    private MutableLiveData<ArrayList<User>> mutableUser;
    private MutableLiveData<ArrayList<Conversation>> mutableConversation;
    private DatabaseReference databaseRef;
    private DatabaseReference lastMessReference;

    public ConversationViewModel() {
        userArrayList = new ArrayList<>();
        listConversation = new ArrayList<>();
        mutableUser = new MutableLiveData<>();
        mutableConversation = new MutableLiveData<>();
        databaseRef = database.getReference().child(KEY_COLLECTION_USER);
        lastMessReference = database.getReference(KEY_CHAT).child(KEY_LAST_MESS);
    }

    public void loadUser() {
        databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    userArrayList.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user.getUserID().equals(firebaseAuth.getCurrentUser().getUid())) {
                        } else {
                            userArrayList.add(user);
                        }
                    }
                    mutableUser.postValue(userArrayList);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void loadRecentConversation() {
        lastMessReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listConversation.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage.getReceiverID().equals(firebaseAuth.getCurrentUser().getUid())) {
                        for (User u : userArrayList) {
                            if (u.getUserID().equals(chatMessage.getSenderID())) {
                                listConversation.add(new Conversation(chatMessage, u));
                            }
                        }
                    } else if (chatMessage.getSenderID().equals(firebaseAuth.getCurrentUser().getUid())) {
                        for (User u : userArrayList) {
                            if (u.getUserID().equals(chatMessage.getReceiverID())) {
                                listConversation.add(new Conversation(chatMessage, u));
                            }
                        }
                    }
                    mutableConversation.postValue(listConversation);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<ArrayList<Conversation>> getConversations() {
        return mutableConversation;
    }

    public ArrayList<Conversation> getConversationArrayList() {
        return listConversation;
    }

}