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

import java.util.List;

/**
 * Geometry object that consists of Line geometries and has a closed form
 * 
 * @author Mykola Paliyenko
 *
 */
@SuppressWarnings("serial")
public class Polygon implements Geometry {
	/**
	 * Border line of polygon
	 */
	public final Line border;
	/**
	 * List of lines that make up the holes in polygon
	 */
	public final List<Line> holes;

	public Polygon(Line border, List<Line> holes) {
		this.holes = holes;
		this.border = border;
	}

	public boolean hasHoles() {
		return holes != null && holes.size() > 0;
	}

}
