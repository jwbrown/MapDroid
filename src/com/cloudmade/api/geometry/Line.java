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
 * Geometry object that consists of Point geometries
 * 
 * @author Mykola Paliyenko
 *
 */
@SuppressWarnings("serial")
public class Line implements Geometry {
	/**
	 * List of points, that make up the line
	 */
	public final List<Point> points;

	public Line(List<Point> points) {
		this.points = points;
	}
	
}
