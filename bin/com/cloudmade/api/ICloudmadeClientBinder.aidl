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

import com.cloudmade.api.geocoding.GeoResults;
import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.routing.Route;
import com.cloudmade.api.geometry.BBox;
import com.cloudmade.api.geometry.Point;

/**
 * 
 * @author Alexander Kipar
 *
 */
interface ICloudmadeClientBinder {

	void setApiKey(in String apiKey);
	
	void setHost(in String host, int port);

	byte[] getTile(double latitude, double longitude, int zoom,
			int styleId, int size);
			
	GeoResults find(String query, int results, int skip, in BBox bbox,
			boolean bboxOnly, boolean returnGeometry, boolean returnLocation);

	GeoResult findClosest(in String object_type, in Point point);

	Route route(in Point start, in Point end, in String routeType, in List<Point> transitPoints,
			in String typeModifier, in String lang, in String units);

}