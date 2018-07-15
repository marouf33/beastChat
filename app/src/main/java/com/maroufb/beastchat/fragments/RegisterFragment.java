package com.maroufb.beastchat.fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.maroufb.beastchat.R;
import com.maroufb.beastchat.utils.Constants;

import java.net.URISyntaxException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.socket.client.IO;
import io.socket.client.Socket;

public class RegisterFragment extends BaseFragment {

    @BindView(R.id.fragment_register_userName) EditText mUsernameEt;

    @BindView(R.id.fragment_register_userEmail) EditText mUserEmailEt;

    @BindView(R.id.fragment_register_userPassword) EditText mUserPasswordEt;

    private Unbinder mUnbinder;

    private Socket mSocket;

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
    }

    public static RegisterFragment newInstance(){
        return new RegisterFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register,container,false);
        mUnbinder = ButterKnife.bind(this,rootView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
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
}
