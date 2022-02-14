package com.sonnguyen.chatapp_05012022.adapters;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.pgreze.reactions.ReactionPopup;
import com.github.pgreze.reactions.ReactionsConfig;
import com.github.pgreze.reactions.ReactionsConfigBuilder;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.sonnguyen.chatapp_05012022.R;
import com.sonnguyen.chatapp_05012022.databinding.ItemContainerReceivedMessageBinding;
import com.sonnguyen.chatapp_05012022.databinding.ItemContainerSentMessageBinding;
import com.sonnguyen.chatapp_05012022.model.ChatMessage;
import com.sonnguyen.chatapp_05012022.utilities.Constants;

import java.util.List;
import java.util.Objects;

public class ChatAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int VIEW_TYPE_SENT = 1;
    public static final int VIEW_TYPE_RECEIVED = 2;
    private final List<ChatMessage> chatMessages;
    private String senderId, receiverID, chatRoom;
    private final Bitmap receiverProfileImage;
    private Context mContext;

    public ChatAdapter(List<ChatMessage> chatMessages, Bitmap receiverProfileImage, Context mContext) {
        this.chatMessages = chatMessages;
        this.receiverProfileImage = receiverProfileImage;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_SENT) {
            return new SentMessageViewHolder(
                    ItemContainerSentMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext())
                            , parent
                            , false
                    )
            );
        } else {
            return new ReceivedMessageViewHolder(
                    ItemContainerReceivedMessageBinding.inflate(
                            LayoutInflater.from(parent.getContext())
                            , parent
                            , false
                    )
            );
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ChatMessage message = chatMessages.get(position);
        senderId = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        receiverID = chatMessages.get(position).getReceiverID();

        int[] reactions = new int[]{
                R.drawable.ic_fb_like,
                R.drawable.ic_fb_love,
                R.drawable.ic_fb_laugh,
                R.drawable.ic_fb_wow,
                R.drawable.ic_fb_sad,
                R.drawable.ic_fb_angry
        };

        ReactionsConfig config = new ReactionsConfigBuilder(mContext)
                .withReactions(reactions)
                .build();

        ReactionPopup popup = new ReactionPopup(mContext, config, (pos) -> {
            if (pos < 0) {
                return false;
            }

            if (holder.getClass() == SentMessageViewHolder.class) {
                SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
                viewHolder.binding.reaction.setImageResource(reactions[pos]);
            } else {
                ReceivedMessageViewHolder viewHolder = (ReceivedMessageViewHolder) holder;
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
                viewHolder.binding.reaction.setImageResource(reactions[pos]);
            }

            message.setFeeling(pos);

            FirebaseDatabase.getInstance().getReference()
                    .child(Constants.KEY_CHAT)
                    .child(setChatRoom(senderId, receiverID))
                    .child(message.getMessageID()).setValue(message);

            return true;
        });

        if (getItemViewType(position) == VIEW_TYPE_SENT) {
            SentMessageViewHolder viewHolder = (SentMessageViewHolder) holder;
            if (message.getMessage().equals(KEY_IMAGE)) {
                viewHolder.binding.textMessage.setVisibility(View.GONE);
                viewHolder.binding.imageMessage.setVisibility(View.VISIBLE);
                byte[] bytes = Base64.decode(message.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                viewHolder.binding.imageMessage.setImageBitmap(bitmap);
                viewHolder.binding.imageMessage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view, motionEvent);
                        return false;
                    }
                });
            } else {
                viewHolder.setData(chatMessages.get(position));
                viewHolder.binding.textMessage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {

                        return false;
                    }
                });
            }

            if (message.getFeeling() >= 0) {
                viewHolder.binding.reaction.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.reaction.setVisibility(View.GONE);
            }
        } else {
            ReceivedMessageViewHolder viewHolder = (ReceivedMessageViewHolder) holder;
            if (message.getMessage().equals(KEY_IMAGE)) {
                viewHolder.binding.textMessage.setVisibility(View.GONE);
                viewHolder.binding.imageMessage.setVisibility(View.VISIBLE);
                byte[] bytes = Base64.decode(message.getImage(), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                viewHolder.binding.imageProfile.setImageBitmap(receiverProfileImage);
                viewHolder.binding.imageMessage.setImageBitmap(bitmap);
                viewHolder.binding.imageMessage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view, motionEvent);
                        return false;
                    }
                });
            } else {
                viewHolder.setData(chatMessages.get(position), receiverProfileImage);
                viewHolder.binding.textMessage.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        popup.onTouch(view, motionEvent);
                        return false;
                    }
                });
            }

            if (message.getFeeling() >= 0) {
                viewHolder.binding.reaction.setImageResource(reactions[message.getFeeling()]);
                viewHolder.binding.reaction.setVisibility(View.VISIBLE);
            } else {
                viewHolder.binding.reaction.setVisibility(View.GONE);
            }
        }
    }

    private String setChatRoom(String senderID, String receiverID) {
        if (senderID.compareTo(receiverID) > 0) {
            return chatRoom = senderID + receiverID;
        } else {
            return chatRoom = receiverID + senderID;
        }
    }

    @Override
    public int getItemCount() {
        return chatMessages.size();
    }

    @Override
    public int getItemViewType(int position) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (chatMessages.get(position).getSenderID().equals(currentUser.getUid())) {
            return VIEW_TYPE_SENT;
        } else {
            return VIEW_TYPE_RECEIVED;
        }
    }

    static class SentMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerSentMessageBinding binding;

        SentMessageViewHolder(ItemContainerSentMessageBinding itemContainerSentMessageBinding) {
            super(itemContainerSentMessageBinding.getRoot());
            binding = itemContainerSentMessageBinding;
        }

        void setData(ChatMessage chatMessage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getTimeStamp());
        }
    }

    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {

        private final ItemContainerReceivedMessageBinding binding;

        ReceivedMessageViewHolder(ItemContainerReceivedMessageBinding itemContainerReceivedMessageBinding) {
            super(itemContainerReceivedMessageBinding.getRoot());
            binding = itemContainerReceivedMessageBinding;
        }

        void setData(ChatMessage chatMessage, Bitmap receiverProfileImage) {
            binding.textMessage.setText(chatMessage.getMessage());
            binding.textDateTime.setText(chatMessage.getTimeStamp());
            binding.imageProfile.setImageBitmap(receiverProfileImage);
        }

    }
}
