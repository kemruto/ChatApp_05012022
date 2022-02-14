package com.sonnguyen.chatapp_05012022.viewmodel;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CHAT;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_LAST_MESS;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.sonnguyen.chatapp_05012022.base.BaseViewModel;
import com.sonnguyen.chatapp_05012022.model.ChatMessage;

import java.util.ArrayList;
import java.util.Date;

public class ChatViewModel extends BaseViewModel {
    private MutableLiveData<ArrayList<ChatMessage>> messageLiveData;
    private DatabaseReference databaseReference;
    private ArrayList<ChatMessage> listMessage;
    private String chatRoom;

    public ChatViewModel() {
        messageLiveData = new MutableLiveData<>();
        listMessage = new ArrayList<>();
    }

    public void setChatRoom(String senderID, String receiverID) {
        if (senderID.compareTo(receiverID) > 0) {
            chatRoom = senderID + receiverID;
        } else {
            chatRoom = receiverID + senderID;
        }
    }

    public void sendMessage(String senderID, String receiverID, String message, String timeStamp, Date dateObject) {
        ChatMessage chatMessage = new ChatMessage(senderID, receiverID, message,null, timeStamp,dateObject);
        databaseReference.push().setValue(chatMessage).addOnCompleteListener(task -> {

        });
    }

    public void sendImage(String senderID, String receiverID,String image, String timeStamp, Date dateObject){
        ChatMessage chatMessage = new ChatMessage(senderID, receiverID, KEY_IMAGE,image, timeStamp,dateObject);
        databaseReference.push().setValue(chatMessage).addOnCompleteListener(task -> {

        });
    }

    public void loadMessage() {
        databaseReference = database.getReference().child(KEY_CHAT).child(chatRoom);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listMessage.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ChatMessage chatMessage = dataSnapshot.getValue(ChatMessage.class);
                    if (chatMessage != null) {
                        chatMessage.setMessageID(dataSnapshot.getKey());
                    }
                    listMessage.add(chatMessage);
                    DatabaseReference lastMessage = database
                            .getReference(KEY_CHAT)
                            .child(KEY_LAST_MESS)
                            .child(chatRoom);
                    lastMessage.setValue(chatMessage);
                }
                messageLiveData.postValue(listMessage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<ArrayList<ChatMessage>> getMessageLiveData() {
        return messageLiveData;
    }

    public ArrayList<ChatMessage> getMessages() {
        return listMessage;
    }
}