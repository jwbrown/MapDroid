package com.cloudmade.example;

import com.cloudmade.api.R;
import com.cloudmade.api.view.CloudMadeMapView;

import android.app.Activity;
import android.os.Bundle;

public class SimpleMapActivity extends Activity {

	CloudMadeMapView mMapView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.simple);
		mMapView = (CloudMadeMapView) this.findViewById(R.id.mapView);
		mMapView.start();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mMapView.stop();
	}

}
