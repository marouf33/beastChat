package com.maroufb.beastchat.services;

import android.util.Log;
import android.util.Patterns;
import android.widget.EditText;

import org.json.JSONException;
import org.json.JSONObject;
import org.reactivestreams.Subscription;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import io.socket.client.Socket;

public class LiveAccountServices {

    private static LiveAccountServices mLiveAccountServices;

    private final int USER_ERROR_EMPTY_PASSWORD = 1;
    private final int USER_ERROR_EMPTY_EMAIL = 2;
    private final int USER_ERROR_EMPTY_USERNAME =3;
    private final int USER_ERROR_PASSWORD_SHORT = 4;
    private final int USER_ERROR_EMAIL_BAD_FORMAT = 5;

    private final int SERVER_SUCCESS = 6;
    private final int SERVER_FAILURE = 7;

    public static LiveAccountServices getInstance(){
        if(mLiveAccountServices == null){
            mLiveAccountServices = new LiveAccountServices();
        }
        return mLiveAccountServices;
    }

    public Disposable sendRegistrationInfo(final EditText userNameEt, final EditText userEmailEt,
                                             final EditText userPasswordEt, final Socket socket){
        List<String> userDetails = new ArrayList<>();
        userDetails.add(userEmailEt.getText().toString());
        userDetails.add(userEmailEt.getText().toString());
        userDetails.add(userPasswordEt.getText().toString());

        Observable<List<String>> userDetailObservable = Observable.just(userDetails);

        return userDetailObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, Integer>() {
                    @Override
                    public Integer apply(List<String> strings) throws Exception {
                        String userName = strings.get(0);
                        String userEmail = strings.get(1);
                        String userPassword = strings.get(2);

                        if(userName.isEmpty())
                            return USER_ERROR_EMPTY_USERNAME;
                        if (userEmail.isEmpty())
                            return USER_ERROR_EMPTY_EMAIL;
                        if(userPassword.isEmpty())
                            return USER_ERROR_EMPTY_PASSWORD;
                        if(userPassword.length() < 6)
                            return  USER_ERROR_PASSWORD_SHORT;
                        if(!isEmailValid(userEmail))
                            return  USER_ERROR_EMAIL_BAD_FORMAT;
                        JSONObject sendData = new JSONObject();
                        try {
                            sendData.put("email",userEmail);
                            sendData.put("userName", userName);
                            sendData.put("password",userPassword);
                            socket.emit("userData",sendData);
                            return SERVER_SUCCESS;

                        }catch (JSONException e){
                            Log.i(LiveAccountServices.class.getSimpleName(), e.getMessage());
                            return SERVER_FAILURE;
                        }

                    }
                }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        switch (integer.intValue()) {
                            case USER_ERROR_EMPTY_EMAIL:
                                userEmailEt.setError("Email Address Can't Be Empty.");
                                break;
                            case USER_ERROR_EMAIL_BAD_FORMAT:
                                userEmailEt.setError("Please check your email format.");
                                break;
                            case USER_ERROR_EMPTY_PASSWORD:
                                userPasswordEt.setError("Password Can't be blank.");
                                break;
                            case USER_ERROR_PASSWORD_SHORT:
                                userPasswordEt.setError("Password must at least 6 characters long.");
                                break;
                            case USER_ERROR_EMPTY_USERNAME:
                                userNameEt.setError("Username can't be empty");
                                break;
                        }
                    }
                });

    }

    private boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
