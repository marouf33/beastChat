package com.maroufb.beastchat.views.FindFriendsViews;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.squareup.picasso.Picasso;

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

    public void populate(Context context, User user){
        itemView.setTag(user);
        mUserName.setText(user.getUserName());
        Picasso.get()
                .load(user.getUserPicture())
                .into(mUserPicture);
    }
}
