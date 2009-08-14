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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.geocoding.GeoResults;
import com.cloudmade.api.geocoding.ObjectNotFoundException;
import com.cloudmade.api.geometry.BBox;
import com.cloudmade.api.geometry.Point;
import com.cloudmade.api.routing.Route;
import com.cloudmade.api.routing.RouteNotFoundException;

/**
 * Client to CloudMade's services
 * API Client object is initialized by user credentials
 * (apikey) as well as target (i.e. cloudmade)
 * host, port, etc.
 * 
 * @author Mykola Paliyenko
 *
 */
public class CMClient {
	
	private String apiKey;
	private String host = "cloudmade.com";
	private int port = 80;
	private HttpClient httpClient;

	private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int SOCKET_TIMEOUT = 60 * 1000;
	
	/**
	 * Enum for unit of measure used in routing,
	 * currently only miles and kilometers
	 * 
	 * @author Mykola Paliyenko
	 * 
	 */	
	public enum MeasureUnit {
		/**
		 * Kilometers, meters
		 */
		KM("km"),
		/**
		 * Miles, feets
		 */
		MILES("miles");
		
	    public final String name; 
	    
		MeasureUnit(String name) {
	        this.name = name;
	    }
	    /**
	     * Static constructor that looks up proper enum value from its name
	     * @param name
	     * @return measure unit
	     */
		public static MeasureUnit fromName(String name) {
	    	for (MeasureUnit d : MeasureUnit.values()) {
				if (d.name.equals(name)) {
					return d;
				}
			}
	    	return KM;
	    }
	}
	/**
	 * Enum used to specify type of the route
	 * 
	 * @author Mykola Paliyenko
	 *
	 */
	public enum RouteType {
		CAR("car"),
		BICYCLE("bicycle"),
		FOOT("foot");
		
	    public final String name; 
	    
	    RouteType(String name) {
	        this.name = name;
	    }
	}
	
	/**
	 * Enum used to specify modifier of route type, e.g. SHORTEST for route type CAR.
	 * 
	 * @author Mykola Paliyenko
	 *
	 */
	public enum RouteTypeModifier {
		SHORTEST("shortest");
		
	    public final String name; 
	    
		RouteTypeModifier(String name) {
	        this.name = name;
	    }
	}
	/**
	 * Creates client with given API key that connects to the host cloudmade.com and port 80 
	 * @param apiKey Cloudmade API key used for connection
	 */
	public CMClient(String apiKey) {
		this.apiKey = apiKey;
		this.httpClient = new DefaultHttpClient();
	}

	/**
	 * 
	 * Creates client with given API key, host and port
	 * 
	 * @param apiKey Cloudmade API key used for connection
	 * @param host Host of CloudMade's services
	 * @param port HTTP port to be used for connection
	 */
	public CMClient(String apiKey, String host, int port) {
		this(apiKey);
		this.host = host;
		this.port = port;
	}
	
	public void destroy() {
		httpClient.getConnectionManager().shutdown();  
	}

	/**
	 * Calls a cloudmade API service  
	 * @param uri
	 * @param subdomain
	 * @param params
	 * @return the response byte array
	 */
	private byte[] callService(String uri, String subdomain,
			Map<String, String> params) {
		String domain = subdomain == null ? host : subdomain + "." + host;
		StringBuffer buf = new StringBuffer(0);
		if (params != null) {
			Set<String> keys = params.keySet();
			String value;
			for (String key : keys) {
				value = params.get(key);
				buf.append(key).append('=').append(value).append('&');
			}
			buf.setLength(buf.length() - 1);
		}
		String url;
		if (buf.length() > 0) {
			url = String.format("http://%s:%s/%s%s?%s", domain, port, apiKey,
					uri, buf.toString());
			buf.setLength(0);
		} else {
			url = String.format("http://%s:%s/%s%s", domain, port, apiKey,
					uri);
		}
		HttpGet post = new HttpGet(url);
		HttpParams postParams = post.getParams();
		postParams.setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 
				CONNECTION_TIMEOUT);
		postParams.setIntParameter(HttpConnectionParams.SO_TIMEOUT, SOCKET_TIMEOUT);
		Log.v("CMClient", "callService " + url);
		try {
			HttpResponse response = httpClient.execute(post);
			StatusLine statusLine = response.getStatusLine();
			final int statusCode = statusLine.getStatusCode();
			if (statusCode >= 400) {
				throw new HTTPError("Http error code: " + statusCode +
						" for url " + post.getURI() +
						"; reason: " + statusLine.getReasonPhrase() + 
						".");
			}
			return Utils.getResponseString(response.getEntity()).getBytes();
		} catch (Exception e) {
			throw new HTTPError(e);
		}
	}
	/**
	 * Get tile with given latitude, longitude, zoom, styleId and size
	 * as a bytes of the tile image (PNG file)
	 * 
	 * @param latitude latitude of requested tile
	 * @param longitude longitude of requested tile
	 * @param zoom zoom level, on which tile is being requested (0-18)
	 * @param styleId style id of the requested tile
	 * @param size length of requested tiles' sides (64 or 256)
	 * @return tile image (PNG file) as byte array
	 */
	public byte[] getTile(double latitude, double longitude, int zoom,
			int styleId, int size) {
		int[] tilenums = Utils.latlon2tilenums(latitude, longitude, zoom);
		String uri = String.format("/%s/%s/%s/%s/%s.png", styleId, size, zoom,
				tilenums[0], tilenums[1]);
		return callService(uri, "tile", null);
	}
	
	/**
	 * Find objects that match given query
	 * <p>
	 * Query should be in format 
	 * <code>[POI],[House Number],[Street],[City],[County],[Country]</code> 
	 * (comma separated names of geo objects from small to big) like 
	 * <code>Potsdamer Platz, Berlin, Germany</code>. 
	 * Also supports "near" in queries, e.g. 
	 * <code>hotel near Potsdamer Platz, Berlin, Germany</code>
	 * </p>
	 * @param query Query by which objects should be searched.
	 * @param results How many results should be returned.
	 * @param skip Number of results to skip from beginning.
	 * @param bbox Bounding box in which objects should be searched.
	 * @param bboxOnly If set to <code>false</code>, results will be searched in the
        whole world if there are no results for a given bbox.
	 * @param returnGeometry If set to <code>true</code>, adds geometry in returned
        results. Otherwise only centroid returned. 
	 * @param returnLocation If set to <code>true</code>, adds location information in returned
        results. This might have some negative impact on performance of query, please use it wisely. 
	 * @return {@link GeoResults} object with found results.
	 */
	public GeoResults find(String query, int results, int skip, BBox bbox,
			boolean bboxOnly, boolean returnGeometry, boolean returnLocation) {
		
		byte[] response = {};
		
		try {
			String uri = String.format("/geocoding/find/%s.js", URLEncoder
					.encode(query, "UTF-8"));
			Map<String, String> map = new HashMap<String, String>(0);
			map.put("results", String.valueOf(results));
			map.put("skip", String.valueOf(skip));
			map.put("bbox_only", String.valueOf(bboxOnly));
			map.put("return_geometry", String.valueOf(returnGeometry));
			map.put("return_location", String.valueOf(returnLocation));
			if (bbox != null) {
				map.put("bbox", bbox.toString());
			}			
			response = callService(uri, "geocoding", map);
			map.clear();
			JSONObject obj = new JSONObject(new String(response, "UTF-8"));
			GeoResults result = new GeoResults(Utils.geoResultsFromJson(obj
					.optJSONArray("features")), obj.optInt("found", 0), Utils
					.bboxFromJson(obj.optJSONArray("bounds")));
			return result;
		} catch (JSONException jse) {
			throw new RuntimeException("Error building a JSON object from the " + 
							"geocoding service response:\n" + new String(response,0,500)
							, jse); 
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}
	/**
	 * Find closest object of the given type to a given point.
	 * <p>
	 * For a list of available object types, see: 
	 * <a href="http://www.cloudmade.com/developers/docs/geocoding-http-api/object_types">
	 * http://www.cloudmade.com/developers/docs/geocoding-http-api/object_types
	 * </a>
	 * </p>
	 * @param object_type Type of object, that should be searched
	 * @param point Closes object to this point will be searched.
	 * @return found object
	 * @throws ObjectNotFoundException Thrown when no object could be found in radius of 50 km from point.
	 */
	public GeoResult findClosest(String object_type, Point point) throws ObjectNotFoundException {
		byte[] response = {};
		
		try {
			String uri = String.format("/geocoding/closest/%s/%s.js", URLEncoder
					.encode(object_type, "UTF-8"), point.toString());
			Map<String, String> map = new HashMap<String, String>(0);
			map.put("return_geometry", "true");
			map.put("return_location", "true");
			response = callService(uri, "geocoding", map);
			map.clear();
			String str = new String(response, "UTF-8");
			JSONObject obj = new JSONObject(str);
			if (!obj.has("features")) { 
				throw new ObjectNotFoundException("No object of the secified type could be" +
						" found in radius of 50 km from point");
			}
			GeoResult result = Utils.geoResultFromJson(obj.getJSONArray("features").getJSONObject(0));
			return result;
		} catch (JSONException jse) {
			throw new RuntimeException("Error building a JSON object from the " + 
							"geocoding service response:\n" + new String(response,0,500)
							, jse); 
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}

	}
	/**
	 * Find route between two points with given paramteres.
	 * 
	 * @param start Starting point
	 * @param end Ending point
	 * @param routeType Type of route, e.g. car, foot, etc.
	 * @param transitPoints List of points route must visit before
        reaching end. Points are visited in the same order they are
        specified in the sequence. Set it to <code>null</code> if you do not need it.
	 * @param typeModifier Modifier of the route type, e.g. shortest
	 * @param lang Language code in conformance to `ISO 3166-1 alpha-2` standard
	 * @param units Measure units for distance calculation (km/miles)
	 * @return Route that was found
	 * @throws RouteNotFoundException If no route found between points.
	 */
	public Route route(Point start, Point end, RouteType routeType, List<Point> transitPoints,
			RouteTypeModifier typeModifier, String lang, MeasureUnit units) throws RouteNotFoundException{
		byte[] response = {};
		
		try {
			StringBuffer tps = new StringBuffer("");
			if (transitPoints != null && transitPoints.size() > 0) {
				tps.append("[");
				for (Point transitPoint : transitPoints) {
					tps.append(transitPoint.toString()).append(",");
				}
				tps.replace(tps.length() - 1, tps.length(), "],");
			}
			String tms = "";
			if (typeModifier != null) {
				tms = "/" + typeModifier.name;
			}
			
			String uri = String.format(Locale.US, "/api/0.3/%s,%s%s/%s%s.js", 
					start.toString(), URLEncoder.encode(tps.toString(), "utf-8"), end.toString(),
					routeType.name, tms);
			Map<String, String> map = new HashMap<String, String>(0);
			map.put("lang", lang);
			map.put("units", units.name);
			response = callService(uri, "routes", map);
			map.clear();
			JSONObject obj = new JSONObject(new String(response, "UTF-8"));
			return Utils.routeFromJson(obj);
		} catch (JSONException jse) {
			throw new RuntimeException("Error building a JSON object from the " + 
							"routing service response:\n" + new String(response,0,500)
							, jse); 
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}

}
