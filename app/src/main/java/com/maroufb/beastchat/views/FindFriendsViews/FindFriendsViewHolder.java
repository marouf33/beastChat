package com.maroufb.beastchat.views.FindFriendsViews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.utils.Constants;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FindFriendsViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_user_userPicture)
    ImageView mUserPicture;

    @BindView(R.id.list_user_addFriend)
    public ImageView mAddFriend;

    @BindView(R.id.list_user_userName)
    TextView mUserName;

    @BindView(R.id.list_user_userStatus)
    TextView mUserStatus;

    public FindFriendsViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(Context context, User user, HashMap<String, User> friendRequestSentMap
            , HashMap<String, User> friendRequestReceivedMap, HashMap<String,User> currentUsersFriendMap){
        itemView.setTag(user);
        mUserName.setText(user.getUserName());
        Picasso.get()
                .load(user.getUserPicture())
                .into(mUserPicture);

        if(Constants.isIncludedInMap(friendRequestSentMap,user)){
            mUserStatus.setVisibility(View.VISIBLE);
            mUserStatus.setText("Friend Request Sent");
            mAddFriend.setImageResource(R.mipmap.ic_cancel_request);
            mAddFriend.setVisibility(View.VISIBLE);
        }else if(Constants.isIncludedInMap(friendRequestReceivedMap,user)){
            mAddFriend.setVisibility(View.GONE);
            mUserStatus.setText("This User Has Requested You");
            mUserStatus.setVisibility(View.VISIBLE);
        }else if(Constants.isIncludedInMap(currentUsersFriendMap,user)){
            mUserStatus.setVisibility(View.VISIBLE);
            mUserStatus.setText("Already a friend");
            mAddFriend.setVisibility(View.GONE);
        }else {
            mUserStatus.setVisibility(View.GONE);
            mAddFriend.setImageResource(R.mipmap.ic_add_friends);
            mAddFriend.setVisibility(View.VISIBLE);
        }
    }
}
