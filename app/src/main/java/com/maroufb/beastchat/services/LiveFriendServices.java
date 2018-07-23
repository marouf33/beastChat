package com.maroufb.beastchat.services;

public class LiveFriendServices {
    private LiveFriendServices mLiveFriendServices;

    public LiveFriendServices getInstance() {
        if(mLiveFriendServices == null)
            mLiveFriendServices = new LiveFriendServices();
        return mLiveFriendServices;
    }
}
