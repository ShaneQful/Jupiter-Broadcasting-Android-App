package jupiter.broadcasting.live.tv;

/*
 * Copyright (c) 2012 Shane Quigley
 *
 * This software is MIT licensed see link for details
 * http://www.opensource.org/licenses/MIT
 *
 * @author Shane Quigley
 * @hacked Adam Szabo
 */

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.Window;

import java.util.Hashtable;



public class RssListActivity extends SherlockFragmentActivity {
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    public Hashtable<String, String> audioFeedTable;
    public Hashtable<String, String> videoFeedTable;
    public String[] shows;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        shows = new String[]{getString(R.string.allshows),
                "Coder Radio",
                "Faux Show",
                "Linux Action Show",
                "Plan B",
                "SciByte",
                "Techsnap",
                "Unfilter"};

        audioFeedTable = new Hashtable<String, String>();
        audioFeedTable.put(getString(R.string.allshows), "http://feeds.feedburner.com/JupiterBroadcasting");
        audioFeedTable.put("Coder Radio", "http://feeds.feedburner.com/coderradiomp3");
        audioFeedTable.put("Faux Show", "http://www.jupiterbroadcasting.com/feeds/FauxShowMP3.xml");
        audioFeedTable.put("Linux Action Show", "http://feeds.feedburner.com/TheLinuxActionShow");
        audioFeedTable.put("SciByte", "http://feeds.feedburner.com/scibyteaudio");
        audioFeedTable.put("Techsnap", "http://feeds.feedburner.com/techsnapmp3");
        audioFeedTable.put("Unfilter", "http://www.jupiterbroadcasting.com/feeds/unfilterMP3.xml");
        audioFeedTable.put("Plan B", "http://feeds.feedburner.com/planbmp3");

        videoFeedTable = new Hashtable<String, String>();
        videoFeedTable.put(getString(R.string.allshows), "http://feeds2.feedburner.com/AllJupiterVideos");
        videoFeedTable.put("Coder Radio", "http://feeds.feedburner.com/coderradiovideo");
        videoFeedTable.put("Faux Show", "http://www.jupiterbroadcasting.com/feeds/FauxShowMobile.xml");
        videoFeedTable.put("Linux Action Show", "http://feeds.feedburner.com/linuxactionshowipodvid");
        videoFeedTable.put("SciByte", "http://feeds.feedburner.com/scibytelarge");
        videoFeedTable.put("Techsnap", "http://feeds.feedburner.com/techsnapmobile");
        videoFeedTable.put("Unfilter", "http://www.jupiterbroadcasting.com/feeds/unfilterMob.xml");
        videoFeedTable.put("Plan B", "http://feeds.feedburner.com/PlanBVideo");

        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.main);


        mTitle = mDrawerTitle = getTitle();

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.showlist);

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with shows
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, shows));

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        // enable ActionBar app icon to behave as action to toggle nav drawer (Not working for some reason)
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);

            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);

            }
        };
        //set the drawer icon to be clickable
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        //first start opens the drawer
        if (savedInstanceState == null) {
            mDrawerLayout.openDrawer(mDrawerList);
        }
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position); //start fragment to download items for the selected show
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (item.getItemId() == android.R.id.home) {

            if (mDrawerLayout.isDrawerOpen(mDrawerList)) {
                mDrawerLayout.closeDrawer(mDrawerList);
            } else {
                mDrawerLayout.openDrawer(mDrawerList);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments

        SherlockFragment fragment = new EpisodeListFragment();
        Bundle args = new Bundle();
        String afeed = audioFeedTable.get(shows[position]);
        String vfeed = videoFeedTable.get(shows[position]);
        args.putInt("SHOW_ID", position);
        args.putString("SHOW_AUDIO", afeed);
        args.putString("SHOW_VIDEO", vfeed);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction ft = fragmentManager.beginTransaction();
        //ft.setCustomAnimations(android.R.anim.fade_in,android.R.anim.fade_out);
        ft.replace(R.id.episodelist, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(shows[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }
}
