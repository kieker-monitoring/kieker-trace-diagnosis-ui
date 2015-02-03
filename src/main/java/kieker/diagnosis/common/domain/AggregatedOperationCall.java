package kieker.diagnosis.common.domain;

public final class AggregatedOperationCall extends AbstractOperationCall<AggregatedOperationCall> {

	private long totalDuration;
	private long minDuration;
	private long maxDuration;
	private long avgDuration;
	private int calls;

	public AggregatedOperationCall(final OperationCall call) {
		super(call.getContainer(), call.getComponent(), call.getOperation(), call.getFailedCause());

		for (final OperationCall child : call.getChildren()) {
			super.addChild(new AggregatedOperationCall(child));
		}

		this.setStackDepth(call.getStackDepth());
		this.setStackSize(call.getStackSize());
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

}
