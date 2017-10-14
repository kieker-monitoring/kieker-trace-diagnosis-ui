package kieker.diagnosis.service.settings;

import java.util.concurrent.TimeUnit;

/**
 * This is a data transfer object holding the settings of the application.
 *
 * @author Nils Christian Ehmke
 */
public final class Settings {

	private TimestampAppearance ivTimestampAppearance;
	private TimeUnit ivTimeUnit;
	private ClassAppearance ivClassAppearance;
	private MethodAppearance ivMethodAppearance;
	private boolean ivShowUnmonitoredTimeProperty;
	private MethodCallAggregation ivMethodCallAggregation;
	private int ivMaxNumberOfMethodCalls;
	private float ivMethodCallThreshold;

	public TimestampAppearance getTimestampAppearance( ) {
		return ivTimestampAppearance;
	}

	public void setTimestampAppearance( final TimestampAppearance aTimestampAppearance ) {
		ivTimestampAppearance = aTimestampAppearance;
	}

	public TimeUnit getTimeUnit( ) {
		return ivTimeUnit;
	}

	public void setTimeUnit( final TimeUnit aTimeUnit ) {
		ivTimeUnit = aTimeUnit;
	}

	public ClassAppearance getClassAppearance( ) {
		return ivClassAppearance;
	}

	public void setClassAppearance( final ClassAppearance aClassAppearance ) {
		ivClassAppearance = aClassAppearance;
	}

	public MethodAppearance getMethodAppearance( ) {
		return ivMethodAppearance;
	}

	public void setMethodAppearance( final MethodAppearance aMethodAppearance ) {
		ivMethodAppearance = aMethodAppearance;
	}

	public boolean isShowUnmonitoredTimeProperty( ) {
		return ivShowUnmonitoredTimeProperty;
	}

	public void setShowUnmonitoredTimeProperty( final boolean aShowUnmonitoredTimeProperty ) {
		ivShowUnmonitoredTimeProperty = aShowUnmonitoredTimeProperty;
	}

	public MethodCallAggregation getMethodCallAggregation( ) {
		return ivMethodCallAggregation;
	}

	public void setMethodCallAggregation( final MethodCallAggregation aMethodCallAggregation ) {
		ivMethodCallAggregation = aMethodCallAggregation;
	}

	public int getMaxNumberOfMethodCalls( ) {
		return ivMaxNumberOfMethodCalls;
	}

	public void setMaxNumberOfMethodCalls( final int aMaxNumberOfMethodCalls ) {
		ivMaxNumberOfMethodCalls = aMaxNumberOfMethodCalls;
	}

	public float getMethodCallThreshold( ) {
		return ivMethodCallThreshold;
	}

	public void setMethodCallThreshold( final float aMethodCallThreshold ) {
		ivMethodCallThreshold = aMethodCallThreshold;
	}

}
