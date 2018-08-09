package com.maroufb.beastchat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.ChatRoom;
import com.maroufb.beastchat.Entities.Message;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.MessagesViews.MessagesAdapter;
import com.squareup.picasso.Picasso;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
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

    @BindView(R.id.fragment_messages_recyclerView)
    RecyclerView mMessagesRecyclerView;

    private Unbinder mUnbinder;

    private Socket mSocket;
    private LiveFriendServices mLiveFriendServices;

    private PublishSubject<String> mMessageSubject;

    private MessagesAdapter mMessagesAdapter;

    private String mFriendEmailString;
    private String mFriendPictureString;
    private String mFriendNameString;
    private String mUserEmailString;

    private DatabaseReference mGetAllMessagesReference;
    private ValueEventListener mGetAllMessagesListener;

    private DatabaseReference mUserChatRoomsReference;

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

        mSocket.connect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_messages,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);

        Picasso.get().load(mFriendPictureString).into(mFriendPicture);
        mfriendName.setText(mFriendNameString);

         mMessagesAdapter = new MessagesAdapter((BaseFragmentActivity) getActivity(),mUserEmailString);
        mMessagesRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        mGetAllMessagesReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PATH_USER_MESSAGES)
                .child(Constants.encodeEmail(mUserEmailString)).child(Constants.encodeEmail(mFriendEmailString));

        mGetAllMessagesListener = mLiveFriendServices.getAllMessages(mMessagesRecyclerView,mfriendName,mFriendPicture,mMessagesAdapter);
        mGetAllMessagesReference.addValueEventListener(mGetAllMessagesListener);
        mMessagesRecyclerView.setAdapter(mMessagesAdapter);

        mUserChatRoomsReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_USER_CHAT_ROOMS)
                .child(Constants.encodeEmail(mUserEmailString));

        mCompositeDisposable.add(createChatRoomSubscription());
        messageBoxListener();

        return rootView;
    }

    @OnClick(R.id.fragment_messages_sendButton)
    public void  setmSendMessage()
    {
        if(mMessageBox.getText().toString().trim().equals("")){
            Toast.makeText(getActivity(),"Message Can't Be Blank", Toast.LENGTH_SHORT).show();
        }else {

            ChatRoom chatRoom = new ChatRoom(mFriendPictureString,mFriendNameString,
                    mFriendEmailString,mMessageBox.getText().toString(),mUserEmailString,
                    true,true);
            mUserChatRoomsReference.child(Constants.encodeEmail(mFriendEmailString)).setValue(chatRoom);

            DatabaseReference newMessageReference = mGetAllMessagesReference.push();
            Message message = new Message(newMessageReference.getKey(), mMessageBox.getText().toString()
                    ,mUserEmailString,mSharedPreferences.getString(Constants.USER_PICTURE,""));
            newMessageReference.setValue(message);
            mCompositeDisposable.add(mLiveFriendServices.sendMessage(mSocket,mUserEmailString,
                    mSharedPreferences.getString(Constants.USER_PICTURE,""),mMessageBox.getText().toString(),
                    mFriendEmailString,mSharedPreferences.getString(Constants.USER_NAME,"")));

            mMessageBox.setText("");



            mMessagesRecyclerView.scrollToPosition(mMessagesAdapter.getMessages().size() - 1);

        }
    }

    private Disposable createChatRoomSubscription(){
        mMessageSubject = PublishSubject.create();
        return mMessageSubject.debounce(200, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>(){
                    @Override
                    public void onNext(String message) {
                        if(!message.isEmpty()){
                            ChatRoom chatRoom = new ChatRoom(mFriendPictureString,mFriendNameString,
                                    mFriendEmailString,message,mUserEmailString,true,false);
                            mUserChatRoomsReference.child(Constants.encodeEmail(mFriendEmailString)).setValue(chatRoom);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void messageBoxListener(){
        mMessageBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mMessageSubject.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if(mGetAllMessagesListener != null){
            mGetAllMessagesReference.removeEventListener(mGetAllMessagesListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }
}
