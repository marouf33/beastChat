package com.maroufb.beastchat.views.MessagesViews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastchat.Entities.Message;
import com.maroufb.beastchat.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MessagesViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_messages_friendPicture)
    ImageView mFriendPicture;

    @BindView(R.id.list_messages_userPicture)
    ImageView mUserPicture;

    @BindView(R.id.list_messages_userText)
    TextView mUserText;

    @BindView(R.id.list_messages_friendText)
    TextView mFriendText;

    public MessagesViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(Message message, String currentUserEmail){
        if(!currentUserEmail.equals(message.getMessageSenderEmail())){
            mUserPicture.setVisibility(View.GONE);
            mUserText.setVisibility(View.GONE);

            mFriendPicture.setVisibility(View.VISIBLE);
            mFriendText.setVisibility(View.VISIBLE);

            Picasso.get().load(message.getMessageSenderPicture()).into(mFriendPicture);
            mFriendText.setText(message.getMessageText());
        }else{
            mUserPicture.setVisibility(View.VISIBLE);
            mUserText.setVisibility(View.VISIBLE);

            mFriendPicture.setVisibility(View.GONE);
            mFriendText.setVisibility(View.GONE);

            Picasso.get().load(message.getMessageSenderPicture()).into(mUserPicture);
            mUserText.setText(message.getMessageText());
        }
    }
}
