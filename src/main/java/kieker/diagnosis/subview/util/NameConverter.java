/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.subview.util;

public final class NameConverter {

	private NameConverter() {}

	public static String toShortComponentName(final String componentName) {
		final int lastPointPos = componentName.lastIndexOf('.');
		return componentName.substring(lastPointPos + 1);
	}

	public static String toShortOperationName(final String operationName) {
		final String result = operationName.replaceAll("\\(.*\\)", "(...)");
		final int lastPointPos = result.lastIndexOf('.', result.length() - 5);
		return result.substring(lastPointPos + 1);
	}

}
