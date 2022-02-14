package com.sonnguyen.chatapp_05012022.view;

import static android.app.Activity.RESULT_OK;
import static com.sonnguyen.chatapp_05012022.utilities.Constants.KEY_IMAGE;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.lifecycle.Observer;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sonnguyen.chatapp_05012022.base.BaseFragment;
import com.sonnguyen.chatapp_05012022.databinding.FragmentUserInfoBinding;
import com.sonnguyen.chatapp_05012022.model.User;
import com.sonnguyen.chatapp_05012022.utilities.MessageEvent;
import com.sonnguyen.chatapp_05012022.viewmodel.UserInfoViewModel;

import org.greenrobot.eventbus.EventBus;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class UserInfoFragment extends BaseFragment<UserInfoViewModel,FragmentUserInfoBinding> {
    private FragmentUserInfoBinding binding;
    private String image;
    private DatabaseReference databaseRef;

    public static UserInfoFragment newInstance(){ return new UserInfoFragment();}

    @Override
    protected FragmentUserInfoBinding getBinding() {
        binding = FragmentUserInfoBinding.inflate(getLayoutInflater());
        return binding;
    }

    @Override
    protected void initData() {
        databaseRef = FirebaseDatabase.getInstance().getReference();

        byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE), Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

        mViewModel.getUserImage().observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                byte[] bytes = Base64.decode(preferenceManager.getString(KEY_IMAGE), Base64.DEFAULT);
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                binding.imageProfile.setImageBitmap(bitmap);
            }
        });
    }

    @Override
    protected void initEvents() {
        binding.imageBack.setOnClickListener(v->getFragmentManager().popBackStack());
        binding.buttonChangeImageProfile.setOnClickListener(v->changeImage());
        binding.imageConfirm.setOnClickListener(v->{
            if (image!=null){
                mViewModel.updateData(image);
                preferenceManager.putString(KEY_IMAGE,image);
            }else{
                Toast.makeText(getContext(), "Pick image!", Toast.LENGTH_SHORT).show();
            }
        });
        binding.textDeleteAccount.setOnClickListener(v->deleteAccount());
    }

    private void deleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Delete Account!");
        builder.setMessage("Do you really want to delete this account");
        builder.setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        builder.setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                mViewModel.deleteAccount();
                MessageEvent messageEvent = new MessageEvent("delete account");
                EventBus.getDefault().post(messageEvent);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void changeImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickImage.launch(intent);
    }

    private final ActivityResultLauncher<Intent> pickImage = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK) {
                    if (result.getData() != null) {
                        Uri imageUri = result.getData().getData();
                        try {
                            InputStream inputStream = requireActivity().getContentResolver().openInputStream(imageUri);
                            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                            binding.imageProfile.setImageBitmap(bitmap);
                            image = bitmapToString(bitmap);
                        } catch (FileNotFoundException e) {
                            Toast.makeText(getContext(), "Can't load image", Toast.LENGTH_SHORT).show();;
                        }
                    }
                }
            }
    );

    private String bitmapToString(Bitmap bitmap) {
        int previewWidth = 150;
        int previewHeight = bitmap.getHeight() * previewWidth / bitmap.getWidth();
        Bitmap previewBitmap = Bitmap.createScaledBitmap(bitmap, previewWidth, previewHeight, false);
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        previewBitmap.compress(Bitmap.CompressFormat.JPEG, 50, byteArrayOutputStream);
        byte[] bytes = byteArrayOutputStream.toByteArray();
        return Base64.encodeToString(bytes, Base64.DEFAULT);
    }

    @Override
    protected Class getClassViewModel() {
        return UserInfoViewModel.class;
    }
}