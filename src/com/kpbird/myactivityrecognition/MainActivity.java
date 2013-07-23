package com.kpbird.myactivityrecognition;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.ActivityRecognitionClient;

public class MainActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks,GooglePlayServicesClient.OnConnectionFailedListener {

	private ActivityRecognitionClient arclient;
	private PendingIntent pIntent;
	private BroadcastReceiver receiver;
	private TextView tvActivity;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		tvActivity = (TextView) findViewById(R.id.tvActivity);
		
		int resp =GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
		if(resp == ConnectionResult.SUCCESS){
			arclient = new ActivityRecognitionClient(this, this, this);
			arclient.connect();
			
			
		}
		else{
			Toast.makeText(this, "Please install Google Play Service.", Toast.LENGTH_SHORT).show();
		}
		
		receiver = new BroadcastReceiver() {
		    @Override
		    public void onReceive(Context context, Intent intent) {
		    	String v =  "Activity :" + intent.getStringExtra("Activity") + " " + "Confidence : " + intent.getExtras().getInt("Confidence") + "\n";
		    	v += tvActivity.getText();
		    	tvActivity.setText(v);
		    }
		  };
		  
		 IntentFilter filter = new IntentFilter();
		 filter.addAction("com.kpbird.myactivityrecognition.ACTIVITY_RECOGNITION_DATA");
		 registerReceiver(receiver, filter);
		
	}
	
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(arclient!=null){
			arclient.removeActivityUpdates(pIntent);
			arclient.disconnect();
		}
		unregisterReceiver(receiver);
	}
	
	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		Toast.makeText(this, "Connection Failed", Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onConnected(Bundle arg0) {
		Intent intent = new Intent(this, ActivityRecognitionService.class);
		pIntent = PendingIntent.getService(this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);
		arclient.requestActivityUpdates(1000, pIntent);   
	}
	@Override
	public void onDisconnected() {
	}

	
}
