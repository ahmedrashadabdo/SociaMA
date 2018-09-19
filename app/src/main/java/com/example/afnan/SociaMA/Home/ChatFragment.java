package com.example.afnan.SociaMA.Home;

import android.app.Notification;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.afnan.SociaMA.Profile.AccountSettingActivity;
import com.example.afnan.SociaMA.Profile.ProfileActivity;
import com.example.afnan.SociaMA.R;
import com.example.afnan.SociaMA.Search.SearchActivity;


/**
 * Created by Memo on 11/02/2018.
 */

public class ChatFragment extends Fragment {
    private static final String TAG = "ChatFragment";

    private TextView  mCreate ,mSearch;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle
            savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        mCreate = (TextView) view.findViewById(R.id.iv_create);
        mCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                startActivity(intent);
            }
        });

        mSearch = (TextView) view.findViewById(R.id.iv_search);
        mSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SearchActivity.class);
                startActivity(intent);
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        return view;

    }
}
