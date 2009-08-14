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
package com.cloudmade.api;

import junit.framework.TestSuite;
import android.test.InstrumentationTestRunner;
import android.test.InstrumentationTestSuite;

/**
 * 
 * @author Alexander Kipar
 *
 */
public class CloudmadeTestRunner extends InstrumentationTestRunner {

	@Override
	public TestSuite getAllTests() {
		InstrumentationTestSuite suite = new InstrumentationTestSuite(this);
		suite.addTestSuite(CloudmadeApiTestSuite.class);
		return suite;
	}

	@Override
	public ClassLoader getLoader() {
		return InstrumentationTestRunner.class.getClassLoader();
	}

}
