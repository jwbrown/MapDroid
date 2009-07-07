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
/**
 * Bounding box object, which contains a pair of points
 * 
 * @author Mykola Paliyenko
 *
 */
public class BBox extends Geometry {
	/**
	 * Pair of points, which correspond to low-left and 
	 * upper-right coordinates respectively
	 */
	public final Point[] points;

	public BBox(Point[] points) {
		this.points = points;
	}

	public BBox(Point p0, Point p1) {
		this.points = new Point[] { p0, p1 };
	}

	@Override
	public String toString() {
		return String.format("%s,%s", points[0], points[1]);
	}
	

}
