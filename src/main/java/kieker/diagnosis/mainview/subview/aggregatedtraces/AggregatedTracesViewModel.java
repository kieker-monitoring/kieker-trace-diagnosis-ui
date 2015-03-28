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

package kieker.diagnosis.mainview.subview.aggregatedtraces;

import kieker.diagnosis.domain.AggregatedOperationCall;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class AggregatedTracesViewModel {

	@Autowired
	private AggregatedTracesView view;

	private Filter filter = Filter.NONE;
	private AggregatedOperationCall operationCall;

	private String regExpr;

	public Filter getFilter() {
		return this.filter;
	}

	public void setFilter(final Filter filter) {
		this.filter = filter;

		this.view.notifyAboutChangedFilter();
	}

	public AggregatedOperationCall getOperationCall() {
		return this.operationCall;
	}

	public void setOperationCall(final AggregatedOperationCall operationCall) {
		this.operationCall = operationCall;

		this.view.notifyAboutChangedOperationCall();
	}

	public String getRegExpr() {
		return this.regExpr;
	}

	public void setRegExpr(final String regExpr) {
		this.regExpr = regExpr;

		this.view.notifyAboutChangedRegExpr();
	}

	public static enum Filter {
		NONE, JUST_FAILED, JUST_FAILURE_CONTAINING
	}

}
