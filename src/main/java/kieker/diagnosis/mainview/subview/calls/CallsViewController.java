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

package kieker.diagnosis.mainview.subview.calls;

import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.mainview.Controller;
import kieker.diagnosis.mainview.subview.ISubController;
import kieker.diagnosis.mainview.subview.ISubView;
import kieker.diagnosis.mainview.subview.calls.CallsViewModel.Filter;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public final class CallsViewController implements ISubController, SelectionListener, TraverseListener {

	@Autowired
	private Controller masterController;

	@Autowired
	private CallsViewModel model;

	@Autowired
	private CallsView view;

	@Override
	public ISubView getView() {
		return this.view;
	}

	@Override
	public void widgetSelected(final SelectionEvent e) {
		if (e.widget == this.view.getBtn1()) {
			this.model.setFilter(Filter.NONE);
		}
		if (e.widget == this.view.getBtn2()) {
			this.model.setFilter(Filter.JUST_FAILED);
		}
		if ((e.item != null) && (e.item.getData() instanceof OperationCall)) {
			this.model.setOperationCall((OperationCall) e.item.getData());
		}
	}

	@Override
	public void widgetDefaultSelected(final SelectionEvent e) {
		if (e.widget == this.view.getTable()) {
			this.masterController.jumpToCorrespondingTrace((OperationCall) e.item.getData());
		}
	}

	@Override
	public void keyTraversed(final TraverseEvent e) {
		if (e.widget == this.view.getFilterText()) {
			this.model.setRegExpr(this.view.getFilterText().getText());
		}
	}
}
