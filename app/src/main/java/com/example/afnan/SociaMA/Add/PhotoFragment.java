package com.example.afnan.SociaMA.Add;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.afnan.SociaMA.R;
import com.example.afnan.SociaMA.Profile.AccountSettingActivity;
import com.example.afnan.SociaMA.Utils.Permissions;

/**
 * Created by afnan on 08-Apr-18.
 */

public class PhotoFragment extends Fragment   {

    private static final String TAG = "PhotoFragment";

    //constant
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int  CAMERA_REQUEST_CODE = 5;

    Button btnOpenCamera;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started.");

        btnOpenCamera = (Button) view.findViewById(R.id.btnOpenCamera);
        btnOpenCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera.");

                if(((AddActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if(((AddActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])){
                        Log.d(TAG, "onClick: starting camera");
                        Intent CameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(CameraIntent, CAMERA_REQUEST_CODE);
                    }else{
                        Intent intent = new Intent(getActivity(), AddActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
            }
        });

        return view;
    }

    private boolean isRootTask(){
        return ((AddActivity) getActivity()).getTask() == 0;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null) {
            if (requestCode == CAMERA_REQUEST_CODE) {
                Log.d(TAG, "onActivityResult: done taking a photo.");
                Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");

                Bitmap bitmap;
                bitmap = (Bitmap) data.getExtras().get("data");

                if (isRootTask()) {
                    try {
                        Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                        Intent intent = new Intent(getActivity(), PublishActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                        startActivity(intent);
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                    }
                } else {
                    try {
                        Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
                        Intent intent = new Intent(getActivity(), AccountSettingActivity.class);
                        intent.putExtra(getString(R.string.selected_bitmap), bitmap);
                        intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                        startActivity(intent);
                        getActivity().finish();
                    } catch (NullPointerException e) {
                        Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                    }
                }

            }
        }
    }
}
