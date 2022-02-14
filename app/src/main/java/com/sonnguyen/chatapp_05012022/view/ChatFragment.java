package com.sonnguyen.chatapp_05012022.view;

import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEYBUNDLE_USER_DATA_TO_CHAT;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.google.firebase.auth.FirebaseAuth;
import com.sonnguyen.chatapp_05012022.viewmodel.ChatViewModel;
import com.sonnguyen.chatapp_05012022.adapters.ChatAdapter;
import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentChatBinding;
import com.sonnguyen.chatapp_05012022.model.ChatMessage;
import com.sonnguyen.chatapp_05012022.model.User;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class ChatFragment extends BaseFragment<ChatViewModel, FragmentChatBinding> {

    private static final int REQUEST_IMAGE = 123;
    private FragmentChatBinding binding;
    private User user;

    private ChatAdapter chatAdapter;
    private String senderID, receiverID, timeStamp;
    private Date dateObject;

    public static ChatFragment newInstance() {
        return new ChatFragment();
    }

    @Override
    protected FragmentChatBinding getBinding() {
        binding = FragmentChatBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {
        senderID = FirebaseAuth.getInstance().getUid();
        getUserValue();
        mViewModel.setChatRoom(senderID, receiverID);
        mViewModel.loadMessage();
        chatAdapter = new ChatAdapter(mViewModel.getMessages(), getBitmapFromEncodedString(user.getImage()), getContext());
        binding.chatRecyclerView.setAdapter(chatAdapter);

        mViewModel.getMessageLiveData().observe(this, new Observer<ArrayList<ChatMessage>>() {
            @Override
            public void onChanged(ArrayList<ChatMessage> messages) {
                if (mViewModel.getMessages().size() == 0) {
                    chatAdapter.notifyDataSetChanged();
                } else {
                    chatAdapter.notifyItemRangeInserted(mViewModel.getMessages().size(), mViewModel.getMessages().size());
                    binding.chatRecyclerView.smoothScrollToPosition(mViewModel.getMessages().size() - 1);
                }

            }
        });
    }

    private void getUserValue() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            user = (User) bundle.getSerializable(KEYBUNDLE_USER_DATA_TO_CHAT);
            receiverID = user.getUserID();
        }

        binding.textName.setText(user.getName());
        binding.progressBar.setVisibility(View.INVISIBLE);
        binding.chatRecyclerView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void initEvents() {
        binding.imageSendMessage.setOnClickListener(v -> {
            if (binding.inputMessage.getText().toString().isEmpty()) {

            } else {
                String message = binding.inputMessage.getText().toString();
                DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                timeStamp = df.format(Calendar.getInstance().getTime());
                dateObject = Calendar.getInstance().getTime();
                mViewModel.sendMessage(senderID, receiverID, message, timeStamp, dateObject);
                if (mViewModel.getMessages().size() > 0) {
                    binding.chatRecyclerView.smoothScrollToPosition(mViewModel.getMessages().size() - 1);
                }
                binding.inputMessage.setText(null);
            }
        });

        binding.imageSendImage.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, REQUEST_IMAGE);
        });

//        binding.chatRecyclerView.setOnClickListener(v->hideSoftKeyboard(requireActivity()));
//        binding.itemView.setOnClickListener(v->hideSoftKeyboard(requireActivity()));

        binding.imageBack.setOnClickListener(v -> getFragmentManager().popBackStack());
    }

    private Bitmap getBitmapFromEncodedString(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQUEST_IMAGE) {
            if(data != null) {
                if(data.getData() != null) {
                    Uri imageUri = data.getData();
                    try {
                        InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                        Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                        String image = bitmapToString(bitmap);
                        Log.i("aaa", "onActivityResult: "+image);
                        DateFormat df = new SimpleDateFormat("EEE, d MMM yyyy, HH:mm");
                        timeStamp = df.format(Calendar.getInstance().getTime());
                        dateObject = Calendar.getInstance().getTime();
                        mViewModel.sendImage(senderID, receiverID, image, timeStamp, dateObject);
                    } catch (FileNotFoundException e) {
                        showToast(e.getMessage());
                    }
                }
            }
        }
    }

    private void showToast(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    private String bitmapToString(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(),
                    0
            );
        }
    }

    @Override
    protected Class<ChatViewModel> getClassViewModel() {
        return ChatViewModel.class;
    }
}