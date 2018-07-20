package com.maroufb.beastchat.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.facebook.login.LoginManager;
import com.google.firebase.auth.FirebaseAuth;
import com.maroufb.beastchat.R;
import com.maroufb.beastchat.fragments.InboxFragment;
import com.maroufb.beastchat.utils.Constants;

import butterknife.internal.Utils;

public class InboxActivity extends BaseFragmentActivity {
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

                Intent intent = new Intent(this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
