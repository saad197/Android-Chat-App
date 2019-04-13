package com.bowfletchers.chatberry.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;
import com.bowfletchers.chatberry.R;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

public class User_profile extends AppCompatActivity {

    private final int REQUEST_CODE_IMAGE = 1;
    private final String STORE_URL = "gs://chatberry-201de.appspot.com";
    private final String DEFAULT_PHOTO_URL = "https://cdn.pixabay.com/photo/2016/08/08/09/17/avatar-1577909__340.png";

    ImageView imageViewUserPhoto;
    TextView editTextUserName;
    Switch aSwitchOnlineStatus;
    Button buttonDone;
    Button buttonSignout;

    FirebaseAuth mAuth;
    FirebaseUser currentUser;
    FirebaseStorage firebaseStore;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        referenceViews();

        // init Fire auth instance
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        firebaseStore = FirebaseStorage.getInstance();
        storageReference = firebaseStore.getReferenceFromUrl(STORE_URL);

        displayDefaultUserInfo();

        // open camera when user click on image view
        imageViewUserPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, REQUEST_CODE_IMAGE);
            }
        });

        // handle update user info when click Done button
        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // save user photo to firebase store
                uploadUserPhotoToStore();
            }
        });

        // sign out user when click sign out button
        buttonSignout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent backSignInIntent = new Intent(User_profile.this, Login_account.class);
                startActivity(backSignInIntent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.homePage:
                Intent chatListIntent = new Intent(User_profile.this, ChatHistoryList.class);
                startActivity(chatListIntent);
                return true;
            case R.id.my_friends:
                Intent userFriendsIntent = new Intent(User_profile.this, Friend_List.class);
                startActivity(userFriendsIntent);
                return true;
            default:
                // Do nothing
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // get the captured image to image view
        if (requestCode == REQUEST_CODE_IMAGE && resultCode == RESULT_OK && data != null) {
            Bitmap bitmapImg = (Bitmap) data.getExtras().get("data");
            imageViewUserPhoto.setImageBitmap(bitmapImg);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void referenceViews() {
        imageViewUserPhoto = findViewById(R.id.user_profile_avatar);
        editTextUserName = findViewById(R.id.user_profile_name);
        aSwitchOnlineStatus = findViewById(R.id.user_profile_onlineStatus_switch);
        buttonDone = findViewById(R.id.user_profile_button_done);
        buttonSignout = findViewById(R.id.user_profile_button_logout);
    }

    private void displayDefaultUserInfo(){
        // display user photo
        Uri userPhotoURI = currentUser.getPhotoUrl();
        if (userPhotoURI != null) {
            String userPhotoURL = userPhotoURI.toString();
            Glide.with(this).load(userPhotoURL).into(imageViewUserPhoto);
        } else {
            Glide.with(this).load(DEFAULT_PHOTO_URL).into(imageViewUserPhoto);
        }

        // display user name
        String userName = currentUser.getDisplayName();
        if (userName != null && !userName.equals("")) {
            editTextUserName.setText(userName);
        } else {
            editTextUserName.setText("NO NAME");
        }
    }

    private void uploadUserPhotoToStore() {
        // set components to create img name
        String userName = currentUser.getDisplayName();
        String userId = currentUser.getUid();
        String imageName = userName + userId + ".png";

        // upload current img to store
        final StorageReference userImageRef = storageReference.child("images/" + imageName);
        imageViewUserPhoto.setDrawingCacheEnabled(true);
        imageViewUserPhoto.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) imageViewUserPhoto.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = userImageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                Toast.makeText(User_profile.this, "Upload image failed", Toast.LENGTH_SHORT).show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // get user img download url from store
                userImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String downloadUrl = uri.toString();
                        // after upload img succeed , update the user profile
                        // with user photo url and user name
                        String userName = editTextUserName.getText().toString();
                        updateUserProfileInfo(downloadUrl, userName);
                    }
                });
            }
        });
    }

    private void updateUserProfileInfo(String newUserPhoToURL, String newUserDisplayName) {
        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                .setDisplayName(newUserDisplayName)
                .setPhotoUri(Uri.parse(newUserPhoToURL))
                .build();
        currentUser.updateProfile(userProfileChangeRequest)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(User_profile.this, "User info has been updated successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(User_profile.this, "Update user information failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}