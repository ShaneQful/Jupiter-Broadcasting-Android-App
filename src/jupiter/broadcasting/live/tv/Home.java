package jupiter.broadcasting.live.tv;

import java.io.IOException;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
/*
 * Copyright (c) 2012 Shane Quigley
 *
 * This software is MIT licensed see link for details
 * http://www.opensource.org/licenses/MIT
 * 
 * @author Shane Quigley
 */
public class Home extends Activity {
	/** Called when the activity is first created. */
	private final int NOTIFICATION_ID = 3434;
	MediaPlayer mp = new MediaPlayer();
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.startscreen);
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
  		mNotificationManager.cancel(NOTIFICATION_ID);
		final Button play = (Button)this.findViewById(R.id.button1);
		ImageView pic = (ImageView)this.findViewById(R.id.imageView1);
		pic.setOnClickListener(new OnClickListener() {
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jupiterbroadcasting.com"));
				startActivity(i);
			}
		}
		);
		Button donate = (Button) this.findViewById(R.id.button3);
		donate.setOnClickListener(new OnClickListener() {
			
			public void onClick(View arg0) {
				Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.jupiterbroadcasting.com/support-us/"));
				startActivity(i);
				
			}
		}
		);
		Button rss = (Button) this.findViewById(R.id.button2);
		rss.setOnClickListener(new OnClickListener() {
			
			public void onClick(View v) {
				Intent myIntent = new Intent(v.getContext(), RssListActivity.class);
				startActivityForResult(myIntent, 0);
			}
		});

		final AlertDialog.Builder alertbox = new AlertDialog.Builder(this);
		play.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				if(!mp.isPlaying()){
					alertbox.setMessage(R.string.whichstream);
					alertbox.setPositiveButton(R.string.audio, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							try {
								mp.setDataSource("http://jblive.fm/");
							} catch (IllegalArgumentException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							try {
								mp.prepare();
							} catch (IllegalStateException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							mp.start();
							if(mp.isPlaying()){//Incase there is a network issue and the stream doesn't work
								play.setText(R.string.pause);
							}
						}
					});
					alertbox.setNegativeButton(R.string.video, new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface arg0, int arg1) {
							Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("rtsp://videocdn-us.geocdn.scaleengine.net/jblive/jblive.stream"));
							startActivity(i);						
						}
					});
					alertbox.show();
				}else{
					mp.stop();
					play.setText("Play");
				}
			}

		});
	}
	@Override protected void onResume() {
		super.onResume();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		mNotificationManager.cancel(NOTIFICATION_ID);//For good measure because app pauses before it quits aswell as on pause

	}
	@Override protected void onPause() {
		super.onPause();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		if(mp.isPlaying()){
			//mp.stop();
			//mp.release();

			mNotificationManager.cancel(NOTIFICATION_ID);
			Notification notification = new Notification(R.drawable.icon, "Jupiter Broadcasting",System.currentTimeMillis());

			Intent notificationIntent = new Intent(this, Home.class);
			notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

			PendingIntent intent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
			notification.setLatestEventInfo(getApplicationContext(),"Jupiter Broadcasting", getString(R.string.plaiyinglivestream), intent);

			notification.flags = Notification.FLAG_ONGOING_EVENT ;   
			mNotificationManager.notify(NOTIFICATION_ID,notification);
		}else{
			mNotificationManager.cancel(NOTIFICATION_ID);
		}
	}
	@Override protected void onDestroy() {
		super.onDestroy();
		String ns = Context.NOTIFICATION_SERVICE;
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(ns);
		if(mp.isPlaying()){
			mp.stop();
			mp.release();
		}
		mNotificationManager.cancel(NOTIFICATION_ID);//because onPause is called first

	}

}