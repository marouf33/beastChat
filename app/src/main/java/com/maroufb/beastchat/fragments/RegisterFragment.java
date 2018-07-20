package com.maroufb.beastchat.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
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

public class RegisterFragment extends BaseFragment {

    @BindView(R.id.fragment_register_userName) EditText mUsernameEt;

    @BindView(R.id.fragment_register_userEmail) EditText mUserEmailEt;

    @BindView(R.id.fragment_register_userPassword) EditText mUserPasswordEt;

    @BindView(R.id.register_progressBar) ProgressBar mProgressBar;

    private Unbinder mUnbinder;

    private Socket mSocket;

    private LiveAccountServices mLiveAccountServices;

    private BaseFragmentActivity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            mSocket = IO.socket(Constants.IP_LOCAL_HOST);
        } catch (URISyntaxException e) {
            Log.i(RegisterFragment.class.getSimpleName(),e.getMessage() );
            Toast.makeText(getActivity(),"Can't connect",Toast.LENGTH_SHORT);
        }

        mSocket.connect();
        mSocket.on("message",accountResponse());
        mLiveAccountServices = LiveAccountServices.getInstance();
    }

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        mProgressBar.setVisibility(View.GONE);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSocket.disconnect();
    }

    @OnClick(R.id.fragment_register_loginButton)
    public void setLoginButton(){
        getActivity().finish();
    }

    @OnClick(R.id.fragment_register_registerButton)
    public void setRegisterButton(){
        mCompositeDisposable.add(mLiveAccountServices.sendRegistrationInfo(
                mUsernameEt,mUserEmailEt,mUserPasswordEt,mSocket,mProgressBar
        ));
    }

    private Emitter.Listener accountResponse(){
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject data = (JSONObject) args[0];
                mCompositeDisposable.add(mLiveAccountServices.registerResponse(data,mActivity));
            }
        };
    }
}
