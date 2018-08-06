package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.Message;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.IO;
import io.socket.client.Socket;

public class MessageFragment extends BaseFragment {

    public static final  String FRIEND_DETAILS_EXTRA = "FRIEND_DETAILS_EXTRA";

    @BindView(R.id.fragment_messages_friendPicture)
    ImageView mFriendPicture;

    @BindView(R.id.fragment_messages_friendName)
    TextView mfriendName;

    @BindView(R.id.fragment_messages_message_box)
    EditText mMessageBox;

    @BindView(R.id.fragment_messages_sendButton)
    ImageView mSendMessage;

    private Unbinder mUnbinder;

    private Socket mSocket;
    private LiveFriendServices mLiveFriendServices;

    private String mFriendEmailString;
    private String mFriendPictureString;
    private String mFriendNameString;
    private String mUserEmailString;

    private DatabaseReference mGetAllMessagesReference;
    private ValueEventListener mGetAllMessagesListener;

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

        mLiveFriendServices = LiveFriendServices.getInstance();
        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(LoginFragment.class.getSimpleName(),e.getMessage() );
            Toast.makeText(getActivity(),"Can't connect",Toast.LENGTH_SHORT);
        }

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

        mGetAllMessagesReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PATH_USER_MESSAGES)
                .child(Constants.encodeEmail(mUserEmailString)).child(Constants.encodeEmail(mFriendEmailString));
        return rootView;
    }

    @OnClick(R.id.fragment_messages_sendButton)
    public void  setmSendMessage()
    {
        if(mMessageBox.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(),"Message Can't Be Blank", Toast.LENGTH_SHORT).show();
        }else {
            DatabaseReference newMessageReference = mGetAllMessagesReference.push();
            Message message = new Message(newMessageReference.getKey(), mMessageBox.getText().toString()
                    ,mUserEmailString,mSharedPreferences.getString(Constants.USER_PICTURE,""));
            newMessageReference.setValue(message);
            mCompositeDisposable.add(mLiveFriendServices.sendMessage(mSocket,mUserEmailString,
                    mSharedPreferences.getString(Constants.USER_PICTURE,""),mMessageBox.getText().toString()));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
