package com.maroufb.beastchat.views.ChatRoomViews;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.maroufb.beastchat.Entities.ChatRoom;
import com.maroufb.beastchat.R;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ChatRoomViewHolder extends RecyclerView.ViewHolder {

    @BindView(R.id.list_chat_room_lastMessage)
    TextView mLastMessage;

    @BindView(R.id.list_chat_room_newMessageIndicator)
    ImageView mLastMessageIndicator;

    @BindView(R.id.list_chat_room_userName)
    TextView mUserName;

    @BindView(R.id.list_chat_room_userPicture)
    ImageView mUserPicture;

    public ChatRoomViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void populate(ChatRoom chatRoom, String currentUserEmail){
        itemView.setTag(chatRoom);

        Picasso.get()
                .load(chatRoom.getFriendPicture())
                .into(mUserPicture);
        mUserName.setText(chatRoom.getFriendName());

        String lastMessageSent = chatRoom.getLastMessage();
        if(lastMessageSent.length() > 80){
            lastMessageSent = lastMessageSent.substring(0,80) + " ...";
        }

        if(!chatRoom.isSentLastMessage()){
            lastMessageSent = lastMessageSent +"(Draft)";
        }

        if(chatRoom.getLastMessageSenderEmail().equals(currentUserEmail)){
            lastMessageSent = "(Me) " + lastMessageSent;
        }

        if(!chatRoom.isLastMessageRead()){
            mLastMessageIndicator.setVisibility(View.VISIBLE);
        }else{
            mLastMessageIndicator.setVisibility(View.GONE);
        }

        mLastMessage.setText(lastMessageSent);

    }


}
