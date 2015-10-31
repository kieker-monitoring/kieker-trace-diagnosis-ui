package kieker.diagnosis.util;

public final class ContextEntry {

	private final ContextKey key;
	private final Object value;

	public ContextEntry(final ContextKey key, final Object value) {
		this.key = key;
		this.value = value;
	}

	public ContextKey getKey() {
		return key;
	}

	public Object getValue() {
		return value;
	}

}
