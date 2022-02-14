package com.sonnguyen.chatapp_05012022.adapters;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_CONVERSATION_TO_CHAT;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sonnguyen.chatapp_05012022.databinding.ItemContainerConversationBinding;
import com.sonnguyen.chatapp_05012022.listeners.OnActionCallbackFragment;
import com.sonnguyen.chatapp_05012022.model.ChatMessage;
import com.sonnguyen.chatapp_05012022.model.Conversation;
import com.sonnguyen.chatapp_05012022.model.User;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ConversationAdapter extends RecyclerView.Adapter<ConversationAdapter.ConversationViewHolder> {

    private OnActionCallbackFragment callbackFragment;
    private Context mContext;
    private List<Conversation> conversationList;

    public ConversationAdapter(List<Conversation> conversationList, Context context) {
        this.conversationList = conversationList;
        this.mContext = context;
    }

    @NonNull
    @Override
    public ConversationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemContainerConversationBinding itemContainerConversationBinding = ItemContainerConversationBinding.inflate(
                LayoutInflater.from(mContext),
                parent,
                false
        );
        return new ConversationViewHolder(itemContainerConversationBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull ConversationViewHolder holder, int position) {
        Conversation conversation = conversationList.get(position);
        User user = conversation.getOtherUser();
        ChatMessage lastMessage = conversation.getLastMessage();

        String time = lastMessage.getTimeStamp();
        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm a");
        Date date = new Date(time);
        String updateTime = dateFormat.format(date);

        holder.binding.imageProfile.setImageBitmap(getUserImage(user.getImage()));
        holder.binding.textName.setText(user.getName());
        holder.binding.textLastMessage.setText(lastMessage.getMessage());
        holder.binding.textLastMsgTime.setText(updateTime);

        holder.binding.itemConversation.setOnClickListener(v->{
            callbackFragment.onActionCallback(KEY_CONVERSATION_TO_CHAT,conversation);
        });
    }

    private Bitmap getUserImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public int getItemCount() {
        return conversationList.size();
    }

    public void setCallback(OnActionCallbackFragment callbackFragment) {
        this.callbackFragment = callbackFragment;
    }

    public static class ConversationViewHolder extends RecyclerView.ViewHolder {
        ItemContainerConversationBinding binding;

        public ConversationViewHolder(@NonNull ItemContainerConversationBinding itemContainerConversationBinding) {
            super(itemContainerConversationBinding.getRoot());
            binding = itemContainerConversationBinding;
        }
    }
}
