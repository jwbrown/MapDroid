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

/**
 * Location of the object in geographical terms
 * 
 * @author Mykola Paliyenko
 *
 */

@SuppressWarnings("serial")
public class Location implements Serializable {
	/**
	 * Country in which the object is situated
	 */
	public final String country;
	/**
	 * County, where the object is situated
	 */
	public final String county;
	/**
	 * City, where the object is situated
	 */
	public final String city;
	/**
	 * Road on which object is situated
	 */
	public final String road;
	/**
	 * Postcode, which corresponds to location of the object
	 */
	public final String postcode;

	public Location(String country, String county, String city, String road,
			String postcode) {
		this.country = country;
		this.county = county;
		this.city = city;
		this.road = road;
		this.postcode = postcode;
	}
	
	
}
