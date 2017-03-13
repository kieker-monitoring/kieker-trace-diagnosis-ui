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

import kieker.diagnosis.application.service.data.domain.OperationCall;
import kieker.diagnosis.application.service.data.domain.Trace;

import teetime.stage.basic.AbstractTransformation;

/**
 * @author Nils Christian Ehmke
 */
final class OperationCallExtractor extends AbstractTransformation<Trace, OperationCall> {

	@Override
	protected void execute( final Trace aElement ) {
		sendAllCalls( aElement.getRootOperationCall( ) );
	}

	private void sendAllCalls( final OperationCall aCall ) {
		getOutputPort( ).send( aCall );

		aCall.getChildren( ).forEach( child -> sendAllCalls( child ) );
	}

}
