package com.maroufb.beastchat.views.userFriendViews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class UserFriendAdapter extends RecyclerView.Adapter {

    private BaseFragmentActivity mActivity;
    private List<User> mUsers;
    private LayoutInflater mInflator;
    private UserClickedListener mListener;

    public UserFriendAdapter(BaseFragmentActivity activity, UserClickedListener listener) {
        mActivity = activity;
        mListener = listener;
        mInflator = mActivity.getLayoutInflater();
        mUsers = new ArrayList<>();
    }

    public void setUsers(List<User> users) {
        mUsers.clear();
        mUsers.addAll(users);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflator.inflate(R.layout.list_users_friends,parent,false);
        final UserFriendViewHolder userFriendViewHolder = new UserFriendViewHolder(view);
        userFriendViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) userFriendViewHolder.itemView.getTag();
                mListener.OnUserClicked(user);
            }
        });

        return userFriendViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((UserFriendViewHolder) holder).populate(mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface  UserClickedListener{
        void OnUserClicked(User user);
    }
}
