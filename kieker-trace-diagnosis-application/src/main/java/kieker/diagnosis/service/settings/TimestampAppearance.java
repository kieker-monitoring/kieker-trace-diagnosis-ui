package kieker.diagnosis.service.settings;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

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