package com.maroufb.beastchat.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maroufb.beastchat.Entities.ChatRoom;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.views.ChatRoomViews.ChatRoomViewHolder;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomAdapter extends RecyclerView.Adapter{

    private BaseFragmentActivity mActivity;
    private List<ChatRoom> mChatRooms;
    private LayoutInflater mInflater;
    private ChatRoomListener mListener;
    private String mCurrentEmailString;

    public ChatRoomAdapter(BaseFragmentActivity activity, ChatRoomListener listener, String currentEmailString) {
        mActivity = activity;
        mListener = listener;
        mCurrentEmailString = currentEmailString;
        mInflater = mActivity.getLayoutInflater();
        mChatRooms = new ArrayList<>();
    }

    public void setChatRooms(List<ChatRoom> chatRooms) {
        mChatRooms.clear();
        mChatRooms.addAll(chatRooms);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_chat_room,parent,false);
        final ChatRoomViewHolder chatRoomViewHolder = new ChatRoomViewHolder(view);
        chatRoomViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ChatRoom chatRoom = (ChatRoom) chatRoomViewHolder.itemView.getTag();
                mListener.OnChatRoomClicked(chatRoom);
            }
        });
        return chatRoomViewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ChatRoomViewHolder) holder).populate(mChatRooms.get(position),mCurrentEmailString);
    }

    @Override
    public int getItemCount() {
        return mChatRooms.size();
    }

    public interface  ChatRoomListener{

        void OnChatRoomClicked(ChatRoom chatRoom);

    }
}
