/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.search.methods;

import java.time.LocalDate;
import java.time.LocalTime;

import lombok.Getter;
import lombok.Setter;

/**
 * This is a data transfer object holding the filter for the methods service.
 *
 * @author Nils Christian Ehmke
 */
@Getter
@Setter
public final class MethodsFilter {

	private String host;
	private String clazz;
	private String method;
	private String exception;
	private Long traceId;
	private SearchType searchType = SearchType.ALL;
	private boolean useRegExpr;
	private LocalTime lowerTime;
	private LocalDate upperDate;
	private LocalTime upperTime;
	private LocalDate lowerDate;

}
