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
package com.cloudmade.api.geocoding;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import android.os.Parcel;
import android.os.Parcelable;

import com.cloudmade.api.geometry.BBox;
import com.cloudmade.api.geometry.Geometry;
/**
 * Parsed result of geocoding request
 * 
 * @author Mykola Paliyenko
 *
 */
@SuppressWarnings("serial")
public class GeoResult implements Serializable, Parcelable {
	/**
	 * Id of the object
	 */
    public final int id;
    /**
     * Geometry of the object
     */
    public final Geometry geometry;
    /**
     * Centroid of the object
     */
    public final Geometry centroid;
    /**
     * Bounds of object's geometry
     */
    public final BBox bounds;
    /**
     * Location of the object
     */
    public final Location location;
    /**
     * Properties of the object
     */
    public final Map<String, String> properties;
	
    public GeoResult(int id, Geometry geometry, Geometry centroid, BBox bounds, Map<String, String> properties,
			Location location) {
		super();
		this.id = id;
		this.geometry = geometry;
		this.centroid = centroid;
		this.bounds = bounds;
		this.properties = properties;
		this.location = location;
	}
    
    private GeoResult(Parcel in) {
    	this.id = in.readInt();		
		final int size = in.readInt();
		Map<String, String> _properties = new HashMap<String, String>(size);
		for (int i = 0; i < size; i++) {
			_properties.put(in.readString(), in.readString());
		}
		this.properties = _properties;
		this.geometry = (Geometry) in.readSerializable();
		this.centroid = (Geometry) in.readSerializable();
		this.bounds = (BBox) in.readSerializable();
		this.location = (Location) in.readSerializable();
    }

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(this.id);
		Map<String, String> _properties = this.properties;
		final int size = _properties.size();
		dest.writeInt(size);
		Set<String> keys = _properties.keySet();
		for (String key : keys) {
			dest.writeString(key);
			dest.writeString(_properties.get(key));
		}
		dest.writeSerializable(this.geometry);
		dest.writeSerializable(this.centroid);
		dest.writeSerializable(this.bounds);
		dest.writeSerializable(this.location);
	}
	
	public static final Parcelable.Creator<GeoResult> CREATOR = new Parcelable.Creator<GeoResult>() {
        public GeoResult createFromParcel(Parcel in) {
            return new GeoResult(in);
        }

        public GeoResult[] newArray(int size) {
            return new GeoResult[size];
        }
    };

}
