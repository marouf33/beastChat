package com.maroufb.beastchat.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.fragments.FindFriendsFragment;
import com.maroufb.beastchat.views.FindFriendsViews.FindFriendsAdapter;
import com.maroufb.beastchat.views.FriendRequestViews.FriendRequestAdapter;
import com.maroufb.beastchat.views.userFriendViews.UserFriendAdapter;
import com.roughike.bottombar.BottomBar;

import org.json.JSONException;
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


    public ValueEventListener getAllFriends(final UserFriendAdapter adapter, final TextView textView, final RecyclerView recyclerView){
        final List<User> userList = new ArrayList<>();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    userList.add(user);
                }

                if(userList.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
              }
                adapter.setUsers(userList);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public ValueEventListener getAllCurrentUsersFriendMap(final FindFriendsAdapter adapter){
        final HashMap<String,User> userHashMap = new HashMap<>();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userHashMap.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    userHashMap.put(user.getEmail(),user);
                }
                adapter.setCurrentUserFriendsMap(userHashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public ValueEventListener getAllFriendRequests(final FriendRequestAdapter adapter, final RecyclerView recyclerView, final TextView textView){

        final List<User> users = new ArrayList<>();

        return new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }
                if(users.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);

                }
                adapter.setUsers(users);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public Disposable sendMessage(final Socket socket, String messageSenderEmail, String messageSenderPicture, String messageText ){
        List<String> details = new ArrayList<>();
        details.add(messageSenderEmail);
        details.add(messageSenderPicture);
        details.add(messageText);

        Observable<List<String>> listObservable = Observable.just(details);

        return  listObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, Integer>() {
                    @Override
                    public Integer apply(List<String> strings) throws Exception {
                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("senderEmail", strings.get(0));
                            sendData.put("senderPicture", strings.get(1));
                            sendData.put("messageText", strings.get(2));
                            socket.emit("details", sendData);
                            return SERVER_SUCCESS;
                        }catch (JSONException e){
                            e.printStackTrace();
                            return SERVER_FAILURE;
                        }

                    }
                }).subscribeOn(AndroidSchedulers.mainThread())
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

    public Disposable approveDeclineFriendRequest(final Socket socket, String userEmail, String friendEmail, String requestCode){
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
                        sendData.put("userEmail", strings.get(0));
                        sendData.put("friendEmail", strings.get(1));
                        sendData.put("requestCode", strings.get(2));
                        socket.emit("friendRequestResponse",sendData);
                        return SERVER_SUCCESS;
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Integer>() {
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

    public ValueEventListener getFriendRequestsReceived(final FindFriendsAdapter adapter){
        final HashMap<String, User> userHashMap = new HashMap<>();

        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userHashMap.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    userHashMap.put(user.getEmail(),user);
                }

                adapter.setmFriendRequestReceivedMap(userHashMap);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public ValueEventListener getFriendRequestBottom(final BottomBar bottomBar, final int tagId){
        final List<User> users = new ArrayList<>();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                users.clear();

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user = snapshot.getValue(User.class);
                    users.add(user);
                }

                if(!users.isEmpty()){
                    bottomBar.getTabWithId(tagId).setBadgeCount(users.size());
                }else {
                    bottomBar.getTabWithId(tagId).removeBadge();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public List<User> getMatchingUsers(List<User> users, String userEmail){
        if(userEmail.isEmpty()){
            return users;
        }
        List<User> usersFound = new ArrayList<>();

        for(User user: users){
            if(user.getEmail().toLowerCase().startsWith(userEmail.toLowerCase())){
                usersFound.add(user);
            }
        }

        return usersFound;
    }
}
