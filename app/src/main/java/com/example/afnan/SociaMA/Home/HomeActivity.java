package com.example.afnan.SociaMA.Home;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.afnan.SociaMA.Utils.MainfeedListAdapter;
import com.example.afnan.SociaMA.VPN.VPNFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import com.example.afnan.SociaMA.Add.AddActivity;
import com.example.afnan.SociaMA.Login.LoginActivity;
import com.example.afnan.SociaMA.Chat.ChatActivity;
import com.example.afnan.SociaMA.Profile.ProfileActivity;
import com.example.afnan.SociaMA.Search.SearchActivity;
import com.example.afnan.SociaMA.Utils.SectionPagerAdapter;
import com.example.afnan.SociaMA.Utils.UniversalImageLoader;
import com.example.afnan.SociaMA.R;

import com.example.afnan.SociaMA.Utils.ViewCommentsFragment;
import com.example.afnan.SociaMA.Models.Photo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;


public class HomeActivity extends AppCompatActivity implements
        MainfeedListAdapter.OnLoadMoreItemsListener {

    @Override
    public void onLoadMoreItems() {
        Log.d(TAG, "onLoadMoreItems: displaying more photos");
        HomeFragment fragment = (HomeFragment)getSupportFragmentManager()
                .findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
        if(fragment != null){
            fragment.displayMorePhotos();
        }
    }

    private static final String TAG = "HomeActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final int HOME_FRAGMENT = 1;
    private static final int VERIFY_PERMISSIONS_REQUEST = 1;

    private Context mContext = HomeActivity.this;

    //Firebase : Declare an instance of Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    //widgets
    private ViewPager mViewPager;
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    private Toolbar toolbar;
    private ImageButton mMune;
    private String ic_android_green;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Log.d(TAG, "onCreate: starting.");
        mViewPager = (ViewPager) findViewById(R.id.viewpager_container);
        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        mMune = (ImageButton) findViewById(R.id.btnMore);
        ic_android_green = createImageOnSDCard(R.drawable.ic_android_green);
        /*setupToolbar();*/


        if (netStatus().equals(true)) {
            setupFirebaseAuth();
            initImageLoader();
            circularFloatingActionMenu();
            setupViewPager();
        }else  {

            Snackbar snackbar = Snackbar
                    .make(mRelativeLayout, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(mContext, HomeActivity.class);
                            startActivity(i);
                        }
                    });

// Changing message text color
            snackbar.setActionTextColor(Color.RED);

// Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }
        if (checkPermissionsArray(com.example.afnan.SociaMA.Utils.Permissions.PERMISSIONS)) {
            setupViewPager();
        } else {
            verifyPermissions(com.example.afnan.SociaMA.Utils.Permissions.PERMISSIONS);
        }

        /*setupToolbar();*/
       /*mAuth.signOut();*/
    }

    private void setupToolbar(){

        ((HomeActivity.this)).setSupportActionBar(toolbar);
//        toolbar.setTitle(null);

        // Locate filter button and attach click listener

        /*mMune.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterPopup(v);
            }
        });*/
    }

    // Display anchored popup menu based on view selected
    private void showFilterPopup(View v) {
        android.support.v7.widget.PopupMenu popup = new android.support.v7.widget.PopupMenu(mContext, v);
        // Inflate the menu from xml
        popup.inflate(R.menu.popup_filters);
        // Setup menu item selection
        popup.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_Report:
                        Toast.makeText(mContext, "REPORT!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_SharePhoto:
                        onShareOnePhoto();
                        Toast.makeText(mContext, "SHARE PHOTO!", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_CopyShareUrl:
                        onShareText();
                        Toast.makeText(mContext, "Copy Share Url", Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.menu_Cancel:
                        Toast.makeText(mContext, "Cancel", Toast.LENGTH_SHORT).show();
                        return true;
                    default:
                        return false;
                }
            }
        });
        // Handle dismissal with: popup.setOnDismissListener(...);
        // Show the menu
        popup.show();
    }

    /**
     * Create and start intent to share a standard text value.
     */
    private void onShareText() {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is a text I'm sharing.");
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    /**
     * Create and start intent to share a photo with apps that can accept a single image
     * of any format.
     */
    private void onShareOnePhoto() {
        Uri path = FileProvider.getUriForFile(this, "com.example.afnan.SociaMA", new File(ic_android_green));

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "This is one image I'm sharing.");
        shareIntent.putExtra(Intent.EXTRA_STREAM, path);
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        shareIntent.setType("image/*");
        startActivity(Intent.createChooser(shareIntent, "Share..."));
    }

    /**
     * Create an image on the phone's SD card to later be able to share it.
     *
     * @param resID resource ID of an image coming from the res folder.
     * @return Return the path of the image that was created on the phone SD card.
     */

    private String createImageOnSDCard(int resID) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), resID);
        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES) + "/" + resID + ".jpg";
        File file = new File(path);
        try {
            OutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return file.getPath();
    }


    //make to display comments in post for users , connected with MainfeedListAdapter class
    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.camera), photo);
        args.putString(getString(R.string.home_activity), getString(R.string.home_activity));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(mFrameLayout.getVisibility() == View.VISIBLE){
            showLayout();
        }
    }



    // implement Universal image loader
    /*
    * https://github.com/nostra13/Android-Universal-Image-Loader
    * */
    private void initImageLoader(){
        UniversalImageLoader universalImageLoader = new UniversalImageLoader(mContext);
        ImageLoader.getInstance().init(universalImageLoader.getConfig());
    }

    // to Add 4 Tabs Camera , Home , chat and VPN in Home Activity
    private void setupViewPager()
    {
        SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());
        adapter.AddFragment(new CameraFragment()); //index 0
        adapter.AddFragment(new HomeFragment()); //index 1
        adapter.AddFragment(new ChatFragment()); //index 2
        adapter.AddFragment(new VPNFragment()); //index 3
        mViewPager.setAdapter(adapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        tabLayout.getTabAt(0).setIcon(R.drawable.ic_camera);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_timeline);
        tabLayout.getTabAt(2).setIcon(R.drawable.ic_chat);
        tabLayout.getTabAt(3).setIcon(R.drawable.ic_vpn);

    }


     /*
     ************************************ Firebase DataBase ****************************************
     */

    //checks to see if the 'user' is logged in
    private void checkCurrentUser(FirebaseUser user){
        Log.d(TAG, "checkCurrentUser: checking if user is logged in.");

        if (user != null) {
            // User is signed in
            Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());



        } else {
            // User is signed out
            Log.d(TAG, "onAuthStateChanged:signed_out");
            Intent intent = new Intent(mContext, LoginActivity.class);
            startActivity(intent);

        }
    }

    // Setup the firebase auth object
    private void setupFirebaseAuth(){

        //initialize the FirebaseAuth Object.
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();

                // check if the user is logged in
                checkCurrentUser(user);

            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        if (netStatus().equals(true)) {
            mAuth.addAuthStateListener(mAuthListener);
            mViewPager.setCurrentItem(HOME_FRAGMENT);
            checkCurrentUser(mAuth.getCurrentUser());
        }else {

            RelativeLayout relLayoutParent;
            relLayoutParent=(RelativeLayout) findViewById(R.id.relLayoutParent);
            Snackbar snackbar = Snackbar
                    .make(relLayoutParent, "No internet connection!", Snackbar.LENGTH_INDEFINITE)
                    .setAction("RETRY", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent i = new Intent(mContext, HomeActivity.class);
                            startActivity(i);
                        }
                    });

// Changing message text color
            snackbar.setActionTextColor(Color.RED);

// Changing action button text color
            View sbView = snackbar.getView();
            TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
            textView.setTextColor(Color.WHITE);
            snackbar.show();
        }

    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    /*
     *********************************** Floating Action Menu **************************************
     */
    //initialaize FAM

    // circularFloatingActionMenu Setup
    private void circularFloatingActionMenu() {

        final ImageView icon = new ImageView(this); // Create an icon
        icon.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(),R.drawable.ic_launch_black_24dp));

        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon)
                .setPosition(FloatingActionButton.POSITION_BOTTOM_RIGHT)
                .build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView homeIcon          = new ImageView(this);
        ImageView searchIcon        = new ImageView(this);
        ImageView addIcon           = new ImageView(this);
        ImageView profileIcon       = new ImageView(this);
        ImageView notificationIcon  = new ImageView(this);

        homeIcon            .setImageDrawable( ContextCompat.getDrawable(getApplicationContext(),R.mipmap.home) );
        profileIcon         .setImageDrawable( ContextCompat.getDrawable(getApplicationContext(),R.mipmap.profile) );
        notificationIcon    .setImageDrawable( ContextCompat.getDrawable(getApplicationContext(),R.mipmap.chat) );
        addIcon             .setImageDrawable( ContextCompat.getDrawable(getApplicationContext(),R.mipmap.add) );
        searchIcon          .setImageDrawable( ContextCompat.getDrawable(getApplicationContext(),R.mipmap.search) );

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
                PropertyValuesHolder pvhr = PropertyValuesHolder.ofFloat(View.ROTATION,45);
                ObjectAnimator animation = ObjectAnimator.ofPropertyValuesHolder(icon, pvhr);
                animation.start();
            }

            @Override
            public void onMenuClosed(FloatingActionMenu floatingActionMenu) {
                icon.setRotation(45);
                PropertyValuesHolder pvhr = PropertyValuesHolder.ofFloat(View.ROTATION,0);
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

    /*
     ***********************************************************************************************
     */
    public Boolean netStatus(){
        ConnectivityManager cm =
                (ConnectivityManager)mContext.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }
    public int getTask() {
        Log.d(TAG, "getTask: TASK: " + getIntent().getFlags());
        return getIntent().getFlags();

    }
    public boolean checkPermissionsArray(String[] permissions) {
        Log.d(TAG, "checkPermissionsArray: checking permissions array.");

        for (String check : permissions) { // check all of array
            if (!checkPermissions(check)) {
                return false;
            }
        }
        return true;
    }
    public void verifyPermissions(String[] permissions) {
        Log.d(TAG, "verifyPermissions: verifying permissions.");

        ActivityCompat.requestPermissions(
                HomeActivity.this,
                permissions,
                VERIFY_PERMISSIONS_REQUEST
        );
    }
    public boolean checkPermissions(String permission) {
        Log.d(TAG, "checkPermissions: checking permission: " + permission);

        int permissionRequest = ActivityCompat.checkSelfPermission(HomeActivity.this, permission);

        if (permissionRequest != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "checkPermissions: \n Permission was not granted for: " + permission);
            return false;
        } else {
            Log.d(TAG, "checkPermissions: \n Permission was granted for: " + permission);
            return true;
        }
    }
    public int getCurrentTabNumber() {

        return mViewPager.getCurrentItem();
    }
}