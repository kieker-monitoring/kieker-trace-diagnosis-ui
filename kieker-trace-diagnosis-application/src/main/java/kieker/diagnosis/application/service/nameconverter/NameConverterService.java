package kieker.diagnosis.application.service.nameconverter;

import java.util.concurrent.TimeUnit;

public interface NameConverterService {

	public String toShortTimeUnit( TimeUnit aTimeUnit );

	public String toShortComponentName( String aComponentName );

	public String toShortOperationName( String aOperationName );

	public String toDurationString( long aDuration, TimeUnit aSourceUnit, TimeUnit aTargetUnit );

	public String toTimestampString( long aTimestamp, TimeUnit aSourceUnit );

}
