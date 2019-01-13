/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.frontend.tab.traces.aggregator;

import java.util.List;

import kieker.diagnosis.backend.data.MethodCall;

/**
 * This aggregator is a pseudo aggregator which does not do anything. It is basically the identity function on method
 * call aggregation.
 *
 * @author Nils Christian Ehmke
 */
public final class IdentityAggregator extends Aggregator {

	@Override
	public List<MethodCall> aggregate( final List<MethodCall> aCalls ) {
		return aCalls;
	}

}
