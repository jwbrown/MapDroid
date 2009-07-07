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
package com.cloudmade.api.geometry;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Simple container of latitude, longitude pair
 * 
 * @author Mykola Paliyenko
 *
 */
public class Point extends Geometry{
	/**
	 * latitude of the point
	 */
	public final float lat;
	/**
	 * longitude of the point
	 */
	public final float lon;
	
	public Point(double lat, double lon) {
		this.lat = (float) lat;
		this.lon = (float) lon;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Point) {
			Point p = (Point) obj;
			return p.lat == lat && p.lon == lon;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return new Double(lat).hashCode() * 37 + new Double(lon).hashCode();
	}
	
	public float[] coords(){
		return new float[]{lat, lon};
	}
	
	@Override
	public String toString() {
		DecimalFormat formatter = new DecimalFormat("0.#####", 
				new DecimalFormatSymbols(Locale.US));
		return formatter.format(lat) + "," + formatter.format(lon);
	}
}
