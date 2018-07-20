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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.internal.CallbackManagerImpl;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.activities.BaseFragmentActivity;
import com.maroufb.beastchat.activities.RegisterActivity;
import com.maroufb.beastchat.application.BaseApplication;
import com.maroufb.beastchat.services.LiveAccountServices;
import com.maroufb.beastchat.utils.Constants;

import org.json.JSONException;
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

    @BindView(R.id.login_progressBar)  ProgressBar mProgressBar;

    private Unbinder mUnbinder;

    private Socket mSocket;

    private BaseFragmentActivity mActivity;
    private LiveAccountServices mLiveAccountServices;

    @BindView(R.id.activity_login_facebook_button)
    LoginButton facebookButton;

    private CallbackManager mCllbackManager;

    private GoogleApiClient mGoogleApiClient;


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
        mCllbackManager = CallbackManager.Factory.create();
        configureGoogleSignIn();

        mSocket.connect();
    }

    // This method configures Google SignIn
    public void configureGoogleSignIn(){
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestIdToken(Constants.GOOGLE_ID)
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity() /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getActivity(),connectionResult.getErrorMessage(),Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
    }

    public static  LoginFragment newInstance(){
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login,container,false);

        mUnbinder = ButterKnife.bind(this,rootView);
        mProgressBar.setVisibility(View.GONE);
        return rootView;
    }



    @OnClick(R.id.login_with_google)
    public void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, Constants.GOOGLE_SIGN_IN);
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
        mCompositeDisposable.add(mLiveAccountServices.sendLogingInfo(mUserEmailEt,mUserPasswordEt,mSocket,mActivity,mProgressBar));
    }

    @OnClick(R.id.activity_login_facebook_button)
    public void setFacebookLoginButton(){



        facebookButton.setReadPermissions("email","public_profile");

        facebookButton.registerCallback(mCllbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                GraphRequest graphRequest = GraphRequest.newMeRequest(loginResult.getAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        try{
                            mProgressBar.setVisibility(View.VISIBLE);
                            String email = object.getString("email");
                            String name = object.getString("name");
                            AuthCredential authCredential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                            mCompositeDisposable.add(mLiveAccountServices.SignInWithLoginToken(mSocket,mActivity,email,name,authCredential,mProgressBar));
                        }   catch (JSONException e){
                            mProgressBar.setVisibility(View.GONE);
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,email,gender,birthday");
                graphRequest.setParameters(parameters);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                mProgressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(FacebookException error) {
                mProgressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(),error.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }

    private Emitter.Listener tokenListener(){
        return new Emitter.Listener() {
            @Override
            public void call(Object... args) {
                JSONObject jsonObject = (JSONObject) args[0];
                mCompositeDisposable.add(mLiveAccountServices
                .getAuthToken(jsonObject,mActivity,mSharedPreferences,mProgressBar,mGoogleApiClient));

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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==Constants.GOOGLE_SIGN_IN){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                GoogleSignInAccount account = result.getSignInAccount();
                AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                mProgressBar.setVisibility(View.VISIBLE);
                mCompositeDisposable.add(mLiveAccountServices.SignInWithLoginToken(mSocket,mActivity,account.getEmail(),
                        account.getDisplayName(),credential,mProgressBar));
            }else{
                Log.e(getActivity().getClass().getSimpleName(), "Login Unsuccessful. ");
                Toast.makeText(getContext(), "Login Unsuccessful", Toast.LENGTH_SHORT)
                        .show();
            }
        }
        else if (requestCode == CallbackManagerImpl.RequestCodeOffset.Login.toRequestCode()) {
            mCllbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }
}
