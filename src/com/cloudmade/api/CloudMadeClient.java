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

import com.cloudmade.api.geocoding.GeoResult;
import com.cloudmade.api.geocoding.GeoResults;
import com.cloudmade.api.geometry.BBox;
import com.cloudmade.api.geometry.Point;
import com.cloudmade.api.routing.Route;
import com.cloudmade.api.routing.RouteNotFoundException;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

/**
 * 
 * @author Alexander Kipar
 *
 */
public final class CloudMadeClient extends Service {
	
	public static final String ACTION = "com.cloudmade.api.CLOUDMADE";
	
	private String apiKey;
	private String host = "cloudmade.com";
	private int port = 80;
	
	private HttpClient httpClient;

	private static final int CONNECTION_TIMEOUT = 30 * 1000;
    private static final int SOCKET_TIMEOUT = 60 * 1000;
    	
	@Override
	public void onCreate() {
		super.onCreate();
		httpClient = new DefaultHttpClient();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		httpClient.getConnectionManager().shutdown();
	}

	@Override
	public IBinder onBind(Intent intent) {
		return mBinder;
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
		Log.v("CloudmadeClient", "callService " + url);
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
	
	private final ICloudmadeClientBinder.Stub mBinder = new ICloudmadeClientBinder.Stub() {
		
		@Override
		public final byte[] getTile(double latitude, double longitude, int zoom,
				int styleId, int size) throws RemoteException {
			int[] tilenums = Utils.latlon2tilenums(latitude, longitude, zoom);
			String uri = String.format("/%s/%s/%s/%s/%s.png", styleId, size, zoom,
					tilenums[0], tilenums[1]);
			return callService(uri, "tile", null);
		}
		
		@Override
		public final GeoResults find(String query, int results, int skip, BBox bbox,
				boolean bboxOnly, boolean returnGeometry, boolean returnLocation)
				throws RemoteException {
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
				GeoResults result = new GeoResults(
						Utils.geoResultsFromJson(obj.optJSONArray("features")),
						obj.optInt("found", 0), 
						Utils.bboxFromJson(obj.optJSONArray("bounds")));
				return result;
			} catch (JSONException jse) {
				throw new RuntimeException("Error building a JSON object from the " + 
								"geocoding service response:\n" + new String(response, 0, 00)
								, jse); 
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}

		@Override
		public final void setApiKey(final String apiKey) throws RemoteException {
			CloudMadeClient.this.apiKey = apiKey;	
		}

		@Override
		public final void setHost(String host, int port) throws RemoteException {
			CloudMadeClient.this.host = host;
			CloudMadeClient.this.port = port;
		}

		@Override
		public GeoResult findClosest(String objectType, Point point)
				throws RemoteException {
			byte[] response = {};
			
			try {
				String uri = String.format("/geocoding/closest/%s/%s.js", URLEncoder
						.encode(objectType, "UTF-8"), point.toString());
				Map<String, String> map = new HashMap<String, String>(0);
				map.put("return_geometry", "true");
				map.put("return_location", "true");
				response = callService(uri, "geocoding", map);
				map.clear();
				String str = new String(response, "UTF-8");
				JSONObject obj = new JSONObject(str);
				if (!obj.has("features")) { 
					return null;
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

		@Override
		public Route route(Point start, Point end, String routeType,
				List<Point> transitPoints, String typeModifier, String lang,
				String units) throws RemoteException {
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
					tms = "/" + typeModifier;
				}
				
				String uri = String.format(Locale.US, "/api/0.3/%s,%s%s/%s%s.js", 
						start.toString(), URLEncoder.encode(tps.toString(), "utf-8"), end.toString(),
						routeType, tms);
				Map<String, String> map = new HashMap<String, String>(0);
				map.put("lang", lang);
				map.put("units", units);
				response = callService(uri, "routes", map);
				map.clear();
				JSONObject obj = new JSONObject(new String(response, "UTF-8"));
				try {
					return Utils.routeFromJson(obj);
				} catch (RouteNotFoundException e) {
					return null;
				}
			} catch (JSONException jse) {
				throw new RuntimeException("Error building a JSON object from the " + 
								"routing service response:\n" + new String(response,0,500)
								, jse); 
			} catch (UnsupportedEncodingException e) {
				throw new RuntimeException(e);
			}
		}
		
	};
}
