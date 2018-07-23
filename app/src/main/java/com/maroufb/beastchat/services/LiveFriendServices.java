package com.maroufb.beastchat.services;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.fragments.FindFriendsFragment;
import com.maroufb.beastchat.views.FindFriendsViews.FindFriendsAdapter;

import java.util.HashMap;

public class LiveFriendServices {
    private static LiveFriendServices mLiveFriendServices;

    public static LiveFriendServices getInstance() {
        if(mLiveFriendServices == null)
            mLiveFriendServices = new LiveFriendServices();
        return mLiveFriendServices;
    }

    public ValueEventListener getFriendRequestsSent(final FindFriendsAdapter adapter, final FindFriendsFragment fragment){
        final HashMap<String, User> userHashMap = new HashMap<>();

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userHashMap.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    userHashMap.put(user.getEmail(),user);
                }

                adapter.setmFriendRequestSentMap(userHashMap);
                fragment.setmFriendRequestsSentMap(userHashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }
}
