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

package kieker.diagnosis.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.model.PropertiesModel.TimestampTypes;

/**
 * @author Nils Christian Ehmke
 */
public final class NameConverter {

	private static Mapper<TimeUnit, String> cvShortTimeUnitMapper = new Mapper<>( );

	private NameConverter( ) {
	}

	static {
		NameConverter.initializeMapper( );
	}

	private static void initializeMapper( ) {
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.NANOSECONDS ).to( "ns" );
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.MICROSECONDS ).to( "us" );
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.MILLISECONDS ).to( "ms" );
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.SECONDS ).to( "s" );
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.MINUTES ).to( "m" );
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.HOURS ).to( "h" );
		NameConverter.cvShortTimeUnitMapper.map( TimeUnit.DAYS ).to( "d" );
		NameConverter.cvShortTimeUnitMapper.mapPerDefault( ).to( "" );
	}

	public static String toShortTimeUnit( final TimeUnit aTimeUnit ) {
		return NameConverter.cvShortTimeUnitMapper.resolve( aTimeUnit );
	}

	public static String toShortComponentName( final String aComponentName ) {
		final int lastPointPos = aComponentName.lastIndexOf( '.' );
		return aComponentName.substring( lastPointPos + 1 );
	}

	public static String toShortOperationName( final String aOperationName ) {
		final String result = aOperationName.replaceAll( "\\(.*\\)", "(...)" );
		final int lastPointPos = result.lastIndexOf( '.', result.length( ) - 5 );
		return result.substring( lastPointPos + 1 );
	}

	public static String toDurationString( final long aDuration, final TimeUnit aSourceUnit, final TimeUnit aTargetUnit ) {
		final String shortSourceUnit = NameConverter.toShortTimeUnit( aSourceUnit );
		final String shortTargetUnit = NameConverter.toShortTimeUnit( aTargetUnit );

		final long targetDuration = aTargetUnit.convert( aDuration, aSourceUnit );

		return targetDuration + " " + shortTargetUnit + " (" + aDuration + " " + shortSourceUnit + ")";
	}

	public static String toTimestampString( final long aTimestamp, final TimeUnit aSourceUnit ) {
		final TimestampTypes timestampType = PropertiesModel.getInstance( ).getTimestampType( );
		if ( timestampType == TimestampTypes.TIMESTAMP ) {
			return Long.toString( aTimestamp );
		}

		final DateTimeFormatter formatter;

		switch ( timestampType ) {
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

		final long timestampInMS = TimeUnit.MILLISECONDS.convert( aTimestamp, aSourceUnit );
		final Instant instant = Instant.ofEpochMilli( timestampInMS );
		final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant( instant, ZoneId.systemDefault( ) );
		return formatter.format( zonedDateTime );
	}

}
