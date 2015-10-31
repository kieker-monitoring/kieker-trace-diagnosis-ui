package kieker.diagnosis.util;

import java.util.EnumMap;

public class Context extends EnumMap<ContextKey, Object> {

	private static final long serialVersionUID = 1L;

	public Context(final ContextEntry... entries) {
		super(ContextKey.class);

		for (final ContextEntry entry : entries) {
			super.put(entry.getKey(), entry.getValue());
		}
	}

}
