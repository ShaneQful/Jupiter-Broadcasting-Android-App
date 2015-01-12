package jupiter.broadcasting.live.tv;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class MediaPlayerService extends Service {
	private MediaPlayer mediaPlayer;
	
	private boolean isPlaying = false;
	private String showUri;
	private String showInfo;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		if(isPlaying == false) {
			this.isPlaying = true;
			
			int lastPosition = -1;
			
			boolean newShow = intent.getBooleanExtra("NEW_SHOW", false);
			if( newShow == true ) {
				showUri = intent.getStringExtra("SHOW_URI");
				showInfo = intent.getStringExtra("SHOW_INFO");
			}
			else {
				SharedPreferences settings = getSharedPreferences("jupiter.broadcasting.live.tv", Context.MODE_APPEND);
				lastPosition = settings.getInt("LAST_POSITION", -1); //get the last position, else returns default -1
				showUri = settings.getString("LAST_SHOW_URI", intent.getStringExtra("SHOW_URI"));
				showInfo = settings.getString("LAST_SHOW_INFO", intent.getStringExtra("SHOW_INFO"));
			}
			
			play(showUri, showInfo, lastPosition);
			return (START_NOT_STICKY);		
		}
		return -1;
	}

	private boolean play(String showUri, String showInfo, int lastPosition) {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(showUri);
			mediaPlayer.prepare();
			if (lastPosition != -1) {
				mediaPlayer.seekTo(lastPosition);
				mediaPlayer.start();
			}
			else {
				mediaPlayer.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(getBaseContext(), "Could not open episode, please try again later!", Toast.LENGTH_SHORT)
					.show();
			return false;
		}
		
		Intent i=new Intent(this, EpisodeListActivity.class);
		i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|
                Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi=PendingIntent.getActivity(this, 0, i, 0);
		
		Notification notification = new Notification(R.drawable.icon,
                "Jupiter Broadcasting",
                System.currentTimeMillis());
		notification.setLatestEventInfo(this, "Jupiter Broadcasting",
                "Now Playing: " + showInfo,
                pi);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		
		startForeground(9999, notification);
		
		return true;
	}

	@Override
	public void onDestroy() {
		SharedPreferences settings = getSharedPreferences("jupiter.broadcasting.live.tv", Context.MODE_APPEND);
		settings.edit().putInt("LAST_POSITION", mediaPlayer.getCurrentPosition()).commit();
		settings.edit().putString("LAST_SHOW_URI", showUri).commit();
		settings.edit().putString("LAST_SHOW_INFO", showInfo).commit();
		
		stop();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return (null);
	}

	private void stop() {
		if (isPlaying) {
			Log.w(getClass().getName(), "Got to stop()!");
			isPlaying = false;
			mediaPlayer.pause();
			stopForeground(true);
		}
	}

}
