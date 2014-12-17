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

import java.util.List;

import kieker.gui.model.AbstractProxyDataModel;
import kieker.gui.model.AggregatedTracesSubViewModel;
import kieker.gui.model.DataModel;
import kieker.gui.model.PropertiesModel;
import kieker.gui.model.domain.AggregatedExecution;
import kieker.gui.view.AggregatedTracesSubView;
import kieker.gui.view.ISubView;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * The sub-controller responsible for the sub-view presenting the available aggregated traces.
 *
 * @author Nils Christian Ehmke
 */
public final class AggregatedTracesSubViewController implements SelectionListener, ISubController {

	private final ISubView view;
	private final AggregatedTracesSubViewModel model;

	public AggregatedTracesSubViewController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		this.model = new AggregatedTracesSubViewModel();
		this.view = new AggregatedTracesSubView(new AbstractProxyDataModel<AggregatedExecution>(dataModel) {

			@Override
			public List<AggregatedExecution> getContent() {
				return super.dataModel.getAggregatedTracesCopy();
			}
		}, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof AggregatedExecution) {
			this.model.setCurrentActiveTrace((AggregatedExecution) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

}
