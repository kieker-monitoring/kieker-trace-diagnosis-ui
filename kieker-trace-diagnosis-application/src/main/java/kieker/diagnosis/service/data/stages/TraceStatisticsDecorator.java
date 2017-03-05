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

package kieker.diagnosis.service.data.stages;

import kieker.diagnosis.service.ServiceUtil;
import kieker.diagnosis.service.data.domain.OperationCall;
import kieker.diagnosis.service.data.domain.Trace;
import kieker.diagnosis.service.properties.PercentCalculationProperty;
import kieker.diagnosis.service.properties.PropertiesService;
import teetime.stage.basic.AbstractTransformation;

/**
 * This class is a {@code TeeTime} stage adding statistics (via the corresponding setters) to instances of {@link Trace}. The traces are forwarded to the output
 * port.
 *
 * @author Nils Christian Ehmke
 */
public final class TraceStatisticsDecorator extends AbstractTransformation<Trace, Trace> {

	private final PropertiesService ivPropertiesService = ServiceUtil.getService( PropertiesService.class );

	@Override
	public void execute( final Trace aTrace ) {
		addTraceDepth( aTrace.getRootOperationCall( ) );
		addTraceSize( aTrace.getRootOperationCall( ) );
		addPercentValues( aTrace.getRootOperationCall( ), aTrace.getRootOperationCall( ).getDuration( ), aTrace.getRootOperationCall( ).getDuration( ) );

		super.getOutputPort( ).send( aTrace );
	}

	private int addTraceDepth( final OperationCall aCall ) {
		final int ivTraceDepth;

		if ( aCall.getChildren( ).isEmpty( ) ) {
			ivTraceDepth = 0;
		} else {
			int maxTraceDepthOfChildren = 0;

			for ( final OperationCall child : aCall.getChildren( ) ) {
				final int traceDepthOfChild = addTraceDepth( child );
				maxTraceDepthOfChildren = Math.max( traceDepthOfChild, maxTraceDepthOfChildren );
			}

			ivTraceDepth = 1 + maxTraceDepthOfChildren;
		}

		aCall.setStackDepth( ivTraceDepth );
		return ivTraceDepth;
	}

	private int addTraceSize( final OperationCall aCall ) {
		int traceSize = 1;

		for ( final OperationCall child : aCall.getChildren( ) ) {
			final int traceSizeOfChild = addTraceSize( child );
			traceSize += traceSizeOfChild;
		}

		aCall.setStackSize( traceSize );
		return traceSize;
	}

	private void addPercentValues( final OperationCall aCall, final long aParentDuration, final long aRootDuration ) {
		if ( aCall.getParent( ) == null ) {
			aCall.setPercent( 100.0f );
		} else {
			final boolean percentCalculationsRefersToTopMost = ivPropertiesService.loadPrimitiveProperty( PercentCalculationProperty.class );
			if ( percentCalculationsRefersToTopMost ) {
				aCall.setPercent( ( 100.0f * aCall.getDuration( ) ) / aRootDuration );
			} else {
				aCall.setPercent( ( 100.0f * aCall.getDuration( ) ) / aParentDuration );
			}
		}

		for ( final OperationCall child : aCall.getChildren( ) ) {
			addPercentValues( child, aCall.getDuration( ), aRootDuration );
		}
	}
}
