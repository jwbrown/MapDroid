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

import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * 
 * @author Alexander Kipar
 *
 */
public final class CloudMadeMapTileCache extends Handler implements Constants {
	
	public static final int MSG_UPDATE = 12;
	
	private int sizeMax = Integer.MAX_VALUE;
	private int style;
	private int zoom;
	private int mapSize;

	public Bitmap nullBitmap;
	
	private HashMap<Integer, CloudMadeMapTile> mTileHash;
	
	private Handler mUpdateHandler;
	
	private CloudMadeMapTileProvider mProvider;
	private CloudMadeMapTile[] mMap = new CloudMadeMapTile[9];
	private final int[] mMapRect = new int[4];
	
	private int leftX, topY;
	
	public CloudMadeMapTileCache(Context context, String apiKey, Handler updateHandler) {
		mProvider = new CloudMadeMapTileProvider(context, apiKey, this);
		mTileHash = new HashMap<Integer, CloudMadeMapTile>(0);
		mUpdateHandler = updateHandler;
	}
	
	@Override
	public void handleMessage(Message msg) {
		switch (msg.what) {
		case CloudMadeMapTileProvider.GET_TILE_SUCCESS:
			CloudMadeMapTile tile = (CloudMadeMapTile) msg.getData().getSerializable(KEY_TILE);
			int index = tile.x - leftX + (tile.y - topY) * 3; 
			Log.v("CloudMadeMapTileCache", "GET_TILE_SUCCESS x " + tile.x + " y " + tile.y + " i " + index);
			if (index >= 0 && index < 9) {
				mMap[index] = tile;
			}
			this.addTile(tile);
			mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
			break;
		case CloudMadeMapTileProvider.GET_TILE_FAIL:
			Log.v("CloudMadeMapTileCache", "GET_TILE_FAIL");
			break;
		}
	}
	
	public void setSizeMax(int size) {
		sizeMax = size;
	}
	
	public void setParams(int style, int zoom) {
		this.style = style;
		this.zoom = zoom;
		this.mapSize = (int) Math.pow(2, zoom);
		this.clear();
	}
	
	public void clear() {
		mTileHash.clear();
		mUpdateHandler.sendEmptyMessage(MSG_UPDATE);
	}
	
	public void addTile(CloudMadeMapTile tile) {		
		HashMap<Integer, CloudMadeMapTile> tileHash = mTileHash;
		final int key = tile.mapY * this.mapSize + tile.mapX;
		if (tileHash.containsKey(key)) {
			tileHash.remove(key);
		}
		tileHash.put(key, tile);
		final int size = tileHash.size();
		if (size >= sizeMax) {
			tileHash.remove(size - 1);
		}
	}
	
	public CloudMadeMapTile[] getMap(int viewX, int viewY, int viewW, int viewH) {
		if (!isPointInRect(new int[] {viewX, viewY}, mMapRect) ||
				!isPointInRect(new int[] {viewX + viewW, viewY + viewH}, mMapRect)) {
			CloudMadeMapTile[] result = new CloudMadeMapTile[9];
			leftX = viewX / TILE_SIZE;
			topY = viewY / TILE_SIZE;
			if (viewX < 0) {
				leftX--;
			}
			if (topY < 0) {
				topY--;
			}
			HashMap<Integer, CloudMadeMapTile> tileHash = mTileHash;
			for (int i = 0; i < 9; i++) {
				int x = leftX + i % 3;
				int y = topY + i / 3;		
				int mapX = x, mapY = y;
				if (mapX < 0) {
					mapX += mapSize;
				} else if (mapX >= mapSize) {
					mapX -= mapSize;
				}
				if (mapY < 0) {
					mapY += mapSize;
				} else if (mapY >= mapSize) {
					mapY -= mapSize;
				}
				Log.v("CloudMadeMapTileCache", "getTile x = " + x + "y = " + y +
						" real x = " + mapX + " real y = " + mapY);
				int key = mapY * mapSize + mapX;
				if (tileHash.containsKey(key)) {
					result[i] = tileHash.get(key);
					result[i].x = x;
					result[i].y = y;
				} else {					
					mProvider.getTile(mapX, mapY, style, zoom, x, y);
				}
			}
			mMap = result;
			mMapRect[0] = leftX * TILE_SIZE;
			mMapRect[1] = topY * TILE_SIZE;
			mMapRect[2] = TILE_MAP_W;
			mMapRect[3] = TILE_MAP_W;
		} else {
			Log.v("CloudMadeMapTileCache", "re");
		}
		return mMap;
	}
	
	private final boolean isPointInRect(int[] coord, int[] rect) {
		boolean result = true;
		if (coord[0] < rect[0] || coord[0] > rect[0] + rect[2] ||
				coord[1] < rect[1] || coord[1] > rect[1] + rect[3]) {
			result = false;
		}
		return result;
	}
	
}
