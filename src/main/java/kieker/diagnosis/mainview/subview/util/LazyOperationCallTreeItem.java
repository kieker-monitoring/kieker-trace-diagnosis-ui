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

package kieker.diagnosis.mainview.subview.util;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;
import kieker.diagnosis.domain.AbstractOperationCall;

/**
 * @author Nils Christian Ehmke
 */
public final class LazyOperationCallTreeItem<T extends AbstractOperationCall<T>> extends TreeItem<T> {

	private boolean childrenInitialized = false;

	public LazyOperationCallTreeItem(final T value) {
		super(value);
	}

	@Override
	public ObservableList<TreeItem<T>> getChildren() {
		if (!this.childrenInitialized) {
			this.initializeChildren();
		}

		return super.getChildren();
	}

	@Override
	public boolean isLeaf() {
		return super.getValue().getChildren().isEmpty();
	}

	private void initializeChildren() {
		this.childrenInitialized = true;

		final List<TreeItem<T>> result = new ArrayList<>();

		for (final T child : super.getValue().getChildren()) {
			result.add(new LazyOperationCallTreeItem<T>(child));
		}

		super.getChildren().setAll(result);
	}
}
