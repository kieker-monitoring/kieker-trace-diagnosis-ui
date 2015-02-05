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

package kieker.diagnosis.subview.util;

import java.util.Observable;
import java.util.Observer;

import kieker.diagnosis.common.model.DataModel;

public abstract class AbstractDataModelProxy<T> extends Observable implements IModel<T>, Observer {

	private final DataModel dataModel;

	public AbstractDataModelProxy(final DataModel dataModel) {
		this.dataModel = dataModel;
		this.dataModel.addObserver(this);
	}

	@Override
	public final void update(final Observable o, final Object arg) {
		this.setChanged();
		this.notifyObservers(arg);
	}

	@Override
	public final String getShortTimeUnit() {
		return this.dataModel.getShortTimeUnit();
	}

	protected final DataModel getDataModel() {
		return this.dataModel;
	}
}
