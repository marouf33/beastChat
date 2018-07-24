package com.maroufb.beastchat.services;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.fragments.FindFriendsFragment;
import com.maroufb.beastchat.views.FindFriendsViews.FindFriendsAdapter;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.Socket;

public class LiveFriendServices {
    private static LiveFriendServices mLiveFriendServices;

    private final int SERVER_SUCCESS = 6;
    private final int SERVER_FAILURE = 7;

    public static LiveFriendServices getInstance() {
        if(mLiveFriendServices == null)
            mLiveFriendServices = new LiveFriendServices();
        return mLiveFriendServices;
    }


    public Disposable addOrRemoveFriendRequest(final Socket socket, String userEmail, String friendEmail, String requestCode){
        List<String> details = new ArrayList<>();
        details.add(userEmail);
        details.add(friendEmail);
        details.add(requestCode);

        Observable<List<String>> listObservable = Observable.just(details);
        return listObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, Integer>() {
                    @Override
                    public Integer apply(List<String> strings) throws Exception {

                        JSONObject sendData = new JSONObject();
                        sendData.put("email", strings.get(1));
                        sendData.put("userEmail",strings.get(0));
                        sendData.put("requestCode",strings.get(2));
                        socket.emit("friendRequest",sendData);
                        return SERVER_SUCCESS;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Integer>(){
                    @Override
                    public void onNext(Integer integer) {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
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
