package com.maroufb.beastchat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.fragments.InboxFragment;
import com.maroufb.beastchat.utils.Constants;


public class InboxActivity extends BaseFragmentActivity {

    private GoogleApiClient mGoogleApiClient;

    @Override
    Fragment createFragment() {
        return InboxFragment.newInstancce();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_inbox,menu);
        return true;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        configureGoogleSignIn();

        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnSuccessListener( new OnSuccessListener<InstanceIdResult>() {
                    @Override
                    public void onSuccess(InstanceIdResult instanceIdResult) {
                        String deviceToken = instanceIdResult.getToken();
                        SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_INFO_PREFERENCE, Context.MODE_PRIVATE);
                        String userEmail = sharedPreferences.getString(Constants.USER_EMAIL,"");
                        if(deviceToken != null &&! userEmail.equals("")){
                            DatabaseReference tokenReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PATH_USER_TOKEN)
                                    .child(Constants.encodeEmail(userEmail));
                            tokenReference.child("token").setValue(deviceToken);

                            getSupportActionBar().setTitle(sharedPreferences.getString(Constants.USER_NAME,"") + "'s Inbox");
                        }
                    }
                });

    }

    // This method configures Google SignIn
    public void configureGoogleSignIn(){
        // Configure sign-in to request the user’s basic profile like name and email
        GoogleSignInOptions options = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .build();
        // Build a GoogleApiClient with access to GoogleSignIn.API and the options above.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, options)
                .build();
        mGoogleApiClient.connect();
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {
            case R.id.action_logout:
                SharedPreferences sharedPreferences = getSharedPreferences(Constants.USER_INFO_PREFERENCE, Context.MODE_PRIVATE);
                //      mProgressBar.setVisibility(View.VISIBLE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(Constants.USER_EMAIL, null);
                editor.putString(Constants.USER_NAME, null);
                editor.putString(Constants.USER_PICTURE, null);
                FirebaseAuth.getInstance().signOut();
                LoginManager.getInstance().logOut();
                Auth.GoogleSignInApi.signOut(mGoogleApiClient);

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                break;

            case R.id.action_create_new_message:
                Intent friendsActivityIntent = new Intent(getApplication(),FriendsActivity.class);
                startActivity(friendsActivityIntent);
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out);
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
