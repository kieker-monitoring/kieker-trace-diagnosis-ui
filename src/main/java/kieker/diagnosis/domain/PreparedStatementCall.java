package kieker.diagnosis.domain;

/**
 * This class represents a concrete prepared statement database call within this
 * application. It adds some properties that are only required for this type of
 * calls.It extends the call tree mechanism (inherited from
 * {@link AbstractOperationCall}) by a parent, allowing to navigate in both
 * directions within the tree.
 * 
 * @author Christian Zirkelbach
 */
public final class PreparedStatementCall extends
		AbstractOperationCall<PreparedStatementCall> {

	private final long traceID;

	private PreparedStatementCall parent;
	private float percent;
	private long duration;
	private long timestamp;
	private String abstractStatement;
	private String concreteStatement;
	private String returnValue;

	public PreparedStatementCall(final String container,
			final String component, final String operation,
			final String returnValue, final long traceID, final long timestamp,
			final long duration) {
		super(container, component, operation, null);

		this.returnValue = returnValue;
		this.traceID = traceID;
		this.timestamp = timestamp;
		this.duration = duration;
	}

	public PreparedStatementCall(final String container,
			final String component, final String operation,
			final String returnValue, final long traceID, final long timestamp,
			final long duration, final String abstractStatement,
			final String concreteStatement) {
		super(container, component, operation, null);

		this.returnValue = returnValue;
		this.traceID = traceID;
		this.timestamp = timestamp;
		this.duration = duration;
		this.abstractStatement = abstractStatement;
		this.concreteStatement = concreteStatement;
	}

	public void addChild(final PreparedStatementCall child) {
		super.addChild(child);
		child.parent = this;
	}

	public PreparedStatementCall getParent() {
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

	public String getFormattedReturnValue() {
		return returnValue;
	}

	public void setFormattedReturnValue(String formattedReturnValue) {
		this.returnValue = formattedReturnValue;
	}

	public String getAbstractStatement() {
		return abstractStatement;
	}

	public void setAbstractStatement(String abstractStatement) {
		this.abstractStatement = abstractStatement;
	}

	public String getConcreteStatement() {
		return concreteStatement;
	}

	public void setConcreteStatement(String concreteStatement) {
		this.concreteStatement = concreteStatement;
	}

	public long getTotalDuration() {
		long totalDuration = 0;
		if (this.parent == null)
			for (PreparedStatementCall child : this.getChildren()) {
				totalDuration += child.getDuration();
			}
		return totalDuration;

	}

	public long getAverageDuration() {
		long totalDuration = this.getTotalDuration();
		final int numberOfChildren = this.getChildren().size();
		if ((this.parent == null) && (numberOfChildren > 0)) {
			totalDuration /= numberOfChildren;
		}
		return totalDuration;
	}

	public long getMinDuration() {
		long minDuration = this.getTotalDuration();
		if (this.parent == null)
			for (PreparedStatementCall child : this.getChildren()) {
				long childDuration = child.getDuration();
				if (childDuration < minDuration) {
					minDuration = childDuration;
				}
			}
		return minDuration;
	}

	public long getMaxDuration() {
		long maxDuration = 0;
		if (this.parent == null)
			for (PreparedStatementCall child : this.getChildren()) {
				long childDuration = child.getDuration();
				if (childDuration > maxDuration) {
					maxDuration = childDuration;
				}
			}
		return maxDuration;
	}
}
