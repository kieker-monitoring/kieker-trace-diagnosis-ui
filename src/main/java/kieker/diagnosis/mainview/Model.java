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

package kieker.diagnosis.mainview;

import org.eclipse.swt.graphics.Cursor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * The model of the main view.
 * 
 * @author Nils Christian Ehmke
 */
@Component
public final class Model {

	@Autowired
	private View view;

	private Cursor cursor;
	private SubView activeSubView;

	public Cursor getCursor() {
		return this.cursor;
	}

	public void setCursor(final Cursor cursor) {
		this.cursor = cursor;

		this.view.notifyAboutChangedCursor();
	}

	public SubView getActiveSubView() {
		return this.activeSubView;
	}

	public void setActiveSubView(final SubView activeSubView) {
		this.activeSubView = activeSubView;

		this.view.notifyAboutChangedSubView();
	}

	public enum SubView {
		TRACES_SUB_VIEW, AGGREGATED_TRACES_SUB_VIEW, NONE, AGGREGATED_OPERATION_CALLS_SUB_VIEW, OPERATION_CALLS_SUB_VIEW,
	}

}
