/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.gui.components.converter;

import kieker.diagnosis.application.service.properties.MethodCallAggregation;
import kieker.diagnosis.architecture.util.Mapper;

import java.util.ResourceBundle;

/**
 * @author Nils Christian Ehmke
 */
public final class MethodCallAggregationStringConverter extends AbstractStringConverter<MethodCallAggregation> {

	@Override
	protected void fillMapper( final Mapper<MethodCallAggregation, String> aMapper, final ResourceBundle aResourceBundle ) {
		aMapper.map( MethodCallAggregation.NONE ).to( aResourceBundle.getString( "methodCallAggregationNone" ) );
		aMapper.map( MethodCallAggregation.BY_DURATION ).to( aResourceBundle.getString( "methodCallAggregationDuration" ) );
		aMapper.map( MethodCallAggregation.BY_THRESHOLD ).to( aResourceBundle.getString( "methodCallAggregationThreshold" ) );
		aMapper.map( MethodCallAggregation.BY_TRACE_DEPTH ).to( aResourceBundle.getString( "methodCallAggregationTraceDepth" ) );
		aMapper.map( MethodCallAggregation.BY_TRACE_SIZE ).to( aResourceBundle.getString( "methodCallAggregationTraceSize" ) );
	}

}
