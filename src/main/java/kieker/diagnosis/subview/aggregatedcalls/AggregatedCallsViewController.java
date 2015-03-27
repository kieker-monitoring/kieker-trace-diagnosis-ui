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

package kieker.diagnosis.subview.aggregatedcalls;

import kieker.diagnosis.domain.AggregatedOperationCall;
import kieker.diagnosis.subview.ISubController;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.aggregatedcalls.AggregatedCallsViewModel.Filter;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class AggregatedCallsViewController implements ISubController, SelectionListener {

	@Autowired
	private AggregatedCallsView view;

	@Autowired
	private AggregatedCallsViewModel model;

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.widget == view.getBtn1()) {
			this.model.setFilter(Filter.NONE);
		} 
		if (e.widget == view.getBtn2()) {
			this.model.setFilter(Filter.JUST_FAILED);
		}
		if ((e.item != null) && (e.item.getData() instanceof AggregatedOperationCall)) {
			this.model.setOperationCall((AggregatedOperationCall) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Just implemented for the interface
	}

}
