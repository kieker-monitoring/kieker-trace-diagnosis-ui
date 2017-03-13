/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.service.data.stages;

import java.util.function.Predicate;

import teetime.stage.basic.AbstractTransformation;

/**
 * This stage filters incoming objects and forwards only those which meet the given predicate.
 *
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The precise type of the incoming and outgoing object.
 */
final class Filter<T> extends AbstractTransformation<T, T> {

	private final Predicate<T> ivPredicate;

	public Filter( final Predicate<T> aPredicate ) {
		ivPredicate = aPredicate;
	}

	@Override
	protected void execute( final T aElement ) {
		if ( ivPredicate.test( aElement ) ) {
			getOutputPort( ).send( aElement );
		}
	}

}
