package com.maroufb.beastchat.fragments;

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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.services.LiveFriendServices;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.FindFriendsViews.FindFriendsAdapter;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import io.socket.client.IO;
import io.socket.client.Socket;

public class FindFriendsFragment extends BaseFragment implements FindFriendsAdapter.UserListener{


    @BindView(R.id.fragment_findFriends_searchBar)
    EditText mSearchBar;

    @BindView(R.id.fragment_find_friends_recyclerView)
    RecyclerView mRecyclerView;

    @BindView(R.id.fragment_find_friends_noResults)
    TextView mNoResults;

    private Unbinder mUnbinder;

    private DatabaseReference mGetAllUsersReference;
    private ValueEventListener mGetAllUsersListener;

    private DatabaseReference mGetAllFriendsRequestsSentReference;
    private ValueEventListener mGetAllFriendsRequestsSentListener;

    private DatabaseReference mGettAllFriendsRequestsReceivedReference;
    private ValueEventListener mGetAllFriendsRequestsReceivedListener;


    private String mUserEmailString;
    private FindFriendsAdapter mFindFriendsAdapter;

    private List<User> mAllUsers;

    private LiveFriendServices mLiveFriendServices;

    public HashMap<String , User> mFriendRequestsSentMap;

    private Socket mSocket;

    private PublishSubject<String> mSearchBarString;

    public static FindFriendsFragment newInstance(){
        return new FindFriendsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(RegisterFragment.class.getSimpleName(),e.getMessage() );
            Toast.makeText(getActivity(),"Can't connect",Toast.LENGTH_SHORT);
        }

        mSocket.connect();

        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
        mLiveFriendServices = LiveFriendServices.getInstance();
        mFriendRequestsSentMap = new HashMap<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_friends,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mAllUsers = new ArrayList<>();
        mFindFriendsAdapter = new FindFriendsAdapter((BaseFragmentActivity) getActivity(),this);
        mGetAllUsersListener = getAllUsers(mFindFriendsAdapter,mUserEmailString);

        mGetAllUsersReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_BASE_PATH_USERS);
        mGetAllUsersReference.addValueEventListener(mGetAllUsersListener);

        mGetAllFriendsRequestsSentReference = FirebaseDatabase.getInstance().getReference()
                .child(Constants.FIREBASE_PATH_FRIEND_REQUEST_SENT)
                .child(Constants.encodeEmail(mUserEmailString));

        mGettAllFriendsRequestsReceivedReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PATH_FRIEND_REQUEST_RECEIVED)
        .child(Constants.encodeEmail(mUserEmailString));

        mGetAllFriendsRequestsSentListener = mLiveFriendServices.getFriendRequestsSent(mFindFriendsAdapter,this);
        mGetAllFriendsRequestsSentReference.addValueEventListener(mGetAllFriendsRequestsSentListener);

        mGetAllFriendsRequestsReceivedListener = mLiveFriendServices.getFriendRequestsReceived(mFindFriendsAdapter);
        mGettAllFriendsRequestsReceivedReference.addValueEventListener(mGetAllFriendsRequestsReceivedListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFindFriendsAdapter);

        mCompositeDisposable.add(createSearchBarDisposable());
        listenToSearchBar();

        return rootView;
    }
    
    private Disposable createSearchBarDisposable(){
        mSearchBarString = PublishSubject.create();
        return mSearchBarString
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribeOn(Schedulers.io())
                .map(new Function<String, List<User>>() {
                    @Override
                    public List<User> apply(String searchString) throws Exception {
                        return mLiveFriendServices.getMatchingUsers(mAllUsers,searchString);

                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<User>>(){
                    @Override
                    public void onNext(List<User> users) {
                        if(users.isEmpty()){
                            mNoResults.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }else {
                            mNoResults.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                        }
                        mFindFriendsAdapter.setmUsers(users);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void listenToSearchBar(){
        mSearchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mSearchBarString.onNext(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void setmFriendRequestsSentMap(HashMap<String, User> friendRequestsSentMap) {
        mFriendRequestsSentMap.clear();
        mFriendRequestsSentMap.putAll(friendRequestsSentMap);
    }

    public ValueEventListener getAllUsers(final FindFriendsAdapter adapter, String currentUsersEmail){
        return  new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mAllUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    if(!user.getEmail().equals(mUserEmailString)){
                        mAllUsers.add(user);
                    }
                }
                adapter.setmUsers(mAllUsers);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getActivity(),"Can't Load Users", Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();

        if(mGetAllUsersListener != null){
            mGetAllUsersReference.removeEventListener(mGetAllUsersListener);
        }

        if(mGetAllFriendsRequestsSentListener != null){
            mGetAllFriendsRequestsSentReference.removeEventListener(mGetAllFriendsRequestsSentListener);
        }

        if(mGetAllFriendsRequestsReceivedListener != null){
            mGettAllFriendsRequestsReceivedReference.removeEventListener(mGetAllFriendsRequestsReceivedListener);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    public void onUserClicked(User user) {
        if(Constants.isIncludedInMap(mFriendRequestsSentMap,user)){
            mGetAllFriendsRequestsSentReference.child(Constants.encodeEmail(user.getEmail())).removeValue();
            mCompositeDisposable.add(mLiveFriendServices.addOrRemoveFriendRequest(mSocket,mUserEmailString,user.getEmail(),"1"));
        }else {
            mGetAllFriendsRequestsSentReference.child(Constants.encodeEmail(user.getEmail())).setValue(user);
            mCompositeDisposable.add(mLiveFriendServices.addOrRemoveFriendRequest(mSocket,mUserEmailString,user.getEmail(),"0"));
        }
    }
}
