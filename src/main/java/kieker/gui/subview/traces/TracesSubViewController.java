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

package kieker.gui.subview.traces;

import java.util.List;

import kieker.gui.common.AbstractProxyDataModel;
import kieker.gui.common.DataModel;
import kieker.gui.common.ISubController;
import kieker.gui.common.ISubView;
import kieker.gui.common.PropertiesModel;
import kieker.gui.common.domain.Execution;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class TracesSubViewController implements SelectionListener, ISubController {

	private final TracesSubViewModel model;
	private final ISubView view;

	public TracesSubViewController(final DataModel dataModel, final PropertiesModel propertiesModel) {
		this.model = new TracesSubViewModel();
		this.view = new TracesSubView(new AbstractProxyDataModel<Execution>(dataModel) {

			@Override
			public final List<Execution> getContent() {
				return this.dataModel.getTracesCopy();
			}

		}, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof Execution) {
			this.model.setCurrentActiveTrace((Execution) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Nothing to do here. This method is just required by the interface.
	}

}
