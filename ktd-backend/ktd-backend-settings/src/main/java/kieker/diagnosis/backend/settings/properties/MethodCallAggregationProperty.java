/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.settings.properties;

import com.google.inject.Singleton;

import kieker.diagnosis.backend.properties.EnumApplicationProperty;
import kieker.diagnosis.backend.settings.MethodCallAggregation;

/**
 * The application property for the type of the method call aggregation.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
public final class MethodCallAggregationProperty extends EnumApplicationProperty<MethodCallAggregation> {

	public MethodCallAggregationProperty( ) {
		super( MethodCallAggregation.class );
	}

	@Override
	public MethodCallAggregation getDefaultValue( ) {
		return MethodCallAggregation.NONE;
	}

	@Override
	public String getKey( ) {
		return "methodCallAggregation";
	}

}
