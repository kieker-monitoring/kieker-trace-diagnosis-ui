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

package kieker.diagnosis.service.nameconverter;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.concurrent.TimeUnit;

import kieker.diagnosis.common.Mapper;
import kieker.diagnosis.service.ServiceIfc;
import kieker.diagnosis.service.properties.PropertiesService;
import kieker.diagnosis.service.properties.PropertiesService.TimestampTypes;

/**
 * @author Nils Christian Ehmke
 */
public final class NameConverterService implements ServiceIfc {

	private final Mapper<TimeUnit, String> ivShortTimeUnitMapper = new Mapper<>( );

	private PropertiesService ivPropertiesService;

	public NameConverterService( ) {
		ivShortTimeUnitMapper.map( TimeUnit.NANOSECONDS ).to( "ns" );
		ivShortTimeUnitMapper.map( TimeUnit.MICROSECONDS ).to( "us" );
		ivShortTimeUnitMapper.map( TimeUnit.MILLISECONDS ).to( "ms" );
		ivShortTimeUnitMapper.map( TimeUnit.SECONDS ).to( "s" );
		ivShortTimeUnitMapper.map( TimeUnit.MINUTES ).to( "m" );
		ivShortTimeUnitMapper.map( TimeUnit.HOURS ).to( "h" );
		ivShortTimeUnitMapper.map( TimeUnit.DAYS ).to( "d" );
		ivShortTimeUnitMapper.mapPerDefault( ).to( "" );
	}

	public String toShortTimeUnit( final TimeUnit aTimeUnit ) {
		return ivShortTimeUnitMapper.resolve( aTimeUnit );
	}

	public String toShortComponentName( final String aComponentName ) {
		final int lastPointPos = aComponentName.lastIndexOf( '.' );
		return aComponentName.substring( lastPointPos + 1 );
	}

	public String toShortOperationName( final String aOperationName ) {
		final String result = aOperationName.replaceAll( "\\(.*\\)", "(...)" );
		final int lastPointPos = result.lastIndexOf( '.', result.length( ) - 5 );
		return result.substring( lastPointPos + 1 );
	}

	public String toDurationString( final long aDuration, final TimeUnit aSourceUnit, final TimeUnit aTargetUnit ) {
		final String shortSourceUnit = toShortTimeUnit( aSourceUnit );
		final String shortTargetUnit = toShortTimeUnit( aTargetUnit );

		final long targetDuration = aTargetUnit.convert( aDuration, aSourceUnit );

		return String.format( "%d %s (%d %s)", targetDuration, shortTargetUnit, aDuration, shortSourceUnit );
	}

	public String toTimestampString( final long aTimestamp, final TimeUnit aSourceUnit ) {
		final TimestampTypes timestampType = ivPropertiesService.getTimestampType( );
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
