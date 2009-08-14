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

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.geocoding.Location;
import com.cloudmade.api.geometry.BBox;
import com.cloudmade.api.geometry.Geometry;
import com.cloudmade.api.geometry.Line;
import com.cloudmade.api.geometry.MultiLine;
import com.cloudmade.api.geometry.MultiPolygon;
import com.cloudmade.api.geometry.Point;
import com.cloudmade.api.geometry.Polygon;
import com.cloudmade.api.routing.Route;
import com.cloudmade.api.routing.RouteInstruction;
import com.cloudmade.api.routing.RouteNotFoundException;
import com.cloudmade.api.routing.RouteSummary;
import com.cloudmade.api.routing.TransitPoint;
import com.cloudmade.api.routing.RouteInstruction.Direction;
import com.cloudmade.api.routing.RouteInstruction.Turn;

/**
 * Utility class for Cloudmade API client
 * @author Mykola Paliyenko
 *
 */

public final class Utils {
	
	
	public static final int getMapSize(int tile_size, int zoom) {
		return (int) Math.round(Math.pow(2, zoom));
	}
	
	
	/**
	 * Convert latitude, longitude pair to tile coordinates
	 * 
	 * @param latitude
	 * @param longitude
	 * @param zoom
	 * @return Tile coordinates [x, y]
	 */
	public static final int[] latlon2tilenums(double latitude,
			double longitude, int zoom) {
		int factor = 1 << (zoom - 1);
		latitude = Math.toRadians(latitude);
		longitude = Math.toRadians(longitude);
		double xtile = 1 + longitude / Math.PI;
		double ytile = 1 - Math.log(Math.tan(latitude) + 
					(1 / Math.cos(latitude))) / Math.PI;
		return new int[] { (int) (xtile * factor), (int) (ytile * factor) };
	}

	/**
	 * Convert tile coordinates pair to latitude, longitude
	 * 
	 * @param xtile
	 * @param ytile
	 * @param zoom
	 * @return Latitude, longitude pair
	 */
	public static final double[] tilenums2latlon(int xtile, int ytile, int zoom) {
		double factor = 1 << zoom;
		double latitude = Math.atan(Math.sinh(Math.PI
				* (1 - 2 * ytile / factor)));
		double longitude = (xtile * 360 / factor) - 180;
		return new double[] { Math.toDegrees(latitude), longitude };
	}
	
	public static final float[] tilenums2latlonf(int xtile, int ytile, int zoom) {
		float factor = 1 << zoom;
		double latitude = Math.atan(Math.sinh(Math.PI
				* (1 - 2 * ytile / factor)));
		float longitude = (xtile * 360 / factor) - 180;
		return new float[] { (float) Math.toDegrees(latitude), longitude };
	}

	static BBox bboxFromJson(JSONArray array) throws JSONException {
		if (array == null) return null;
		return new BBox(pointFromJson(array.getJSONArray(0)), pointFromJson(array.getJSONArray(1)));
	}

	static Point pointFromJson(JSONArray array) throws JSONException {
		if (array == null) return null;
		if (array.length() == 1) array = array.getJSONArray(0); //Fix of the 0.4 version
		return new Point(array.getDouble(0), array.getDouble(1));
	}

	static TransitPoint transitPointFromJson(JSONArray array) throws JSONException {
		if (array == null) return null;
		return new TransitPoint(array.getString(0), array.getDouble(1), array.getDouble(2));
	}

	static GeoResult[] geoResultsFromJson(JSONArray array) throws JSONException {
		if (array == null) return null;
		GeoResult[] results = new GeoResult[array.length()];
		for (int i = 0; i < results.length; i++) {
			results[i] = geoResultFromJson(array.getJSONObject(i));
		}
		return results;
	}
	
	static GeoResult geoResultFromJson(JSONObject obj) throws JSONException {
		GeoResult result = new GeoResult(obj.getInt("id"), 
				geometryFromJson(obj.optJSONObject("geometry")),
				geometryFromJson(obj.getJSONObject("centroid")),
				bboxFromJson(obj.getJSONArray("bounds")),
				mapFromJson(obj.optJSONObject("properties")),
				locationFromJson(obj.optJSONObject("location")));
		return result;
	}
	
	static Geometry geometryFromJson(JSONObject object) throws JSONException {
		if (object == null) return null; 
		String type = object.getString("type");
		JSONArray coords = object.getJSONArray("coordinates");
		if (type.equalsIgnoreCase("point")) {
			return pointFromJson(coords);
		} else if (type.equalsIgnoreCase("linestring")) {
			return new Line(pointsFromJson(coords));
		} else if (type.equalsIgnoreCase("multilinestring")) {
			return new MultiLine(linesFromJson(coords));
		} else if (type.equalsIgnoreCase("polygon")) {
			return polygonFromJson(coords);
		} else if (type.equalsIgnoreCase("multipolygon")) {
			ArrayList<Polygon> polygons = new ArrayList<Polygon>();
			for (int i = 0; i < coords.length(); i++) {
				polygons.add(polygonFromJson(coords.getJSONArray(i)));
			}
			return new MultiPolygon(polygons);
		} 
		throw new IllegalStateException("Unknown geometry type: " + type);
	}

	static Polygon polygonFromJson(JSONArray coords) throws JSONException {
		ArrayList<Line> lines = linesFromJson(coords);
		return new Polygon(lines.remove(0), lines.size() > 0 ? lines : null);
	}

	static ArrayList<Line> linesFromJson(JSONArray coords) throws JSONException {
		ArrayList<Line> lines = new ArrayList<Line>();
		for (int i = 0; i < coords.length(); i++) {
			lines.add(new Line(pointsFromJson(coords.getJSONArray(i))));
		}
		return lines;
	}

	static ArrayList<Point> pointsFromJson(JSONArray coords) throws JSONException {
		if (coords == null) return null;
		ArrayList<Point> points = new ArrayList<Point>();
		for (int i = 0; i < coords.length(); i++) {
			points.add(pointFromJson(coords.getJSONArray(i)));
		}
		return points;
	}
	static ArrayList<TransitPoint> transitPointsFromJson(JSONArray coords) throws JSONException {
		if (coords == null) return null;
		ArrayList<TransitPoint> points = new ArrayList<TransitPoint>();
		for (int i = 0; i < coords.length(); i++) {
			points.add(transitPointFromJson(coords.getJSONArray(i)));
		}
		return points;
	}

	static Location locationFromJson(JSONObject obj) {
		if (obj == null) return null;
		return new Location(obj.optString("country"), obj.optString("county"),
				obj.optString("city"), obj.optString("road"), obj.optString("postcode"));
	}
	@SuppressWarnings("unchecked")
	static Map<String, String> mapFromJson(JSONObject obj) throws JSONException {
		if (obj == null) return null;
		HashMap<String, String> result = new HashMap<String, String>();
		for (Iterator iterator = obj.keys(); iterator.hasNext();) {
			 String key = iterator.next().toString();
			 result.put(key, obj.getString(key));
		}
		return result;
	}

	static Route routeFromJson(JSONObject obj) throws RouteNotFoundException, JSONException {
		if (obj.getInt("status") == 1) {
			throw new RouteNotFoundException(obj.optString("status_message"));
		}
		return new Route(routeInstructionsFromJson(obj.getJSONArray("route_instructions")),
				routeSummaryFromJson(obj.getJSONObject("route_summary")),
				new Line(pointsFromJson(obj.getJSONArray("route_geometry"))),
				obj.getString("version"));
		
	}

	static RouteSummary routeSummaryFromJson(JSONObject obj) throws JSONException {
		return new RouteSummary(obj.getDouble("total_distance"),
				obj.getDouble("total_time"), obj.getString("start_point"),
				obj.getString("end_point"), 
				transitPointsFromJson(obj.optJSONArray("transit_points")));
	}

	static List<RouteInstruction> routeInstructionsFromJson(
			JSONArray array) throws JSONException {
		ArrayList<RouteInstruction> result = new ArrayList<RouteInstruction>();
		for (int i = 0; i < array.length(); i++) {
			JSONArray a = array.getJSONArray(i);
			result.add(new RouteInstruction(a.getString(0), a.getDouble(1), a.getInt(2),
					a.getDouble(3), a.getString(4), Direction.fromName(a.getString(5)),
					a.getDouble(6), Turn.fromName(a.optString(7, null)), a.optDouble(8, 0)));
		}
		return result;
	}
	
	static byte[] getResponseBytes(HttpEntity entity) {
		byte[] result = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			entity.writeTo(baos);
			baos.close();
			result = baos.toByteArray();
		} catch (IOException e) {
			android.util.Log.v("Utils", e.getClass().getSimpleName(), e);
        }
		String out = new String((byte[]) (result != null ? result : "NULL"));
		android.util.Log.v("Utils", "getResponseByte: " + out);
		return result;
	}
	
	static String getResponseString(HttpEntity entity) {
		StringBuilder sb = new StringBuilder(0);
		try {
			InputStream is = entity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
            String line = null;
	        try {
	            while ((line = reader.readLine()) != null) {
	                sb.append(line + "\n");
	            }
	        } catch (IOException e) {
	        	android.util.Log.v("Utils", e.getClass().getSimpleName(), e);
	        } finally {
	            try {
	            	reader.close();
	                is.close();	                
	            } catch (IOException e) {
	            	android.util.Log.v("Utils", e.getClass().getSimpleName(), e);
	            }
	        }
		} catch (IOException e) {
			android.util.Log.v("Utils", e.getClass().getSimpleName(), e);
        }
		android.util.Log.v("Utils", "getResponseString: " + sb.toString());
        return sb.toString();
	}
}
