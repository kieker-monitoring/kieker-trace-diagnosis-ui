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

package kieker.gui.model;

import java.util.Observable;

public final class MainViewModel extends Observable {

	private SubView currentActiveSubView = SubView.NONE;

	public SubView getCurrentActiveSubView() {
		return this.currentActiveSubView;
	}

	public void setCurrentActiveSubView(final SubView currentActiveSubView) {
		this.currentActiveSubView = currentActiveSubView;

		this.setChanged();
		this.notifyObservers();
	}

	public enum SubView {
		RECORDS_SUB_VIEW, TRACES_SUB_VIEW, FAILED_TRACES_SUB_VIEW, AGGREGATED_TRACES_SUB_VIEW, NONE, FAILURE_CONTAINING_TRACES_SUB_VIEW
	}

}
