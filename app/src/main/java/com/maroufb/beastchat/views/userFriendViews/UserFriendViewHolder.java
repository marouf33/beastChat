package com.maroufb.beastchat.views.userFriendViews;

import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserFriendViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_friends_userName)
    TextView userName;

    @BindView(R.id.list_friends_userPicture)
    ImageView userPicture;

    @BindView(R.id.list_friends_gotoConversation)
    ImageView gotoConversation;

    public UserFriendViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(User user){
        itemView.setTag(user);
        Picasso.get()
                .load(user.getUserPicture())
                .into(userPicture);
        userName.setText(user.getUserName());

    }
}
