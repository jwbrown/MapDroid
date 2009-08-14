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

/**
 * Instructions on route passing
 * 
 * @author Mykola Paliyenko
 *
 */
@SuppressWarnings("serial")
public class RouteInstruction implements Serializable {

	/**
	 * Text instruction
	 */
	public final String instruction;
	/**
	 * Length of the segment in meters
	 */
	public final double length; 
	/**
	 * Index of the first point of the segment in route geometry
	 */
	public final int position;
	/**
	 * Estimated time required to travel the segment in seconds
	 */
	public final double time;
	/**
	 * Length of the segments in specified units
	 */
	public final String length_caption;
	/**
	 * Earth direction
	 */
	public final Direction earth_direction;
	/**
	 * North-based azimuth
	 */
	public final double azimuth;
	/**
	 * Turn type (null for first segment in route)
	 */
	public final Turn turn_type;
	/**
	 * Angle in degress of the turn between two segments
	 */
	public final double turn_angle;

	/**
	 * Earth directions
	 * 
	 * @author Mykola Paliyenko
	 *
	 */
	public enum Direction {
		NORTH(new double[]{360 - 22.5, 360, 0, 22.5}, "N"),
		NORTH_WEST(new double[]{45 - 22.5, 45 + 22.5}, "NE"),
		WEST(new double[]{90 - 22.5, 90 + 22.5}, "E"),
		SOUTH_WEST(new double[]{135 - 22.5, 135 + 22.5}, "SE"),
		SOUTH(new double[]{180 - 22.5, 180 + 22.5}, "S"),
		SOUTH_EAST(new double[]{225 - 22.5, 225 + 22.5}, "SW"),
		EAST(new double[]{270 - 22.5, 270 + 22.5}, "W"),
		NORTH_EAST(new double[]{315 - 22.5, 315 + 22.5}, "NW");
		
		public final double[] azimuth_range;
	    public final String name; 
	    
	    Direction(double[] azimuth_range, String name) {
	        this.azimuth_range = azimuth_range;
	        this.name = name;
	    }
	    public static Direction fromName(String name) {
	    	for (Direction d : Direction.values()) {
				if (d.name.equals(name)) {
					return d;
				}
			}
	    	return NORTH;
	    }
	    

	}
	/**
	 * Turn types
	 * 
	 * @author Mykola Paliyenko
	 *
	 */
	public enum Turn {
		CONTINUE(new double[]{360 - 22.5, 360, 0, 0 + 22.5}, "C"),
		TURN_SLIGHT_RIGHT(new double[]{22.5, 50}, "TSLR"),
		TURN_RIGHT(new double[]{50, 180 - 50}, "TR"),
		TURN_SHARP_RIGHT(new double[]{180 - 50, 180 - 10}, "TSHR"),
		U_TURN(new double[]{180 - 10, 180 + 10}, "TU"),
		TURN_SHARP_LEFT(new double[]{180 + 10, 180 + 50}, "TSHL"),
		TURN_LEFT(new double[]{180 + 50, 360 - 50}, "TL"),
		TURN_SLIGHT_LEFT(new double[]{360 - 50, 360 - 22.5}, "TSLL");

		public final double[] angles;
	    public final String name; 
	    
	    Turn(double[] angles, String name) {
	        this.angles = angles;
	        this.name = name;
	    }
	    public static Turn fromName(String name) {
	    	if (name == null) return null; 
	    	for (Turn d : Turn.values()) {
				if (d.name.equals(name)) {
					return d;
				}
			}
	    	return CONTINUE;
	    }
	}

	public RouteInstruction(
			String instruction,
			double length,
			int position,
			double time,
			String length_caption,
			Direction earth_direction,
			double azimuth,
			Turn turn_type,
			double turn_angle) {

		this.instruction = instruction;
		this.length = length;
		this.position = position;
		this.time = time;
		this.length_caption = length_caption;
		this.earth_direction = earth_direction;
		this.azimuth = azimuth;
		this.turn_type = turn_type;
		this.turn_angle = turn_angle;
	}

	

}
