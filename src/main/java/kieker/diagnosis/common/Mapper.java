/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Nils Christian Ehmke
 *
 * @param <I>
 *            The type of the keys.
 * @param <O>
 *            The type of the values.
 */
public final class Mapper<I, O> extends HashMap<I, O> {

	private static final long serialVersionUID = 1L;
	private O defaultValue;

	public To map(final I key) {
		return new To(key);
	}

	public To mapPerDefault() {
		return new To();
	}

	public O resolve(final I key) {
		if (super.containsKey(key)) {
			return super.get(key);
		} else {
			return this.defaultValue;
		}
	}

	public I invertedResolve(final O value) {
		return super.entrySet().parallelStream().filter(entry -> value.equals(entry.getValue())).map(Map.Entry::getKey).findFirst().orElse(null);
	}

	/**
	 * @author Nils Christian Ehmke
	 */
	public final class To {

		private final I key;
		private final boolean keyAvailable;

		protected To(final I key) {
			this.key = key;
			this.keyAvailable = true;
		}

		protected To() {
			this.key = null;
			this.keyAvailable = false;
		}

		public void to(final O value) {
			if (this.keyAvailable) {
				Mapper.super.put(this.key, value);
			} else {
				Mapper.this.defaultValue = value;
			}
		}

	}

}
