package kieker.diagnosis.domain;


/**
 * This class represents a concrete aggregated database call within this application. It
 * adds some properties that are only required for this type of calls, like the
 * trace ID and the duration. It extends the call tree mechanism (inherited from
 * {@link AbstractOperationCall}) by a parent, allowing to navigate in both
 * directions within the tree.
 * 
 * @author Christian Zirkelbach
 */
public final class AggregatedDatabaseOperationCall extends AbstractOperationCall<AggregatedDatabaseOperationCall> {

	private AggregatedDatabaseOperationCall parent;
	private long totalDuration;
	private long minDuration;
	private long maxDuration;
	private long avgDuration;
	private int calls;
	
	private String callArguments;

	public AggregatedDatabaseOperationCall(final String container, final String component,
			final String operation, final String callArguments,
			final long totalDuration, final long minDuration, final long maxDuration, final long avgDuration,
			final int calls) {
		super(container, component, operation, null);

		this.callArguments = callArguments;
		this.totalDuration = totalDuration;
		this.minDuration = minDuration;
		this.maxDuration = maxDuration;
		this.avgDuration = avgDuration;
		this.calls = calls;
	}
	
	@Override
	public void addChild(final AggregatedDatabaseOperationCall child) {
		super.addChild(child);
		child.parent = this;
	}
	
	public long getTotalDuration() {
		return this.totalDuration;
	}

	public void setTotalDuration(final long totalDuration) {
		this.totalDuration = totalDuration;
	}

	public long getMinDuration() {
		return this.minDuration;
	}

	public void setMinDuration(final long minDuration) {
		this.minDuration = minDuration;
	}

	public long getMaxDuration() {
		return this.maxDuration;
	}

	public void setMaxDuration(final long maxDuration) {
		this.maxDuration = maxDuration;
	}

	public long getAvgDuration() {
		return this.avgDuration;
	}

	public void setAvgDuration(final long avgDuration) {
		this.avgDuration = avgDuration;
	}

	public int getCalls() {
		return this.calls;
	}

	public void setCalls(final int calls) {
		this.calls = calls;
	}

	public String getStringClassArgs() {
		return callArguments;
	}

	public void setStringClassArgs(String stringClassArgs) {
		this.callArguments = stringClassArgs;
	}
	
	public AggregatedDatabaseOperationCall getParent() {
		return parent;
	}

	public void setParent(AggregatedDatabaseOperationCall parent) {
		this.parent = parent;
	}
}
