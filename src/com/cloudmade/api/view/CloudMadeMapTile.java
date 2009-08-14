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

import java.io.Serializable;

import android.graphics.Bitmap;
/**
 * 
 * @author Alexander Kipar
 *
 */
public final class CloudMadeMapTile implements Constants, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1444190678536194769L;
	
	
	public int x;
	public int y;
	
	public int mapX;
	public int mapY;

	public int zoom;
	public int style;

	public int use;
	
	public Bitmap bitmap;
	
	protected String fileName;
	
	protected CloudMadeMapTile() {
		
	}

	public CloudMadeMapTile(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public CloudMadeMapTile(int x, int y, Bitmap bitmap) {
		this.x = x;
		this.y = y;
		this.bitmap = bitmap;
	}
	
	private static final String FILE_FORMAT = "_%s_%s_%s_%s_%s.png";
	private static final String URI_FORMAT = "/%s/%s/%s/%s/%s.png";
	
	public static final String uriToFileName(String uri) {
		return uri.replace('/', '_');
	}
	
	public static final String createFileName(int x, int y, int styleId, int zoom) {
		return String.format(FILE_FORMAT, styleId, TILE_SIZE, zoom, x, y);
	}
	
	public static final String createUri(int x, int y, int styleId, int zoom) {
		return String.format(URI_FORMAT, styleId, TILE_SIZE, zoom, x, y);
	}
	
}
