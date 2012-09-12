package jupiter.broadcasting.live.tv;


import java.util.Hashtable;
import java.util.List;
import jupiter.broadcasting.live.tv.parser.*;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.episodelist);
		ListView showsListView = (ListView) findViewById(R.id.episodelist);
		String feed;
		feed = this.getIntent().getStringExtra("SHOW_FEED");
		SaxRssParser parser = new SaxRssParser();
		RssHandler customhandler = new RssHandler("title", "link", 15);
		parser.setRssHadler(customhandler);
		rssLinkTable = parser.parse(feed);
		List<String> episodes = parser.getTitles();
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
					android.R.layout.simple_list_item_1, android.R.id.text1, episodes);
		showsListView.setAdapter(adapter);
		final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		showsListView.setOnItemClickListener(new OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
				final String urls[] = rssLinkTable.get(parent.getAdapter().getItem(position));
				Toast.makeText(getApplicationContext(),
						"Opening : " + parent.getAdapter().getItem(position), Toast.LENGTH_LONG)
						.show();
				alertbox.setMessage("Would like to :");
				alertbox.setPositiveButton("Stream", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(urls[1]));
						i.setDataAndType(Uri.parse(urls[1]), "audio/mp3");
						startActivity(i);
					}
				});
				alertbox.setNegativeButton("Open Web Page", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Intent i = new Intent(Intent.ACTION_VIEW,Uri.parse(urls[0]));
					    startActivity(i);
					}
				});
				alertbox.show();
			}
		});
	}
}
