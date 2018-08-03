package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastchat.R;
import com.maroufb.beastchat.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class MessageFragment extends BaseFragment {

    public static final  String FRIEND_DETAILS_EXTRA = "FRIEND_DETAILS_EXTRA";

    @BindView(R.id.fragment_messages_friendPicture)
    ImageView mFriendPicture;

    @BindView(R.id.fragment_messages_friendName)
    TextView mfriendName;

    @BindView(R.id.fragment_messages_message_box)
    EditText mMessageBox;

    private Unbinder mUnbinder;

    private String mFriendEmailString;
    private String mFriendPictureString;
    private String mFriendNameString;

    private String mUserEmailString;

    public static MessageFragment newInstance(ArrayList<String> friendDetails){
        Bundle arguments = new Bundle();
        arguments.putStringArrayList(FRIEND_DETAILS_EXTRA,friendDetails);

        MessageFragment messageFragment = new MessageFragment();
        messageFragment.setArguments(arguments);
        return messageFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ArrayList<String> friendDetails = getArguments().getStringArrayList(FRIEND_DETAILS_EXTRA);
        mFriendEmailString = friendDetails.get(0);
        mFriendPictureString = friendDetails.get(1);
        mFriendNameString = friendDetails.get(2);

        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);

        Picasso.get().load(mFriendPictureString).into(mFriendPicture);
        mfriendName.setText(mFriendNameString);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}
