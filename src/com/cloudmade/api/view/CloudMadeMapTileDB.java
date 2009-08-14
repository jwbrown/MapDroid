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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
/**
 * 
 * @author Alexander Kipar
 *
 */
public class CloudMadeMapTileDB extends SQLiteOpenHelper {
	
	public static final String TITLE = "maptiles.db";
	private static final int VERSION = 1;
	
	public static final String TABLE_TITLE = "map_tiles";
	
	public static final String COL_X = "x";
	public static final String COL_Y = "y";
	public static final String COL_STYLE = "style_id";
	public static final String COL_ZOOM = "zoom";
	public static final String COL_FILENAME = "file_name";
	public static final String COL_USE = "use";
	
	private static final String CREATE_QUERY = "CREATE TABLE " + TABLE_TITLE + " ( " +
			"x INTEGER NOT NULL, y INTEGER NOT NULL, " +
			"style_id INTEGER NOT NULL, zoom INTEGER NOT NULL, " +
			"file_name TEXT NOT NULL, use INTEGER NOT NULL" +
			" );";
	
	private static final String FIND_QUERY = CloudMadeMapTileDB.COL_FILENAME 
		+ " = ? ";
	
	private SQLiteDatabase mDb;

	public CloudMadeMapTileDB(Context context) {
		super(context, TITLE, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_QUERY);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS *;");
		this.onCreate(db);
	}
	
	public synchronized void open() {
		mDb = this.getReadableDatabase();
	}
	
	public synchronized void close() {
		if (mDb != null) {
			mDb.close();
		}
	}
	
	public synchronized CloudMadeMapTile getTile(String fileName) {
		String[] find = {fileName};
		Cursor c = this.mDb.query(TABLE_TITLE, null, FIND_QUERY,
				find, null, null, null);
		CloudMadeMapTile result = null;
		if (c.moveToFirst()) {
			result = new CloudMadeMapTile();
			result.fileName = fileName;
			result.mapX = c.getInt(c.getColumnIndex(COL_X));
			result.mapY = c.getInt(c.getColumnIndex(COL_Y));
			result.style = c.getInt(c.getColumnIndex(COL_STYLE));
			result.zoom = c.getInt(c.getColumnIndex(COL_ZOOM));
			result.use = c.getInt(c.getColumnIndex(COL_USE));
		}
		c.close();
		return result;
	}
	
	public synchronized void insertTile(CloudMadeMapTile tile) {
		ContentValues values = new ContentValues();
		values.put(COL_X, tile.mapX);
		values.put(COL_Y, tile.mapY);
		values.put(COL_STYLE, tile.style);
		values.put(COL_ZOOM, tile.zoom);
		values.put(COL_USE, 1);
		values.put(COL_FILENAME, tile.fileName);
		mDb.insert(TABLE_TITLE, null, values);
	}
	
	public synchronized void updateTile(CloudMadeMapTile tile) {
		String[] find = {tile.fileName};
		Cursor c = this.mDb.query(TABLE_TITLE, null, FIND_QUERY,
				find, null, null, null);
		if (c.moveToFirst()) {
			ContentValues values = new ContentValues();
			values.put(COL_X, tile.mapX);
			values.put(COL_Y, tile.mapY);
			values.put(COL_STYLE, tile.style);
			values.put(COL_ZOOM, tile.zoom);
			values.put(COL_USE, c.getInt(c.getColumnIndex(COL_USE)) + 1);
			values.put(COL_FILENAME, tile.fileName);
			c.close();
			mDb.update(TABLE_TITLE, values, FIND_QUERY, find);
		}		
			
	}

}
