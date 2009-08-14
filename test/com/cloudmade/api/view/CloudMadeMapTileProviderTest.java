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

import java.io.FileNotFoundException;
import java.util.ArrayList;

import com.cloudmade.api.TestData;
import com.cloudmade.api.Utils;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * 
 * @author Alexander Kipar
 *
 */
public class CloudMadeMapTileProviderTest extends AndroidTestCase implements TestData {

	private Handler mResultHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			synchronized (CloudMadeMapTileProviderTest.this.mSync) {				
				CloudMadeMapTileProviderTest.this.syncResult = msg.what;				
				CloudMadeMapTileProviderTest.this.mSync.notify();
			}
		}
		
	};
	
	private CloudMadeMapTileProvider mProvider;
	
	private Object mSync = new Object();
	private int syncResult;
	
	private ArrayList<String> mFileNames = new ArrayList<String>(0);
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
		mProvider = new CloudMadeMapTileProvider(this.getContext(), API_KEY, mResultHandler);
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
		mProvider.close();		
		Context context = this.getContext();
		ArrayList<String> fileNames = mFileNames;
		final int size = fileNames.size();
		for (int i = 0; i < size; i++) {
			context.deleteFile(fileNames.get(i));
		}
		context.deleteDatabase(CloudMadeMapTileDB.TITLE);
	}

	@MediumTest
	public void testGetTile() {
		int[] coord = Utils.latlon2tilenums(52.377538f, 4.895375f, 12);
		syncResult = -1;
//		mProvider.getTile(coord[0], coord[1], 2, 12);
//		synchronized (mSync) {
//			try {
//				mSync.wait();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//		}
//		String fileName = CloudMadeMapTile.createFileName(coord[0], coord[1], 2, 12);
//		mFileNames.add(fileName);
//		assertEquals(CloudMadeMapTileProvider.GET_TILE_SUCCESS, syncResult);
//		Context context = this.getContext();
//		try {			
//			fileName = CloudMadeMapTile.createFileName(coord[0], coord[1], 2, 12);
//			assertNotNull(context.openFileInput(fileName));			
//		} catch (FileNotFoundException e) {
//			fail("Tile bitmap haven't been downloaded");
//		}		
	}
	
	
}
