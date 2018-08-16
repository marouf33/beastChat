package com.maroufb.beastchat.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.roughike.bottombar.BottomBar;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class ProfileFragment extends BaseFragment {

    @BindView(R.id.profile_bottombar)  BottomBar mBottomBar;

    @BindView(R.id.fragment_profile_cameraPicture)
    ImageView mCameraImage;

    @BindView(R.id.fragment_profile_galleryPicture)
    ImageView mGalleryImage;

    @BindView(R.id.fragment_profile_user_picture)
    ImageView mUserPicture;

    @BindView(R.id.fragment_profile_userEmail)
    TextView mUserEmail;

    @BindView(R.id.fragment_profile_userName)
    TextView mUserName;

    private Unbinder mUnbinder;

    private LiveFriendServices mLiveFriendServices;

    private DatabaseReference mAllFriendRequestReference;
    private ValueEventListener mAllFriendRequesListener;

    private DatabaseReference mUsersNewMessagesReference;
    private ValueEventListener mUsersNewMessagesListener;


    private String mUserEmailString;

    private final int REQUEST_CODE_CAMERA = 100;
    private final int REQUEST_CODE_PICTURE = 101;

    private Uri mTempUri;

    public static ProfileFragment newInstance(){return new ProfileFragment();}

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mBottomBar.selectTabWithId(R.id.tab_profile);
        setupBottomBar(mBottomBar, 3);

        Picasso.get()
                .load(mSharedPreferences.getString(Constants.USER_PICTURE,""))
                .into(mUserPicture);
        mUserEmail.setText(mUserEmailString);
        mUserName.setText(mSharedPreferences.getString(Constants.USER_NAME,""));

        mAllFriendRequestReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_FRIEND_REQUEST_RECEIVED).child(Constants.encodeEmail(mUserEmailString));
        mAllFriendRequesListener = mLiveFriendServices.getFriendRequestBottom(mBottomBar,R.id.tab_friends);
        mAllFriendRequestReference.addValueEventListener(mAllFriendRequesListener);

        mUsersNewMessagesReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_USER_NEW_MESSAGES).child(Constants.encodeEmail(mUserEmailString));
        mUsersNewMessagesListener = mLiveFriendServices.getAllNewMessages(mBottomBar,R.id.tab_messages);
        mUsersNewMessagesReference.addValueEventListener(mUsersNewMessagesListener);
        return rootView;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveFriendServices = LiveFriendServices.getInstance();
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }

    @OnClick(R.id.fragment_profile_galleryPicture)
    public void setmGalleryImage(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/jpeg");
        intent.putExtra(intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent,"Choose Image With"),REQUEST_CODE_PICTURE);
    }

    @OnClick(R.id.fragment_profile_cameraPicture)
    public void setCameraImage(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        mTempUri  = FileProvider.getUriForFile(
                getActivity(),
                "com.maroufb.beastchat.provider", //(use your app signature + ".provider" )
                getOutputFile());
        intent.putExtra(MediaStore.EXTRA_OUTPUT,mTempUri);
        startActivityForResult(intent,REQUEST_CODE_CAMERA);

    }

    private static File getOutputFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES),"BeastChat");
        if(!mediaStorageDir.exists()){
            if(!mediaStorageDir.mkdir()){
                return null;
            }
        }
        String timeStamp = new SimpleDateFormat("yyyydd_HHmmss").format(new Date());
        return new File(mediaStorageDir.getPath() + File.separator +
        "IMG_" + timeStamp +".jpg");
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK) {
            switch(resultCode){
                case REQUEST_CODE_PICTURE:
                    Uri selectedImageUri = data.getData();
                    Log.i(ProfileFragment.class.getSimpleName(), selectedImageUri.toString());
                    break;
                case REQUEST_CODE_CAMERA:
                    selectedImageUri = mTempUri;
                    Log.i(ProfileFragment.class.getSimpleName(),selectedImageUri.toString());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if(mAllFriendRequesListener != null){
            mAllFriendRequestReference.removeEventListener(mAllFriendRequesListener);
        }

        if(mUsersNewMessagesListener != null){
            mUsersNewMessagesReference.removeEventListener(mUsersNewMessagesListener);
        }
    }
}
