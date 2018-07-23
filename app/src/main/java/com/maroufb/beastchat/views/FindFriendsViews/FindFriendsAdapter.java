package com.maroufb.beastchat.views.FindFriendsViews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindFriendsAdapter extends RecyclerView.Adapter {

    private BaseFragmentActivity mActivity;
    private List<User> mUsers;
    private LayoutInflater mInflater;
    private UserListener mListener;

    private HashMap<String, User> mFriendRequestSentMap;

    public FindFriendsAdapter(BaseFragmentActivity mActivity, UserListener mListener) {
        this.mActivity = mActivity;
        this.mListener = mListener;
        mInflater= mActivity.getLayoutInflater();
        mUsers = new ArrayList<>();
        mFriendRequestSentMap = new HashMap<>();
    }

    public void setmUsers(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();

    }

    public void setmFriendRequestSentMap(HashMap<String, User> friendRequestSentMap) {
        mFriendRequestSentMap.clear();
        mFriendRequestSentMap.putAll(friendRequestSentMap);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View userView = mInflater.inflate(R.layout.list_user,parent,false);
        final FindFriendsViewHolder findFriendsViewHolder = new FindFriendsViewHolder(userView);
        findFriendsViewHolder.mAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) findFriendsViewHolder.itemView.getTag();
                mListener.onUserClicked(user);
            }
        });
        return findFriendsViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FindFriendsViewHolder) holder).populate(mActivity,mUsers.get(position), mFriendRequestSentMap);
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface UserListener{
        void onUserClicked(User user);
    }
}
