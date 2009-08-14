/*
 * Copyright 2009 CloudMade.
 *
 * Licensed under the GNU Lesser General Public License, Version 3.0;
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.cloudmade.api.view;

import com.cloudmade.api.R;
import com.cloudmade.api.Utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.widget.Scroller;

/**
 * 
 * @author Alexander Kipar
 *
 */
public final class CloudMadeMapView extends View implements Constants {
	
	private VelocityTracker mVelocityTracker;
	private Scroller mScroller;
	
	private int iOldX = -1, iOldY = -1;
	private int iDx = 0, iDy = 0;
	
	private int iStyle = 2;
	private int iZoom = 15;
	private float fLat = 52.377538f;
	private float fLon = 4.895375f;
	
	private CloudMadeMapTileCache mCache;
	
	private final Paint mPaint;
	
	private int iScrW = -1, iScrH = -1;
	private int iScrW2 = -1, iScrH2 = -1;
	
	private int iScrX = 0, iScrY = 0;
	private int iCenterX = 0, iCenterY = 0;
	
	private boolean bIsActive = false;
	
	private final Handler mInvalidateHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			if (CloudMadeMapView.this.bIsActive) {
				CloudMadeMapView.this.invalidate();
				Log.v("CloudMadeMapView", "mInvalidateHandler");
			}
		}
		
	};
	
	private final Thread mUpdater = new Thread() {
		@Override
		public void run() {
			while (bIsActive) {
				synchronized (mScroller) {
					Scroller scroller = mScroller;
					if (scroller.isFinished()) {
						try {
							scroller.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					scroller.computeScrollOffset();
					iDx = scroller.getCurrX();
					iDy = scroller.getCurrY();
					CloudMadeMapView.this.onChange(iDx, iDy);
					try {
						Thread.sleep(30);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	
	public CloudMadeMapView(Context context) {
		this(context, null);		
	}
	
	public CloudMadeMapView(Context context, AttributeSet attrs) {
		this(context, attrs, -1);
	}
	
	public CloudMadeMapView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray attr = context.obtainStyledAttributes(attrs,
				R.styleable.CloudMadeMapView);
		this.setStyle(2);
		this.setZoom(attr.getInt(R.styleable.CloudMadeMapView_zoom, 12));
		this.setLatitude(attr.getFloat(R.styleable.CloudMadeMapView_latitude, 52.377538f));
		this.setLongitude(attr.getFloat(R.styleable.CloudMadeMapView_longitude, 4.895375f));
		mCache = new CloudMadeMapTileCache(context, 
				attr.getString(R.styleable.CloudMadeMapView_api_key), mInvalidateHandler);		
		mCache.setSizeMax(80);
		mScroller = new Scroller(context);
		mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		mPaint.setStyle(Paint.Style.FILL);
		mCache.setParams(iStyle, iZoom);
	}

	public void start() {
		this.onStartMapping();
		bIsActive = true;
		mUpdater.start();		
		Log.v("CloudMadeMapView", "start ");
	}
	
	public void clearCash() {
		
	}
	
	public void stop() {
		bIsActive = false;
		Log.v("CloudMadeMapView", "stop ");
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		iScrW = w;
		iScrH = h;
		iScrW2 = iScrW / 2;
		iScrH2 = iScrH / 2;
		Log.v("CloudMadeMapView", "onSizeChanged w = " + w + " : h = " + h);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		Log.v("CloudMadeMapView", "onDraw ");
		if (bIsActive) {
			Log.v("CloudMadeMapView", "onDraw " + iScrX + " "  + iScrY);
			CloudMadeMapTile[] tile = mCache.getMap(iScrX, iScrY, iScrW, iScrH);
			for (int i = 0; i < 9; i++) {
				if (tile[i] != null && tile[i].bitmap != null) {
					int x = tile[i].x * TILE_SIZE - iScrX;
					int y = tile[i].y * TILE_SIZE - iScrY;
					canvas.drawBitmap(tile[i].bitmap, x, y, mPaint);
					Log.v("CloudMadeMapView", "tile draw " + x + " " + y);
				} else {
					Log.v("CloudMadeMapView", "tile " + i + " NULL");
				}
			}
		}
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_DPAD_LEFT:
			MotionEvent motionEvent = MotionEvent.obtain(200, 200, 
					MotionEvent.ACTION_DOWN, iCenterX, iCenterY, 0);
			this.onTouchEvent(motionEvent);
			motionEvent = MotionEvent.obtain(200, 200, 
					MotionEvent.ACTION_MOVE, iCenterX - 64, iCenterY, 0);
			this.onTouchEvent(motionEvent);
			motionEvent = MotionEvent.obtain(200, 200, 
					MotionEvent.ACTION_UP, iCenterX - 64, iCenterY, 0);
			this.onTouchEvent(motionEvent);
			return true;
		case KeyEvent.KEYCODE_DPAD_RIGHT:
			this.onChange(64, 0);
			return true;
		case KeyEvent.KEYCODE_DPAD_UP:
			return true;
		case KeyEvent.KEYCODE_DPAD_DOWN:
			return true;
		default:
			return false;
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		switch (action) {
		case MotionEvent.ACTION_DOWN: {
			mVelocityTracker = VelocityTracker.obtain();
			iOldX = iDx + (int) event.getX();
			iOldY = iDy + (int) event.getY();
			break;
		}
		case MotionEvent.ACTION_MOVE: {
			mVelocityTracker.addMovement(event);
			int deltaX = (int) (iOldX - event.getX());
			int deltaY = (int) (iOldY - event.getY());
			iOldX = (int) event.getX();
			iOldY = (int) event.getY();
			Scroller scroller = mScroller;
			scroller.startScroll(iDx, iDy, -deltaX, -deltaY);
			synchronized (scroller) {
				scroller.notify();
			}
			break;
		}
		case MotionEvent.ACTION_UP: {
			VelocityTracker tracker = mVelocityTracker;
			tracker.addMovement(event);
			tracker.computeCurrentVelocity(1000);
			int vx = (int) tracker.getXVelocity();
			int vy = (int) tracker.getYVelocity();
			vx = vx * 3 / 4;
			vy = vy * 3 / 4;
			Scroller scroller = mScroller;
			scroller.fling(iDx, iDy, vx, vy, 0, 1000, 0, 1000);
			synchronized (scroller) {
				scroller.notify();
			}
			tracker.recycle();
			break;
		}
		}
		return true;
	}

	@Override
	public boolean onTrackballEvent(MotionEvent event) {
		return super.onTouchEvent(event);
	}
	
	private void onStartMapping() {
		int[] coords = Utils.latlon2tilenums(fLat, fLon, iZoom);
		float[] locTL = Utils.tilenums2latlonf(coords[0], coords[1], iZoom);
		float[] locRB = Utils.tilenums2latlonf(coords[0] + 1, coords[1] + 1, iZoom);
		float xShift = (fLat - locTL[0]) / (locRB[0] -  locTL[0]);
		float yShift = (fLon - locTL[1]) / (locRB[1] -  locTL[1]);
		int xOffset = (int) (TILE_SIZE * xShift);
		int yOffset = (int) (TILE_SIZE * yShift);
		iCenterX = coords[0] * TILE_SIZE + xOffset;
		iCenterY = coords[1] * TILE_SIZE + yOffset;		
		iScrX = iCenterX - iScrW2;
		iScrY = iCenterY - iScrH2;
	}
	
	private void onChange(int dx, int dy) {
		Log.v("CloudMadeMapView", "onChange " + dx + " " + dy);
		iScrX += dx;
		iScrY += dy;
		mInvalidateHandler.sendEmptyMessage(0);
	}
	
	private void updateMap() {
		if (bIsActive) {
			this.onStartMapping();
			mCache.setParams(this.iStyle, this.iZoom);			
		}
	}
	
	public int getStyle() {
		return iStyle;
	}

	public void setStyle(int style) {
		this.iStyle = style;
		this.updateMap();
	}

	public int getZoom() {
		return iZoom;
	}

	public void setZoom(int zoom) {
		this.iZoom = zoom;
		this.updateMap();
	}

	public float getLatitude() {
		return fLat;
	}

	private void setLatitude(float latitude) {
		this.fLat = latitude;
	}

	public float getLongitude() {
		return fLon;
	}

	private void setLongitude(float longitude) {
		this.fLon = longitude;
	}
	
	public void setLocation(float latitude, float longitude) {
		this.setLatitude(latitude);
		this.setLongitude(longitude);
	}

}
