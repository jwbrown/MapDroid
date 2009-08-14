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

import junit.framework.TestCase;

import android.test.suitebuilder.annotation.MediumTest;

import com.cloudmade.api.CMClient;
import com.cloudmade.api.HTTPError;
import com.cloudmade.api.CMClient.MeasureUnit;
import com.cloudmade.api.CMClient.RouteType;
import com.cloudmade.api.CMClient.RouteTypeModifier;
import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.geocoding.GeoResults;
import com.cloudmade.api.geocoding.ObjectNotFoundException;
import com.cloudmade.api.geometry.Point;
import com.cloudmade.api.routing.Route;
import com.cloudmade.api.routing.RouteNotFoundException;

public class CMClientTest extends TestCase implements TestData {
	
	private CMClient client;

	@Override
	protected void setUp() throws Exception {
		super.setUp();
		client = new CMClient(API_KEY);
	}

	@MediumTest
	public void testFindClosest() throws Exception {
		GeoResult result;
		try {
			result = client.findClosest("parking", new Point(0.1, 51.5));
			fail("ObjectNotFoundException should be raised");
		} catch (ObjectNotFoundException e) {
		}
		result = client.findClosest("road", new Point(51.5, 0.1));
		assertNotNull(result);
	}
	
	@MediumTest
	public void testFind() throws Exception {
		GeoResults results = client.find("Oxford Street, London", 2, 0, null,
				true, true, true);
		assertEquals(2, results.results.length);
		assertTrue(results.found > 0);
		GeoResult r = results.results[0];
		assertTrue(r.bounds != null && r.centroid != null && r.location != null
				&& r.geometry != null && r.properties != null);
		assertTrue(r.location.city.contains("London"));
		results = client.find("Oxford Street, London", 1, 1, null, true, false,
				false);
		r = results.results[0];
		assertTrue(r.location == null && r.geometry == null);

	}

	@MediumTest
	public void testRoute() throws Exception {
		Route route;
		try {
			route = client.route(new Point(0.1, 51.5), new Point(51.5, 0.11),
					RouteType.CAR, null, null, "en", MeasureUnit.KM);
			fail("RouteNotFoundException must be raised");
		} catch (RouteNotFoundException e) {
		}
		route = client.route(new Point(51.5, 0.01), new Point(51.5, 0.001),
				RouteType.FOOT, null, null, "en", MeasureUnit.KM);
		assertEquals("0.3", route.version);
		assertTrue("Too little points in geometry", route.geometry.points
				.size() > 2);
		assertNull(route.summary.transitPoints);
		assertTrue(route.summary.totalDistance > 0
				&& route.summary.totalTime > 0);
		assertTrue(route.instructions.size() > 0);
		route = client.route(new Point(51.5, 0.01), new Point(51.5, 0.001),
				RouteType.CAR, Arrays.asList(new Point[] { new Point(51.5,
						0.002) }), RouteTypeModifier.SHORTEST, "en",
				MeasureUnit.KM);
		assertEquals(1, route.summary.transitPoints.size());
	}

	@MediumTest
	public void testGetTile() throws Exception {
		// Getting tile for London area
		byte[] tile = client.getTile(51.5, 0.1, 9, 1, 256);
		assertNotNull("Tile is null", tile);
		assertTrue("Tile is too small", tile.length > 1000);
		try {
			client.getTile(151.5, 10000.1, 9, 1, 256);
			fail("HTTPError must be raised");
		} catch (HTTPError e) {

		}
	}
}