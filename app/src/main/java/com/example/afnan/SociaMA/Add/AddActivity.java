package com.example.afnan.SociaMA.Add;


import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.afnan.SociaMA.Chat.ChatActivity;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.example.afnan.SociaMA.Home.HomeActivity;
import com.example.afnan.SociaMA.Profile.ProfileActivity;
import com.example.afnan.SociaMA.R;
import com.example.afnan.SociaMA.Search.SearchActivity;
import com.example.afnan.SociaMA.Utils.SectionPagerAdapter;


public class AddActivity extends AppCompatActivity {
    private static final String TAG = "AddActivity";

    //constants
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private ViewPager mViewPager;

    private Context mContext = AddActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);
        Log.d(TAG, "onCreate: started.");

        circularFloatingActionMenu();

        if (checkPermissionsArray(com.example.afnan.SociaMA.Utils.Permissions.PERMISSIONS)) {
            setupViewPager();
        } else {
            verifyPermissions(com.example.afnan.SociaMA.Utils.Permissions.PERMISSIONS);
        }
    }

    /*
    ******************************* Return the Current Tab Number **********************************
    */
    //0 = GalleryFragment
    //1 = PhotoFragment

    public int getCurrentTabNumber() {

        return mViewPager.getCurrentItem();
    }

    /*
    **************** setup viewpager for manager the Gallery and Photo Fragments *******************
    */

    private void setupViewPager() {
        SectionPagerAdapter adapter =  new SectionPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new GalleryFragment());
        adapter.AddFragment(new PhotoFragment());

        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);//viewpager_container
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabBottom);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setText(getString(R.string.gallery));
        tabLayout.getTabAt(1).setText(getString(R.string.camera));

    }

    /*
    **************** verifiy all the permissions checked and passed to the array *******************
    */

    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                AddActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }

    public int getTask() {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();

    }
            /*
            ******************** Check permissions in all of array *********************
            */

    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (String check : permissions) { // check all of array
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }


    /*
     ******************** Check a single permission is it has been verified *********************
    */

    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(AddActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }

    /*
     *********************************** Floating Action Menu **************************************
     */

    // circularFloatingActionMenu Setup
    private void circularFloatingActionMenu() {

        final ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_launch_black_24dp));

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView homeIcon = new ImageView(this);
        ImageView searchIcon = new ImageView(this);
        ImageView addIcon = new ImageView(this);
        ImageView profileIcon = new ImageView(this);
        ImageView notificationIcon = new ImageView(this);

        homeIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.home));
        profileIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.profile));
        notificationIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.chat));
        addIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.add));
        searchIcon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.mipmap.search));

        SubActionButton homeButton = itemBuilder.setContentView(homeIcon).build();
        SubActionButton searchButton = itemBuilder.setContentView(searchIcon).build();
        SubActionButton addButton = itemBuilder.setContentView(addIcon).build();
        SubActionButton profileButton = itemBuilder.setContentView(profileIcon).build();
        SubActionButton notificationButton = itemBuilder.setContentView(notificationIcon).build();

        //show icons in Floating Action Menu
        final FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(profileButton)
                .addSubActionView(notificationButton)
                .addSubActionView(addButton)
                .addSubActionView(searchButton)
                .addSubActionView(homeButton)
                // ...
                .attachTo(actionButton)
                .build();

        actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
            @Override
            public void onMenuOpened(FloatingActionMenu floatingActionMenu) {
                icon.setRotation(0);
                PropertyValuesHolder pvhr = PropertyValuesHolder.ofFloat(View.ROTATION, 45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhr);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                icon.setRotation(45);
                PropertyValuesHolder pvhr = PropertyValuesHolder.ofFloat(View.ROTATION, 0);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhr);
                animation.start();
            }
        });
        homeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "HOME", Toast.LENGTH_SHORT).show();
                Intent hometnt = new Intent(mContext, HomeActivity.class);
                startActivity(hometnt);
                finish();
            }
        });

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "SEARCH", Toast.LENGTH_SHORT).show();
                Intent searchtnt = new Intent(mContext, SearchActivity.class);
                startActivity(searchtnt);
                finish();
            }
        });

        addIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "ADD", Toast.LENGTH_SHORT).show();
                Intent addtnt = new Intent(mContext, AddActivity.class);
                startActivity(addtnt);
                finish();
            }
        });

        notificationIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "New Chat", Toast.LENGTH_SHORT).show();
                Intent notificationtnt = new Intent(mContext, ChatActivity.class);
                startActivity(notificationtnt);
                finish();
            }
        });

        profileIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "PROFILE", Toast.LENGTH_SHORT).show();
                Intent profiletnt = new Intent(mContext, ProfileActivity.class);
                startActivity(profiletnt);
                finish();
            }
        });
    }
}




