package kieker.diagnosis.ui.traces.aggregator;

import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import kieker.diagnosis.service.data.MethodCall;

public abstract class Aggregator {

	private final ResourceBundle ivResourceBundle = ResourceBundle.getBundle( Aggregator.class.getName( ) );

	public abstract List<MethodCall> aggregate( List<MethodCall> aCalls );

	protected final MethodCall aggregateToSingleCall( final List<MethodCall> aList ) {
		final double percent = aList.parallelStream( ).map( MethodCall::getPercent ).collect( Collectors.summingDouble( Float::doubleValue ) );
		final long duration = aList.parallelStream( ).map( MethodCall::getDuration ).collect( Collectors.summingLong( Long::longValue ) );
		final int traceDepth = aList.parallelStream( ).map( MethodCall::getTraceDepth ).max( Comparator.naturalOrder( ) ).get( );
		final int traceSize = aList.parallelStream( ).map( MethodCall::getTraceSize ).collect( Collectors.summingInt( Integer::intValue ) );

		// Create the dummy method call
		final MethodCall methodCall = new MethodCall( );

		methodCall.setHost( "-" );
		methodCall.setClazz( "-" );
		methodCall.setMethod( String.format( ivResourceBundle.getString( "methodCallsAggregated" ), aList.size( ) ) );
		methodCall.setTraceId( aList.get( 0 ).getTraceId( ) );
		methodCall.setPercent( (float) percent );
		methodCall.setDuration( duration );
		methodCall.setTraceDepth( traceDepth );
		methodCall.setTraceSize( traceSize );

		return methodCall;
	}

}
