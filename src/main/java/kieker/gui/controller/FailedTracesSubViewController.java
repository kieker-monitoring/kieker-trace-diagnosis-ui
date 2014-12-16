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

import kieker.gui.model.DataModel;
import kieker.gui.model.PropertiesModel;
import kieker.gui.model.TracesSubViewModel;
import kieker.gui.model.domain.ExecutionEntry;
import kieker.gui.view.TracesSubView;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class FailedTracesSubViewController implements SelectionListener, ISubController {

	private final TracesSubViewModel tracesSubViewModel;
	private final TracesSubView view;

	public FailedTracesSubViewController(final DataModel model, final PropertiesModel propertiesModel) {
		this.tracesSubViewModel = new TracesSubViewModel();
		this.view = new TracesSubView(TracesSubView.Type.SHOW_JUST_FAILED_TRACES, model, this.tracesSubViewModel, propertiesModel, this);
	}

	@Override
	public TracesSubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof ExecutionEntry) {
			this.tracesSubViewModel.setCurrentActiveTrace((ExecutionEntry) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {}

}
