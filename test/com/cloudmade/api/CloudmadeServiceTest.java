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
package com.cloudmade.api;

import java.util.Arrays;

import com.cloudmade.api.CMClient.MeasureUnit;
import com.cloudmade.api.CMClient.RouteType;
import com.cloudmade.api.CMClient.RouteTypeModifier;
import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.geocoding.GeoResults;
import com.cloudmade.api.geometry.Point;
import com.cloudmade.api.routing.Route;

import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.LargeTest;
import android.test.suitebuilder.annotation.MediumTest;
import android.test.suitebuilder.annotation.SmallTest;


/**
 * 
 * @author Alexander Kipar
 *
 */
public final class CloudmadeServiceTest extends ServiceTestCase<CloudMadeClient> 
				implements TestData {

	public CloudmadeServiceTest() {
		super(CloudMadeClient.class);
	}
	
	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@Override
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	@SmallTest
    public void testStartable() {
        Intent startIntent = new Intent();
        startIntent.setAction(CloudMadeClient.ACTION);
        startService(startIntent); 
    }
	
	@MediumTest
    public void testBindable() {
        Intent startIntent = new Intent();
        startIntent.setAction(CloudMadeClient.ACTION);
        IBinder service = bindService(startIntent);
        assertNotNull(service);
        ICloudmadeClientBinder binder = (ICloudmadeClientBinder) service;
        assertNotNull(binder);
    }
	
	@LargeTest
	public void testGetTile() {
		Intent startIntent = new Intent();
        startIntent.setAction(CloudMadeClient.ACTION);
        ICloudmadeClientBinder binder = (ICloudmadeClientBinder) bindService(startIntent);
        try {
			binder.setApiKey(API_KEY);
		} catch (RemoteException e) {
			fail("setApiKey RemoteException " + e.getMessage());
		}
        byte[] tile = null;
		try {
			tile = binder.getTile(51.5, 0.1, 9, 1, 256);
		} catch (RemoteException e) {
			fail("getTile RemoteException " + e.getMessage());
		}
		assertNotNull("Tile is null", tile);
		assertTrue("Tile is too small", tile.length > 1000);
		try {
			binder.getTile(151.5, 10000.1, 9, 1, 256);
			fail("HTTPError must be raised");
		} catch (HTTPError e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			fail("getTile RemoteException " + e.getMessage());
		}
	}
	
	@LargeTest
	public void testFind() {
		Intent startIntent = new Intent();
        startIntent.setAction(CloudMadeClient.ACTION);
        ICloudmadeClientBinder binder = (ICloudmadeClientBinder) bindService(startIntent);
        try {
			binder.setApiKey(API_KEY);
		} catch (RemoteException e) {
			fail("setApiKey RemoteException " + e.getMessage());
		}
		GeoResults results = null;
		try {
			results = binder.find("Oxford Street, London", 2, 0, null,
					true, true, true);
		} catch (RemoteException e) {
			fail("find RemoteException " + e.getMessage());
		}
		assertEquals(2, results.results.length);
		assertTrue(results.found > 0);
		GeoResult r = results.results[0];
		assertTrue(r.bounds != null && r.centroid != null && r.location != null
				&& r.geometry != null && r.properties != null);
		assertTrue(r.location.city.contains("London"));
		try {
			results = binder.find("Oxford Street, London", 1, 1, null, true, false,
					false);
		} catch (RemoteException e) {
			fail("find RemoteException " + e.getMessage());
		}
		r = results.results[0];
		assertTrue(r.location == null && r.geometry == null);
	}
	
	@LargeTest
	public void testFindCloset() {
		Intent startIntent = new Intent();
        startIntent.setAction(CloudMadeClient.ACTION);
        ICloudmadeClientBinder binder = (ICloudmadeClientBinder) bindService(startIntent);
        try {
			binder.setApiKey(API_KEY);
		} catch (RemoteException e) {
			fail("Set api key RemoteException " + e.getMessage());
		}
		GeoResult result;
		try {
			result = binder.findClosest("parking", new Point(0.1, 51.5));
			assertNull(result);
		} catch (RemoteException e) {
			fail("findClosest RemoteException " + e.getMessage());
		}		
		try {
			result = binder.findClosest("road", new Point(51.5, 0.1));
			assertNotNull(result);
		} catch (RemoteException e) {
			fail("findClosest RemoteException " + e.getMessage());
		}		
	}
	
	@LargeTest
	public void testRoute(){
		Intent startIntent = new Intent();
        startIntent.setAction(CloudMadeClient.ACTION);
        ICloudmadeClientBinder binder = (ICloudmadeClientBinder) bindService(startIntent);
        try {
			binder.setApiKey(API_KEY);
		} catch (RemoteException e) {
			fail("Set api key RemoteException " + e.getMessage());
		}
		Route route;
		try {
			route = binder.route(new Point(0.1, 51.5), new Point(51.5, 0.11),
					RouteType.CAR.name, null, null, "en", MeasureUnit.KM.name);
			assertNull(route);
		} catch (RemoteException e) {
			fail("route RemoteException " + e.getMessage());
		}
		try {
			route = binder.route(new Point(51.5, 0.01), new Point(51.5, 0.001),
					RouteType.FOOT.name, null, null, "en", MeasureUnit.KM.name);
			assertEquals("0.3", route.version);
			assertTrue("Too little points in geometry", route.geometry.points
					.size() > 2);
			assertNull(route.summary.transitPoints);
			assertTrue(route.summary.totalDistance > 0
					&& route.summary.totalTime > 0);
			assertTrue(route.instructions.size() > 0);
		} catch (RemoteException e) {
			fail("route RemoteException " + e.getMessage());;
		}		
		try {
			route = binder.route(new Point(51.5, 0.01), new Point(51.5, 0.001),
					RouteType.CAR.name, Arrays.asList(new Point[] { new Point(51.5,
							0.002) }), RouteTypeModifier.SHORTEST.name, "en",
					MeasureUnit.KM.name);
			assertEquals(1, route.summary.transitPoints.size());
		} catch (RemoteException e) {
			fail("route RemoteException " + e.getMessage());
		}		
	}

}
