package jupiter.broadcasting.live.tv;


import java.util.Hashtable;
import java.util.List;

import jupiter.broadcasting.live.tv.parser.RssHandler;
import jupiter.broadcasting.live.tv.parser.SaxRssParser;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
/*
 * Copyright (c) 2012 Shane Quigley
 *
 * This software is MIT licensed see link for details
 * http://www.opensource.org/licenses/MIT
 * 
 * @author Shane Quigley
 */
public class EpisodeListActivity extends Activity{
	Hashtable<String, String[]> rssLinkTable;
	Context mContext;
	String showName;
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mContext = this;
		setContentView(R.layout.episodelist);
		ListView showsListView = (ListView) findViewById(R.id.episodelist);
		String feed = this.getIntent().getStringExtra("SHOW_FEED");
		showName = this.getIntent().getStringExtra("SHOW_NAME");
		SaxRssParser parser = new SaxRssParser();
		RssHandler customhandler = new RssHandler("title", "link", 15);
		parser.setRssHadler(customhandler);
		rssLinkTable = parser.parse(feed);
		List<String> episodes = parser.getTitles();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1, episodes);
		showsListView.setAdapter(adapter);
		showsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				final String urls[] = rssLinkTable.get(parent.getAdapter().getItem(position));
				Toast.makeText(getApplicationContext(),
						getString(R.string.opening) + " " + parent.getAdapter().getItem(position), Toast.LENGTH_LONG)
						.show();
				
				//clear the saved preferences, as we're loading a new episode
				SharedPreferences settings = getSharedPreferences("jupiter.broadcasting.live.tv", Context.MODE_APPEND);
				settings.edit().clear().clear();
				stopService(new Intent(mContext, MediaPlayerService.class));
				
				Intent intent = new Intent(mContext, MediaPlayerService.class);
				intent.putExtra("NEW_SHOW", true);
				intent.putExtra("SHOW_URI", Uri.parse(urls[1]).toString());
				intent.putExtra("SHOW_INFO", showName);
				startService(intent);
			}
		});
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		  switch (item.getItemId()) {
		    case R.id.menu_pause:
		    	stopService(new Intent(this, MediaPlayerService.class));
		    return true;
		    case R.id.menu_play:
		    	startService(new Intent(this, MediaPlayerService.class));
		    default:
		      return super.onOptionsItemSelected(item);
		  }
	}
}
