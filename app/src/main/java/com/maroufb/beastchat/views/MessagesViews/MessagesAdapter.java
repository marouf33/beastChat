package com.maroufb.beastchat.views.MessagesViews;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.maroufb.beastchat.Entities.Message;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class MessagesAdapter extends RecyclerView.Adapter {

    private BaseFragmentActivity mActivity;
    private List<Message> mMessages;
    private LayoutInflater mInflater;

    private String mCurrentUserEmail;

    public MessagesAdapter(BaseFragmentActivity activity, String currentUserEmail) {
        mActivity = activity;
        mCurrentUserEmail = currentUserEmail;
        mMessages = new ArrayList<>();
        mInflater = mActivity.getLayoutInflater();
    }

    public void setMessages(List<Message> messages) {
        mMessages.clear();
        mMessages.addAll(messages);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(R.layout.list_messages,parent,false);
        return new MessagesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((MessagesViewHolder) holder).populate(mMessages.get(position),mCurrentUserEmail);
    }

    @Override
    public int getItemCount() {
        return mMessages.size();
    }
}
