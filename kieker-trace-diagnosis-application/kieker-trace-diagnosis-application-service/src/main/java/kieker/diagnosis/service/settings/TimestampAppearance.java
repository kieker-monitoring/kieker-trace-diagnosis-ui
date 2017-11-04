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

package kieker.diagnosis.service.settings;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

/**
 * This enumeration represents the possible appearances of timestamps in the ui.
 *
 * @author Nils Christian Ehmke
 */
public enum TimestampAppearance {

	TIMESTAMP, DATE, SHORT_TIME, LONG_TIME, DATE_AND_TIME;

	public String convert( final long aTimestamp ) {
		final String timestamp;

		if ( this == TIMESTAMP ) {
			timestamp = Long.toString( aTimestamp );
		} else {
			final DateTimeFormatter formatter;

			switch ( this ) {
				case DATE:
					formatter = DateTimeFormatter.ofLocalizedDate( FormatStyle.SHORT );
				break;
				case DATE_AND_TIME:
					formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.MEDIUM );
				break;
				case LONG_TIME:
					formatter = DateTimeFormatter.ofLocalizedTime( FormatStyle.LONG );
				break;
				case SHORT_TIME:
					formatter = DateTimeFormatter.ofLocalizedTime( FormatStyle.SHORT );
				break;
				default:
					formatter = DateTimeFormatter.ofLocalizedDateTime( FormatStyle.SHORT );
				break;

			}

			final Instant instant = Instant.ofEpochMilli( aTimestamp );
			final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant( instant, ZoneId.systemDefault( ) );
			timestamp = formatter.format( zonedDateTime );
		}

		return timestamp.intern( );
	}

}
