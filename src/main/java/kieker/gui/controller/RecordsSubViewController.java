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
import kieker.gui.view.RecordsSubView;

public class RecordsSubViewController implements ISubController {

	private final DataModel model;
	private final RecordsSubView view;

	public RecordsSubViewController(final DataModel model) {
		this.model = model;
		this.view = new RecordsSubView(this.model, this);
	}

	@Override
	public RecordsSubView getView() {
		return this.view;
	}

}
