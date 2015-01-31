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

package kieker.gui.common.domain;

/**
 * This enumeration shows the available statistics (and their datatype) which can be added for example to traces or operation calls.
 * 
 * @author Nils Christian Ehmke
 */
public enum StatisticType {

	STACK_DEPTH(Integer.class), STACK_SIZE(Integer.class), AVG_DURATION(Long.class), MIN_DURATION(Long.class), MAX_DURATION(Long.class), TOTAL_DURATION(Long.class),
	PERCENT(Float.class), CALLS(Integer.class);

	private final Class<?> typeOfValue;

	private <T extends Comparable<T>> StatisticType(final Class<T> typeOfValue) {
		this.typeOfValue = typeOfValue;
	}

	public Class<?> getTypeOfValue() {
		return this.typeOfValue;
	}

}
