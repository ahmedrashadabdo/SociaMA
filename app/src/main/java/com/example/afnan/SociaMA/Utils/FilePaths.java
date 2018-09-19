package com.example.afnan.SociaMA.Utils;

import android.os.Environment;

public class FilePaths {


        //"storage/emulated/0"
        //Return the primary shared/external storage directory.
        public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

        public String PICTURES = ROOT_DIR + "/Pictures";
        public String CAMERA = ROOT_DIR + "/DCIM/camera";

        public String FIREBASE_IMAGE_STORAGE = "photos/users/";

    }
