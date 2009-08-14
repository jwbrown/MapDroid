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
package com.cloudmade.api.routing;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.os.Parcelable;

import com.cloudmade.api.geometry.Line;

/**
 * Result of the CloudMade's routing service query
 * 
 * @author Mykola Paliyenko
 *
 */
@SuppressWarnings("serial")
public class Route implements Serializable, Parcelable {
	/**
	 * List of instructions
	 */
	public final List<RouteInstruction> instructions;
	/**
	 * Statistical info about the route
	 */
	public final RouteSummary summary;
	/**
	 * Geometry of route
	 */
	public final Line geometry;
	/**
	 * Version of routing HTTP API
	 */
	public final String version;

	public Route(List<RouteInstruction> instructions, RouteSummary summary,
			Line geometry, String version) {
		this.instructions = instructions;
		this.summary = summary;
		this.geometry = geometry;
		this.version = version;
	}
	
	private Route(Parcel in) {
		final int size = in.readInt();
		List<RouteInstruction> _instructions = new ArrayList<RouteInstruction>(size);
		for (int i = 0; i < size; i++) {
			_instructions.add((RouteInstruction) in.readSerializable());
		}
		this.instructions = _instructions;
		this.summary = (RouteSummary) in.readSerializable();
		this.geometry = (Line) in.readSerializable();
		this.version = in.readString();
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		List<RouteInstruction> _instructions = this.instructions;
		final int size = _instructions.size();
		for (int i = 0; i < size; i++) {
			dest.writeSerializable(_instructions.get(i));
		}
		dest.writeSerializable(this.summary);
		dest.writeSerializable(this.geometry);
		dest.writeString(this.version);
	}
	
	public static final Parcelable.Creator<Route> CREATOR = new Parcelable.Creator<Route>() {
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        public Route[] newArray(int size) {
            return new Route[size];
        }
    };
	
}
