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

package kieker.gui.controller;

import kieker.gui.model.AggregatedTracesSubViewModel;
import kieker.gui.model.DataModel;
import kieker.gui.model.PropertiesModel;
import kieker.gui.model.domain.AggregatedExecutionEntry;
import kieker.gui.view.AggregatedTracesSubView;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class AggregatedTracesSubViewController implements SelectionListener, ISubController {

	private final DataModel model;
	private final AggregatedTracesSubView view;
	private final AggregatedTracesSubViewModel aggregatedTracesSubViewModel;

	public AggregatedTracesSubViewController(final DataModel model, final PropertiesModel propertiesModel) {
		this.model = model;
		this.aggregatedTracesSubViewModel = new AggregatedTracesSubViewModel();

		this.view = new AggregatedTracesSubView(this.model, this.aggregatedTracesSubViewModel, propertiesModel, this);
	}

	@Override
	public AggregatedTracesSubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof AggregatedExecutionEntry) {
			this.aggregatedTracesSubViewModel.setCurrentActiveTrace((AggregatedExecutionEntry) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

}
