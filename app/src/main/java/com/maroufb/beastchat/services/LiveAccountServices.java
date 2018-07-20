package com.maroufb.beastchat.services;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.activities.InboxActivity;
import com.maroufb.beastchat.utils.Constants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.observers.DisposableObserver;
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

    private final int USER_NO_ERRORS = 8;

    public static LiveAccountServices getInstance(){
        if(mLiveAccountServices == null){
            mLiveAccountServices = new LiveAccountServices();
        }
        return mLiveAccountServices;
    }

    public Disposable getAuthToken(JSONObject data, final BaseFragmentActivity activity, final SharedPreferences sharedPreferences
            ,final ProgressBar progressBar, GoogleApiClient GoogleApiClient){
        
        Observable<JSONObject> jsonObjectObservable = Observable.just(data);
        
        return jsonObjectObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<JSONObject, List<String>>() {
                    @Override
                    public List<String> apply(JSONObject jsonObject) throws Exception {
                        List<String>  userDetails = new ArrayList<>();
                        JSONObject serverData = jsonObject.getJSONObject("token");
                        String token = (String) serverData.get("authToken");
                        String email = (String) serverData.get("email");
                        String photo = (String) serverData.get("photo");
                        String userName = (String) serverData.get("displayName");

                        userDetails.add(token);
                        userDetails.add(email);
                        userDetails.add(photo);
                        userDetails.add(userName);
                        return userDetails;

                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<List<String>>(){
                    @Override
                    public void onNext(List<String> strings) {
                        String token = strings.get(0);
                        final String email = strings.get(1);
                        final String photo = strings.get(2);
                        final String userName = strings.get(3);

                        if(!email.equals("error")){
                            FirebaseAuth.getInstance().signInWithCustomToken(token).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(!task.isSuccessful()) {
                                        Toast.makeText(activity, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }else{
                                        if(progressBar != null){
                                            progressBar.setVisibility(View.GONE);
                                        }
                                        sharedPreferences.edit().putString(Constants.USER_EMAIL,email).apply();
                                        sharedPreferences.edit().putString(Constants.USER_NAME,userName).apply();
                                        sharedPreferences.edit().putString(Constants.USER_PICTURE,photo).apply();

                                        Intent intent = new Intent(activity, InboxActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        activity.startActivity(intent);
                                        activity.finish();
                                    }
                                }
                            });
                        }else if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onComplete() {
                        if(progressBar != null){
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }



    public Disposable SignInWithLoginToken(final Socket socket, final BaseFragmentActivity activity, String email, String name
                                           ,String profilePicURL, final AuthCredential authCredential, final ProgressBar progressBar){
        List<String> userDetails = new ArrayList<>();
        userDetails.add(email);
        userDetails.add(name);
        userDetails.add(profilePicURL);
        Observable<List<String>> userDetailObservable = Observable.just(userDetails);
        return userDetailObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, Integer>() {
                    @Override
                    public Integer apply(List<String> strings) throws Exception {
                        final String userEmail = strings.get(0);
                        final String userName = strings.get(1);
                        final String userProfilePicURL = strings.get(2);
                        FirebaseAuth.getInstance().signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(!task.isSuccessful()){
                                    Toast.makeText(activity.getApplicationContext(),task.getException().getLocalizedMessage(),Toast.LENGTH_LONG).show();
                                    progressBar.setVisibility(View.GONE);
                                }else{
                                    try {
                                        JSONObject loginData = new JSONObject();
                                        loginData.put("email",userEmail);
                                        loginData.put("name",userName);
                                        loginData.put("profilePicURL",userProfilePicURL);
                                        socket.emit("userInfo", loginData);
                                        FirebaseAuth.getInstance().signOut();
                                    }catch (JSONException e){
                                        e.printStackTrace();
                                        progressBar.setVisibility(View.GONE);
                                    }
                                }
                            }
                        });
                        return SERVER_SUCCESS;


                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Integer>(){
                    @Override  public void onNext(Integer integer) {}
                    @Override  public void onError(Throwable e) {}
                    @Override  public void onComplete() {}
                });

    }

    public Disposable sendLogingInfo(final EditText userEmailEt, final EditText userPasswordEt, final Socket socket
            , final BaseFragmentActivity activity , final ProgressBar progressBar){
        List<String> userDetails = new ArrayList<>();
        userDetails.add(userEmailEt.getText().toString());
        userDetails.add(userPasswordEt.getText().toString());

        Observable<List<String>> userDetailObservable = Observable.just(userDetails);
        return userDetailObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<List<String>, Integer>() {
                    @Override
                    public Integer apply(List<String> strings) throws Exception {
                        final String userEmail = strings.get(0);
                        String userPassword = strings.get(1);

                        if (userEmail.isEmpty())
                            return USER_ERROR_EMPTY_EMAIL;
                        if(!isEmailValid(userEmail))
                            return  USER_ERROR_EMAIL_BAD_FORMAT;
                        if(userPassword.isEmpty())
                            return USER_ERROR_EMPTY_PASSWORD;
                        if(userPassword.length() < 6)
                            return  USER_ERROR_PASSWORD_SHORT;

                        FirebaseAuth.getInstance().signInWithEmailAndPassword(userEmail,userPassword)
                                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()){
                                            Toast.makeText(activity,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                                        }else{
                                            JSONObject sendData = new JSONObject();
                                            try {
                                                sendData.put("email",userEmail);
                                                socket.emit("userInfo",sendData);
                                                FirebaseAuth.getInstance().signOut();
                                            }catch (JSONException e){
                                                e.printStackTrace();
                                                progressBar.setVisibility(View.GONE);
                                            }
                                        }
                                    }
                                });



                        return USER_NO_ERRORS;

                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<Integer>(){
                    @Override
                    public void onNext(Integer integer) {
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
                        }
                        progressBar.setVisibility(integer.intValue() == USER_NO_ERRORS ? View.VISIBLE : View.GONE);

                    }

                    @Override public void onError(Throwable e) {}

                    @Override public void onComplete() {}
                });

    }

    public Disposable sendRegistrationInfo(final EditText userNameEt, final EditText userEmailEt,
                                           final EditText userPasswordEt, final Socket socket, final ProgressBar progressBar){
        List<String> userDetails = new ArrayList<>();
        userDetails.add(userNameEt.getText().toString());
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
                        if(!isEmailValid(userEmail))
                            return  USER_ERROR_EMAIL_BAD_FORMAT;
                        if(userPassword.isEmpty())
                            return USER_ERROR_EMPTY_PASSWORD;
                        if(userPassword.length() < 6)
                            return  USER_ERROR_PASSWORD_SHORT;

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
                }).observeOn(AndroidSchedulers.mainThread()).subscribeWith(new DisposableObserver<Integer>(){
                    @Override
                    public void onNext(Integer integer) {
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
                        progressBar.setVisibility(integer.intValue() == SERVER_SUCCESS ? View.VISIBLE : View.GONE);
                    }

                    @Override public void onError(Throwable e) {}

                    @Override public void onComplete() {}
                });

    }

    private boolean isEmailValid(CharSequence email){
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public Disposable registerResponse(JSONObject data, final BaseFragmentActivity activity){
        Observable<JSONObject> jsonObjectObservable = Observable.just(data);
        return jsonObjectObservable
                .subscribeOn(Schedulers.io())
                .map(new Function<JSONObject, String>() {
                    @Override
                    public String apply(JSONObject jsonObject) throws Exception {
                        String message;

                        try {
                            JSONObject json = jsonObject.getJSONObject("message");
                            message = (String) json.get("text");
                            return message;
                        }catch (JSONException e){
                            return e.getMessage();
                        }
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<String>() {
                    @Override public void onNext(String stringResponse) {
                        if(stringResponse.equals("Success")){
                            activity.finish();
                            Toast.makeText(activity,"Registration Successful!", Toast.LENGTH_SHORT).show();
                        }else{
                            Toast.makeText(activity,stringResponse, Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override public void onError(Throwable e) {}

                    @Override public void onComplete() {}
                });
    }
}
