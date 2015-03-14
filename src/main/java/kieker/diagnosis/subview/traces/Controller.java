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

package kieker.diagnosis.subview.traces;

import java.util.List;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.domain.Trace;
import kieker.diagnosis.model.DataModel;
import kieker.diagnosis.model.PropertiesModel;
import kieker.diagnosis.subview.Filter;
import kieker.diagnosis.subview.ISubController;
import kieker.diagnosis.subview.ISubView;
import kieker.diagnosis.subview.util.AbstractDataModelProxy;
import kieker.diagnosis.subview.util.IModel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

/**
 * The sub-controller responsible for the sub-view presenting the available traces.
 *
 * @author Nils Christian Ehmke
 */
public final class Controller implements ISubController, SelectionListener {

	private final ISubView view;
	private final Model model;

	public Controller(final Filter filter, final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<Trace> modelProxy = Controller.createModelProxy(dataModel, filter);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.item.getData() instanceof OperationCall) {
			this.model.setCurrentActiveCall((OperationCall) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		// Just implemented for the interface
	}

	private static IModel<Trace> createModelProxy(final DataModel dataModel, final Filter filter) {
		if (filter == Filter.JUST_FAILED) {
			return new FailedTracesModelProxy(dataModel);
		}
		if (filter == Filter.JUST_FAILURE_CONTAINING) {
			return new FailureContainingTracesModelProxy(dataModel);
		}
		return new TracesModelProxy(dataModel);
	}

	private static final class TracesModelProxy extends AbstractDataModelProxy<Trace> {

		public TracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Trace> getContent() {
			return super.getDataModel().getTracesCopy();
		}

	}

	private static final class FailedTracesModelProxy extends AbstractDataModelProxy<Trace> {

		public FailedTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Trace> getContent() {
			return super.getDataModel().getFailedTracesCopy();
		}

	}

	private static final class FailureContainingTracesModelProxy extends AbstractDataModelProxy<Trace> {

		public FailureContainingTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Trace> getContent() {
			return super.getDataModel().getFailureContainingTracesCopy();
		}

	}

}
