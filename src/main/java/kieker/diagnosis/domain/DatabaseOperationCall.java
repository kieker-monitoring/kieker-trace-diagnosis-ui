package kieker.diagnosis.domain;

/**
 * This class represents a concrete database call within this application. It
 * adds some properties that are only required for this type of calls, like the
 * trace ID and the duration. It extends the call tree mechanism (inherited from
 * {@link AbstractOperationCall}) by a parent, allowing to navigate in both
 * directions within the tree.
 * 
 * @author Christian Zirkelbach
 */
public class DatabaseOperationCall extends AbstractOperationCall<DatabaseOperationCall> {

	private final long traceID;

	private DatabaseOperationCall parent;
	private float percent;
	private long duration;
	private long timestamp;
	private String callArguments;
	private String returnValue;

	public DatabaseOperationCall(final String container, final String component,
			final String operation, final String callArguments,
			final String returnValue, final long traceID,
			final long timestamp, final long duration) {
		super(container, component, operation, null);

		this.callArguments = callArguments;
		this.returnValue = returnValue;
		this.traceID = traceID;
		this.timestamp = timestamp;
		this.duration = duration;
	}

	@Override
	public void addChild(final DatabaseOperationCall child) {
		super.addChild(child);
		child.parent = this;
	}

	public DatabaseOperationCall getParent() {
		return this.parent;
	}

	public float getPercent() {
		return this.percent;
	}

	public void setPercent(final float percent) {
		this.percent = percent;
	}

	public long getDuration() {
		return this.duration;
	}

	public void setDuration(final long duration) {
		this.duration = duration;
	}

	public long getTraceID() {
		return this.traceID;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public void setTimestamp(final long timestamp) {
		this.timestamp = timestamp;
	}

	public String getStringClassArgs() {
		return callArguments;
	}

	public void setStringClassArgs(String stringClassArgs) {
		this.callArguments = stringClassArgs;
	}

	public String getFormattedReturnValue() {
		return returnValue;
	}

	public void setFormattedReturnValue(String formattedReturnValue) {
		this.returnValue = formattedReturnValue;
	}

}
