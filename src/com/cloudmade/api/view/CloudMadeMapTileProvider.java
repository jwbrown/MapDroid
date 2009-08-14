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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.CompressFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Alexander Kipar
 *
 */
public final class CloudMadeMapTileProvider implements Constants {
	
	public static final int GET_TILE_SUCCESS = 0;
	public static final int GET_TILE_FAIL = 1;
	
	private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int SOCKET_TIMEOUT = 60 * 1000;
	
	private HttpClient mHttpClient;
	
	private final Context mContext;
	private final CloudMadeMapTileDB mDbHelper;
	private final ExecutorService mThreadPool;
	private final Handler mResultHandler;
	
	private String apiKey; 
	
	public CloudMadeMapTileProvider(Context context, String apiKey, Handler cache) {
		mContext = context;
		mDbHelper = new CloudMadeMapTileDB(context);
		mDbHelper.open();
		mHttpClient = new DefaultHttpClient();
		mThreadPool = Executors.newFixedThreadPool(2);
		mResultHandler = cache;
		this.setApiKey(apiKey);
	}
	
	public final void setApiKey(String apiKey) {
		this.apiKey = apiKey;
	}
	
	public final void close() {		
		synchronized (mHttpClient) {
			mHttpClient.getConnectionManager().shutdown();
		}
		synchronized (mDbHelper) {
			mDbHelper.close();
		}		
	}
	
	public final void getTile(int mapX, int mapY, int styleId, int zoom, int x, int y) {		
		mThreadPool.execute(new GetTileTask(mapX, mapY, styleId, zoom, x, y));
	}
	
	private final Bitmap getBitmapFromFile(String fileName) throws FileNotFoundException {
//		Log.v("CloudMadeMapTileProvider", "getBitmapFromFile " + fileName);
		FileInputStream fis = mContext.openFileInput(fileName);
		return BitmapFactory.decodeStream(fis);
	}
	
	private final void saveBitmapToFile(Bitmap bitmap, String fileName) {
		try {	
//			Log.v("CloudMadeMapTileProvider", "saveBitmapToFile " + fileName);
			FileOutputStream fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
			bitmap.compress(CompressFormat.JPEG, 80, fos);
			fos.flush();
			fos.close();
		} catch (FileNotFoundException e) {
			Log.e("CloudMadeMapTileProvider", e.getMessage(), e);
		} catch (IOException e) {
			Log.e("CloudMadeMapTileProvider", e.getMessage(), e);
		}
	}
	
	private final Bitmap getBitmapFromWeb(String uri) throws IOException {
		String url = String.format("http://tile.cloudmade.com:80/%s%s", apiKey,
				uri);
		HttpGet post = new HttpGet(url);
		HttpParams postParams = post.getParams();
		postParams.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
				CONNECTION_TIMEOUT);
		postParams.setIntParameter(HttpConnectionParams.SO_TIMEOUT,
				SOCKET_TIMEOUT);
//		Log.v("CloudMadeMapTileProvider", "getBitmapFromWeb " + url);
		synchronized (mHttpClient) {
			HttpResponse response = mHttpClient.execute(post);
			StatusLine statusLine = response.getStatusLine();
			final int statusCode = statusLine.getStatusCode();
			if (statusCode == 200) {
				InputStream is = response.getEntity().getContent();
				Bitmap tile = BitmapFactory.decodeStream(is);
				is.close();
				return tile;
			} else {
				Log.e("CloudMadeMapTileProvider", "statusCode = " + statusCode
						+ " ; " + statusLine.getReasonPhrase());
				throw new IOException("statusCode = " + statusCode + '\n'
						+ statusLine.getReasonPhrase());
			}
		}		
	}
	
	private final class GetTileTask implements Runnable {

		private int x, y, styleId, zoom, mapX, mapY;
		
		public GetTileTask(int mapX, int mapY, int styleId, int zoom, int x, int y) {
			super();
			this.x = x;
			this.y = y;
			this.styleId = styleId;
			this.zoom = zoom;
			this.mapX = mapX;
			this.mapY = mapY;
		}

		@Override
		public void run() {			
			String uri = CloudMadeMapTile.createUri(mapX, mapY, styleId, zoom);
			String fileName = CloudMadeMapTile.uriToFileName(uri);
			CloudMadeMapTile result;
			synchronized (mDbHelper) {
				result = mDbHelper.getTile(fileName);
				Log.v("CloudMadeMapTileProvider", "getTile x = " + mapX + " y = " + mapY);
				if (result != null) {
					mDbHelper.updateTile(result);
					try {
						result.bitmap = CloudMadeMapTileProvider.this
								.getBitmapFromFile(fileName);
						result.x = x;
						result.y = y;
					} catch (IOException e) {
						Log.e("CloudMadeMapTileProvider", "get tile task fail", e);
						Message msg = Message.obtain(
								CloudMadeMapTileProvider.this.mResultHandler,
								GET_TILE_FAIL);
						msg.sendToTarget();
					}
				} else {
					result = new CloudMadeMapTile();
					try {
						result.bitmap = CloudMadeMapTileProvider.this
								.getBitmapFromWeb(uri);
						result.x = x;
						result.y = y;
						result.mapX = mapX;
						result.mapY = mapY;
						result.style = styleId;
						result.zoom = zoom;
						result.use = 1;
						result.fileName = fileName;
						CloudMadeMapTileProvider.this.saveBitmapToFile(
								result.bitmap, fileName);
						mDbHelper.insertTile(result);
					} catch (IOException e) {
						Log.e("CloudMadeMapTileProvider", "get tile task fail", e);
						Message msg = Message.obtain(
								CloudMadeMapTileProvider.this.mResultHandler,
								GET_TILE_FAIL);
						msg.sendToTarget();
					}
				}
			}
			Log.v("CloudMadeMapTileProvider", "get tile task success");
			Message msg = Message.obtain(
					CloudMadeMapTileProvider.this.mResultHandler,
					GET_TILE_SUCCESS);
			Bundle data = new Bundle();			
			data.putSerializable(KEY_TILE, result);
			msg.setData(data);
			msg.sendToTarget();
		}

	}
	
}
