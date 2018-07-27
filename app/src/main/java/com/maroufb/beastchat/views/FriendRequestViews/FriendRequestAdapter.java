package com.maroufb.beastchat.views.FriendRequestViews;

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

public class FriendRequestAdapter extends RecyclerView.Adapter {

    private BaseFragmentActivity mActivity;
    private LayoutInflater mInflater;
    private List<User> mUsers;
    private OnOptionListener mListener;

    public FriendRequestAdapter(BaseFragmentActivity activity, OnOptionListener listener) {
        mActivity = activity;
        mListener = listener;
        mInflater = mActivity.getLayoutInflater();
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
        View view = mInflater.inflate(R.layout.list_friend_requests,parent,false);
        final FriendRequestViewHolder friendRequestViewHolder = new FriendRequestViewHolder(view);
        friendRequestViewHolder.approveImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) friendRequestViewHolder.itemView.getTag();
                mListener.onOptionClicked(user,"0");
            }
        });

        friendRequestViewHolder.rejectImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User user = (User) friendRequestViewHolder.itemView.getTag();
                mListener.onOptionClicked(user,"1");

            }
        });
        return friendRequestViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FriendRequestViewHolder) holder).populate(mActivity,mUsers.get(position));
    }

    @Override
    public int getItemCount() {
        return mUsers.size();
    }

    public interface OnOptionListener{

        void onOptionClicked(User user, String result);
    }
}
