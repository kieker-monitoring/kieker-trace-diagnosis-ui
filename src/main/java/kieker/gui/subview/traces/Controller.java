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

import kieker.gui.common.domain.Execution;
import kieker.gui.common.model.DataModel;
import kieker.gui.common.model.PropertiesModel;
import kieker.gui.subview.ISubController;
import kieker.gui.subview.ISubView;
import kieker.gui.subview.util.AbstractDataModelProxy;
import kieker.gui.subview.util.IModel;

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

	public Controller(final Type filter, final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<Execution> modelProxy = createModelProxy(dataModel, filter);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
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
		// Just implemented for the interface
	}

	private static IModel<Execution> createModelProxy(final DataModel dataModel, final Type filter) {
		if (filter == Type.JUST_FAILED_TRACES) {
			return new FailedTracesModelProxy(dataModel);
		}
		if (filter == Type.JUST_FAILURE_CONTAINING_TRACES) {
			return new FailureContainingTracesModelProxy(dataModel);
		}
		return new TracesModelProxy(dataModel);
	}

	public enum Type {
		NONE, JUST_FAILED_TRACES, JUST_FAILURE_CONTAINING_TRACES
	}

	private static final class TracesModelProxy extends AbstractDataModelProxy<Execution> {

		public TracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Execution> getContent() {
			return super.dataModel.getTracesCopy();
		}

	}

	private static final class FailedTracesModelProxy extends AbstractDataModelProxy<Execution> {

		public FailedTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Execution> getContent() {
			return super.dataModel.getFailedTracesCopy();
		}

	}

	private static final class FailureContainingTracesModelProxy extends AbstractDataModelProxy<Execution> {

		public FailureContainingTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Execution> getContent() {
			return super.dataModel.getFailureContainingTracesCopy();
		}

	}

}
