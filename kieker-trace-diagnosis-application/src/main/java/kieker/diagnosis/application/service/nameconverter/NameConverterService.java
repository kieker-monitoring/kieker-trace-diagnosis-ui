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

package kieker.diagnosis.application.service.nameconverter;

import java.util.concurrent.TimeUnit;

/**
 * @author Nils Christian Ehmke
 */
public interface NameConverterService {

	public String toShortTimeUnit( TimeUnit aTimeUnit );

	public String toShortComponentName( String aComponentName );

	public String toShortOperationName( String aOperationName );

	public String toDurationString( long aDuration, TimeUnit aSourceUnit, TimeUnit aTargetUnit );

	public String toTimestampString( long aTimestamp, TimeUnit aSourceUnit );

}
