package com.maroufb.beastchat.services;

import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.maroufb.beastchat.Entities.ChatRoom;
import com.maroufb.beastchat.Entities.Message;
import com.maroufb.beastchat.Entities.User;
import com.maroufb.beastchat.fragments.FindFriendsFragment;
import com.maroufb.beastchat.utils.Constants;
import com.maroufb.beastchat.views.ChatRoomAdapter;
import com.maroufb.beastchat.views.FindFriendsViews.FindFriendsAdapter;
import com.maroufb.beastchat.views.FriendRequestViews.FriendRequestAdapter;
import com.maroufb.beastchat.views.MessagesViews.MessagesAdapter;
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


    public ValueEventListener getAllNewMessages(final BottomBar bottomBar,final int tagId){
        final List<Message> messages = new ArrayList<>();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();

                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    messages.add(message);
                }

                if(!messages.isEmpty()){
                    bottomBar.getTabWithId(tagId).setBadgeCount(messages.size());
                }else
                    bottomBar.getTabWithId(tagId).removeBadge();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public ValueEventListener getAllChatRooms(final RecyclerView recyclerView, final TextView textView,
                                              final ChatRoomAdapter adapter){
        final List<ChatRoom> chatRooms = new ArrayList<>();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                chatRooms.clear();
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    ChatRoom chatRoom = snapshot.getValue(ChatRoom.class);
                    chatRooms.add(chatRoom);
                }
                if(chatRooms.isEmpty()){
                    recyclerView.setVisibility(View.GONE);
                    textView.setVisibility(View.VISIBLE);
                }else{
                    recyclerView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.GONE);
                    adapter.setChatRooms(chatRooms);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
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

    public ValueEventListener getAllMessages(final RecyclerView recyclerView, final TextView textView, final ImageView imageView,
                                             final MessagesAdapter adapter, final String usereMail){
        final List<Message> messages = new ArrayList<>();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                messages.clear();
                DatabaseReference newMessagesReference = FirebaseDatabase.getInstance().getReference()
                        .child(Constants.FIREBASE_PATH_USER_NEW_MESSAGES)
                        .child(Constants.encodeEmail(usereMail));
                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    Message message = snapshot.getValue(Message.class);
                    newMessagesReference.child(message.getMessageId()).removeValue();
                    messages.add(message);
                }

                if(messages.isEmpty()){
                    imageView.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                }else{
                    imageView.setVisibility(View.GONE);
                    textView.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.setMessages(messages);

                    if(((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition() == adapter.getItemCount()-2 ||
                            (((LinearLayoutManager)recyclerView.getLayoutManager()).findLastVisibleItemPosition() == -1 && adapter.getItemCount()>0)){
                        recyclerView.smoothScrollToPosition(adapter.getItemCount() -1);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };
    }

    public Disposable sendMessage(final Socket socket, String messageSenderEmail,
                                  String messageSenderPicture, String messageText,
                                  String friendEmail, String messageSenderName){
        List<String> details = new ArrayList<>();
        details.add(messageSenderEmail);
        details.add(messageSenderPicture);
        details.add(messageText);
        details.add(friendEmail);
        details.add(messageSenderName);

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
                            sendData.put("friendEmail", strings.get(3));
                            sendData.put("senderName",strings.get(4));
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
