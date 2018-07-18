package com.maroufb.beastchat.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.activities.RegisterActivity;
import com.maroufb.beastchat.services.LiveAccountServices;
import com.maroufb.beastchat.utils.Constants;

import org.json.JSONObject;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class LoginFragment extends BaseFragment {


    @BindView(R.id.fragment_login_userEmail) EditText mUserEmailEt;

    @BindView(R.id.fragment_login_userPassword) EditText mUserPasswordEt;

    private Unbinder mUnbinder;

    private Socket mSocket;

    private BaseFragmentActivity mActivity;
    private LiveAccountServices mLiveAccountServices;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(LoginFragment.class.getSimpleName(),e.getMessage() );
            Toast.makeText(getActivity(),"Can't connect",Toast.LENGTH_SHORT);
        }

        mLiveAccountServices = LiveAccountServices.getInstance();
        mSocket.on("token",tokenListener());


        mSocket.connect();
    }

    public static  LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);

        mUnbinder = ButterKnife.bind(this,rootView);

        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }

    @OnClick(R.id.fragment_login_registerButton)
    public void setmRegisterButton(){
        startActivity(new Intent(getActivity(), RegisterActivity.class));
    }

    @OnClick(R.id.fragment_login_loginButton)
    public void setLoginButton(){
        mCompositeDisposable.add(mLiveAccountServices.sendLogingInfo(mUserEmailEt,mUserPasswordEt,mSocket,mActivity));
    }

    private Emitter.Listener tokenListener(){
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                mCompositeDisposable.add(mLiveAccountServices
                .getAuthToken(jsonObject,mActivity,mSharedPreferences));

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseFragmentActivity) context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mActivity = null;
    }
}
