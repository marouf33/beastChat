package com.maroufb.beastchat.views.FriendRequestViews;

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

public class FriendRequestViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_friend_request_userPicture)
    ImageView userPicture;

    @BindView(R.id.list_friend_request_userName)
    TextView userName;

    @BindView(R.id.list_friend_request_acceptRequest)
    ImageView approveImageView;

    @BindView(R.id.list_friend_request_rejectRequest)
    ImageView rejectImageView;

    public FriendRequestViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(Context context, User user){
        userName.setText(user.getUserName());
        Picasso.get().load(user.getUserPicture()).into(userPicture);
    }


}
