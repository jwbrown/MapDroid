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
import java.util.List;

import com.cloudmade.api.geometry.Line;

/**
 * Result of the CloudMade's routing service query
 * 
 * @author Mykola Paliyenko
 *
 */
public class Route implements Serializable {
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
	
}
