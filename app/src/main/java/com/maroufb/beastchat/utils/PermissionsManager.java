package com.maroufb.beastchat.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.maroufb.beastchat.fragments.BaseFragment;

public class PermissionsManager {

    public static final int EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE = 10;
    public static final int EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE  = 11;
    public static final int EXTERNAL_CAMERA_PERMISSION_REQUEST_CODE        = 12;
    public static final int EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE_FOR_CAMERA = 13;
    public static final int EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE_FOR_CAMERA  = 14;



    private BaseFragment mFragment;

    public PermissionsManager(BaseFragment fragment) {
        mFragment = fragment;
    }



    public boolean checkPermissionForReadExternalStorage(){
        return ContextCompat.checkSelfPermission(mFragment.getActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForWriteExternalStorage(){
        return ContextCompat.checkSelfPermission(mFragment.getActivity(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public boolean checkPermissionForCamera(){
        return ContextCompat.checkSelfPermission(mFragment.getActivity(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    public void requestPermissionForReadExternalStorage(boolean forCamera){
        if(ActivityCompat.shouldShowRequestPermissionRationale(mFragment.getActivity(),Manifest.permission.READ_EXTERNAL_STORAGE)){
            Toast.makeText(mFragment.getActivity()," External Storage permission is needed. Please allow the app access to it.",
                    Toast.LENGTH_SHORT).show();
        }
        mFragment.requestPermissions( new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                forCamera? EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE_FOR_CAMERA:
                           EXTERNAL_STORAGE_READ_PERMISSION_REQUEST_CODE);

    }

    public void requestPermissionForWriteExternalStorage(boolean forCamera){
        if(ActivityCompat.shouldShowRequestPermissionRationale(mFragment.getActivity(),Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(mFragment.getActivity()," External Storage permission is needed. Please allow the app access to it.",
                    Toast.LENGTH_SHORT).show();
        }
        mFragment.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                forCamera? EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE_FOR_CAMERA:
                           EXTERNAL_STORAGE_WRITE_PERMISSION_REQUEST_CODE);

    }

    public void requestPermissionForCamera(){
        if(ActivityCompat.shouldShowRequestPermissionRationale(mFragment.getActivity(),Manifest.permission.CAMERA)){
            Toast.makeText(mFragment.getActivity()," Camera permission is needed. Please allow the app access to it.",
                    Toast.LENGTH_SHORT).show();
        }
        mFragment.requestPermissions(new String[]{Manifest.permission.CAMERA},
                EXTERNAL_CAMERA_PERMISSION_REQUEST_CODE);

    }
}
