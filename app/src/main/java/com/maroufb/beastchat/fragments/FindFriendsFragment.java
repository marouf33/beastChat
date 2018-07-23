package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.FindFriendsViews.FindFriendsAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class FindFriendsFragment extends BaseFragment implements FindFriendsAdapter.UserListener{


    @BindView(R.id.fragment_findFriends_searchBar)
    EditText mSearchBar;

    @BindView(R.id.fragment_find_friends_recyclerView)
    RecyclerView mRecyclerView;

    private Unbinder mUnbinder;

    private DatabaseReference mGetAllusersReference;
    private ValueEventListener mGetAllUsersListener;
    private String mUserEmailString;
    private FindFriendsAdapter mFindFriendsAdapter;

    private List<User> mAllUsers;

    public static FindFriendsFragment newInstance(){
        return new FindFriendsFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mUserEmailString = mSharedPreferences.getString(Constants.USER_EMAIL,"");
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_find_friends,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mAllUsers = new ArrayList<>();
        mFindFriendsAdapter = new FindFriendsAdapter((BaseFragmentActivity) getActivity(),this);
        mGetAllUsersListener = getAllUsers(mFindFriendsAdapter,mUserEmailString);

        mGetAllusersReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIRE_BASE_PATH_USERS);
        mGetAllusersReference.addValueEventListener(mGetAllUsersListener);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mFindFriendsAdapter);

        return rootView;
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
            mGetAllusersReference.removeEventListener(mGetAllUsersListener);
        }
    }

    @Override
    public void onUserClicked(User user) {

    }
}
