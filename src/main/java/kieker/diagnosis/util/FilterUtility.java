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

package kieker.diagnosis.util;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javafx.scene.control.TextField;
import kieker.diagnosis.model.PropertiesModel;

public final class FilterUtility {

	private FilterUtility() {}
	
	public static <T> Predicate<T> useFilter(final TextField filter, final Function<T, String> function) {
		final String text = filter.getText();

		if ((text == null) || text.isEmpty()) {
			return (x -> true);
		} else {
			final boolean regularExpressionsActive = PropertiesModel.getInstance().isRegularExpressionsActive();
			if (regularExpressionsActive) {
				checkRegularExpression(text);
				return (x -> function.apply(x).matches(text));
			} else {
				final boolean caseSensitivityActive = PropertiesModel.getInstance().isCaseSensitivityActive();
				if (caseSensitivityActive) {
					return (x -> function.apply(x).contains(text));
				} else {
					return (x -> function.apply(x).toLowerCase().contains(text.toLowerCase()));
				}
			}
		}
	}
	
	public static <T> Predicate<T> alwaysTrue() {
		return (x -> true);
	}

	private static void checkRegularExpression(String text) {
		Pattern.compile(text);
	}
	
}
