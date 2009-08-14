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
package com.cloudmade.api.view;

public interface Constants {

	public static final int TILE_SIZE = 256;
	
	public static final int TILE_MAP_SIZE = 3;
	public static final int TILE_MAP_LEN = TILE_MAP_SIZE * TILE_MAP_SIZE;
	
	public static final int TILE_MAP_W = TILE_SIZE * TILE_MAP_SIZE;
	
	public static final int ZOOM_MAX = 18;
	
	public static final String KEY_TILE = "tile";
	
}
