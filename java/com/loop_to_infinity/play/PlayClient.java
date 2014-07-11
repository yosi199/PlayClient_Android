package com.loop_to_infinity.play;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import Fragments.MediaControlsComponent;
import Fragments.Play_Main;
import Fragments.SoundCloudFragment;

public class PlayClient extends Activity {
    public static Context ctx = null;

    private Fragment main;
    private Fragment mSoundCloudFrag;
    private MediaControlsComponent mdc;

    private FragmentManager fm;

    private String[] mNavigationItems;
    private ListView mDrawerList;
    private DrawerLayout mDrawer;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ctx = this;

        // Sound Cloud fragment
        mSoundCloudFrag = new SoundCloudFragment();

        // Add just the main player fragment
        main = new Play_Main();
        mdc = new MediaControlsComponent();


        fm = getFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.add(R.id.frameContainer, main, "main");
        ft.setCustomAnimations(R.animator.fade_in, android.R.animator.fade_out);
        ft.add(R.id.mediaControllerFrame, mdc);
        ft.commit();
        getFragmentManager().executePendingTransactions();


        // Navigation Drawer
        mNavigationItems = getResources().getStringArray(R.array.nav_drawer);
        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        mDrawerList.setAdapter(new ArrayAdapter<String>(this, R.layout.drawer_list_itm, mNavigationItems));
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                switch (position) {
                    case 0:  // Local PC media player
                        FragmentTransaction ft2 = fm.beginTransaction();
                        ft2.replace(R.id.frameContainer, main, "main");
                        ft2.commit();
                        mDrawer.closeDrawers();
                        break;

                    case 1:  // Open the SoundCloud fragment
                        FragmentTransaction ft = fm.beginTransaction();
                        ft.replace(R.id.frameContainer, mSoundCloudFrag, "soundCloudFrag");
                        ft.commit();

                        mDrawer.closeDrawers();
                        break;
                }

            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.play_client, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {


        return true;
    }

}
