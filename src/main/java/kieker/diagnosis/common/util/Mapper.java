package kieker.diagnosis.common.util;

import java.util.HashMap;
import java.util.Map;

public final class Mapper<I, O> {

	private final Map<I, O> map = new HashMap<>();

	public To map(final I key) {
		return new To(key);
	}

	public O resolve(final I key) {
		return this.map.get(key);
	}

	public I invertedResolve(final O value) {
		for (final Map.Entry<I, O> entry : this.map.entrySet()) {
			if (value.equals(entry.getValue())) {
				return entry.getKey();
			}
		}
		return null;
	}

	public class To {

		private final I key;

		private To(final I key) {
			this.key = key;
		}

		public void to(final O value) {
			Mapper.this.map.put(this.key, value);
		}

	}

}
