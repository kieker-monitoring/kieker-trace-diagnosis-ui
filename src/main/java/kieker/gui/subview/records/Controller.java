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

package kieker.gui.subview.records;

import java.util.List;

import kieker.gui.common.AbstractDataModelProxy;
import kieker.gui.common.DataModel;
import kieker.gui.common.IModel;
import kieker.gui.common.ISubController;
import kieker.gui.common.ISubView;
import kieker.gui.common.domain.Record;

/**
 * The sub-controller responsible for the sub-view presenting the available records.
 *
 * @author Nils Christian Ehmke
 */
public final class Controller implements ISubController {

	private final ISubView view;

	public Controller(final DataModel dataModel) {
		final IModel<Record> modelProxy = new RecordsModelProxy(dataModel);

		this.view = new View(modelProxy, this);
	}

	@Override
	public ISubView getView() {
		return this.view;
	}

	private final class RecordsModelProxy extends AbstractDataModelProxy<Record> {

		private RecordsModelProxy(final DataModel dataModel) {
			super(dataModel);
		}

		@Override
		public List<Record> getContent() {
			return super.dataModel.getRecordsCopy();
		}
	}

}
