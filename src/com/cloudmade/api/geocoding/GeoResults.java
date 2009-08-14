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

import android.os.Parcel;
import android.os.Parcelable;

import com.cloudmade.api.geometry.BBox;
/**
 * Container for array of {@link GeoResult} objects
 * 
 * @author Mykola Paliyenko
 *
 */
@SuppressWarnings("serial")
public class GeoResults implements Serializable, Parcelable {
	/**
	 * Array of {@link GeoResult} objects found
	 */
	public GeoResult[] results;
	/**
	 * Total number of results found. Can be more than results.length
	 */
	public final int found;
	/**
	 *  Bounds of result set
	 */
	public final BBox bounds;
	
	public GeoResults(GeoResult[] results, int found, BBox bounds) {
		super();
		this.results = results;
		this.found = found;
		this.bounds = bounds;
	}
	
	private GeoResults(Parcel in) {
		this.results = (GeoResult[]) in.readArray(null);
		this.found = in.readInt();
		this.bounds = (BBox) in.readSerializable();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeArray(results);
		dest.writeInt(found);
		dest.writeSerializable(bounds);
	}
	
	public static final Parcelable.Creator<GeoResults> CREATOR = new Parcelable.Creator<GeoResults>() {
        public GeoResults createFromParcel(Parcel in) {
            return new GeoResults(in);
        }

        public GeoResults[] newArray(int size) {
            return new GeoResults[size];
        }
    };
	
}
 