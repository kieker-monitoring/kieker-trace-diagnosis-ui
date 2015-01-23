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

package kieker.gui.subview.aggregatedtraces;

import java.util.List;

import kieker.gui.common.domain.AggregatedExecution;
import kieker.gui.common.model.DataModel;
import kieker.gui.common.model.PropertiesModel;
import kieker.gui.subview.ISubController;
import kieker.gui.subview.ISubView;
import kieker.gui.subview.util.AbstractDataModelProxy;
import kieker.gui.subview.util.IModel;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;

public class Controller implements ISubController, SelectionListener {

	private final ISubView view;
	private final Model model;

	public Controller(final Filter filter, final DataModel dataModel, final PropertiesModel propertiesModel) {
		final IModel<AggregatedExecution> modelProxy = createModelProxy(dataModel, filter);
		this.model = new Model();

		this.view = new View(modelProxy, this.model, propertiesModel, this);
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

	private static IModel<AggregatedExecution> createModelProxy(final DataModel dataModel, final Filter filter) {
		if (filter == Filter.JUST_FAILED_TRACES) {
			return new FailedTracesModelProxy(dataModel);
		}
		if (filter == Filter.JUST_FAILURE_CONTAINING_TRACES) {
			return new FailureContainingTracesModelProxy(dataModel);
		}
		return new TracesModelProxy(dataModel);
	}

	public enum Filter {
		NONE, JUST_FAILED_TRACES, JUST_FAILURE_CONTAINING_TRACES
	}

	private static final class TracesModelProxy extends AbstractDataModelProxy<AggregatedExecution> {

		private TracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<AggregatedExecution> getContent() {
			return super.dataModel.getAggregatedTracesCopy();
		}

	}

	private static final class FailedTracesModelProxy extends AbstractDataModelProxy<AggregatedExecution> {

		private FailedTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<AggregatedExecution> getContent() {
			return super.dataModel.getFailedAggregatedTracesCopy();
		}

	}

	private static final class FailureContainingTracesModelProxy extends AbstractDataModelProxy<AggregatedExecution> {

		private FailureContainingTracesModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<AggregatedExecution> getContent() {
			return super.dataModel.getFailureContainingAggregatedTracesCopy();
		}

	}

}
