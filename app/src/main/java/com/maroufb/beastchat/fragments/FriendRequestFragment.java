package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.FriendRequestViews.FriendRequestAdapter;

import io.socket.client.Socket;
import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.socket.client.IO;

public class FriendRequestFragment extends BaseFragment implements FriendRequestAdapter.OnOptionListener{


    public static FriendRequestFragment newInstance(){ return  new FriendRequestFragment();}

    @BindView(R.id.fragment_friend_request_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_friend_request_message)
    TextView mTextView;

    private LiveFriendServices mLiveFriendServices;

    private DatabaseReference mGetAllUsersFriendRequestsReference;
    private ValueEventListener mGetAllUsersFriendRequestsListener;

    private Unbinder mUnbinder;

    private String mUserEmailString;

    private Socket mSocket;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLiveFriendServices = LiveFriendServices.getInstance();
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");

        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(LoginFragment.class.getSimpleName(),e.getMessage() );
            Toast.makeText(getActivity(),"Can't connect",Toast.LENGTH_SHORT);
        }

        mSocket.connect();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_friend_request,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);

        FriendRequestAdapter adapter = new FriendRequestAdapter((BaseFragmentActivity) getActivity(),this);

        mRecyclerView.setLayoutManager( new LinearLayoutManager(getActivity()));
        mGetAllUsersFriendRequestsReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_FRIEND_REQUEST_RECEIVED).child(Constants.encodeEmail(mUserEmailString));

        mGetAllUsersFriendRequestsListener = mLiveFriendServices.getAllFriendRequests(adapter,mRecyclerView,mTextView);
        mGetAllUsersFriendRequestsReference.addValueEventListener(mGetAllUsersFriendRequestsListener);
        mRecyclerView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        if(mGetAllUsersFriendRequestsListener != null){
            mGetAllUsersFriendRequestsReference.removeEventListener(mGetAllUsersFriendRequestsListener);
        }
    }

    @Override
    public void onOptionClicked(User user, String result) {
        if(result.equals("0")){
            DatabaseReference userFriendReference = FirebaseDatabase.getInstance().getReference()
                    .child(Constants.FIREBASE_PATH_USER_FRIENDS).child(Constants.encodeEmail(mUserEmailString))
                    .child(Constants.encodeEmail(user.getEmail()));
            userFriendReference.setValue(user);
            mGetAllUsersFriendRequestsReference.child(Constants.encodeEmail(user.getEmail()))
                    .removeValue();
            mCompositeDisposable.add(mLiveFriendServices.approveDeclineFriendRequest(mSocket,mUserEmailString,user.getEmail(),"0"));

        }else {
            mGetAllUsersFriendRequestsReference.child(Constants.encodeEmail(user.getEmail()))
                    .removeValue();
            mCompositeDisposable.add(mLiveFriendServices.approveDeclineFriendRequest(mSocket,mUserEmailString,user.getEmail(),"1"));
        }
    }
}
