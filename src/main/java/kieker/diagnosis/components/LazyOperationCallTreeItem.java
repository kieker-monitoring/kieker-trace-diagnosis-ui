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

package kieker.diagnosis.components;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.control.TreeItem;
import kieker.diagnosis.domain.OperationCall;
import kieker.diagnosis.model.PropertiesModel;

/**
 * @author Nils Christian Ehmke
 */
public final class LazyOperationCallTreeItem extends AbstractLazyOperationCallTreeItem<OperationCall> {

	public LazyOperationCallTreeItem(final OperationCall value) {
		super(value);
	}

	protected void initializeChildren() {
		final List<TreeItem<OperationCall>> result = new ArrayList<>();

		
		if (PropertiesModel.getInstance().isMethodCallAggregationActive()) {
			final float threshold = PropertiesModel.getInstance().getMethodCallAggregationThreshold();
			final List<Float> underThreshold = new ArrayList<>();
			for (final OperationCall child : super.getValue().getChildren()) {
				if (child.getPercent() < threshold) {
					underThreshold.add(child.getPercent());
				} else {
					result.add(new LazyOperationCallTreeItem(child));
				}
			}
			if (!underThreshold.isEmpty()) {
				final double percent = underThreshold.stream().collect(Collectors.summingDouble(Float::doubleValue));
				final OperationCall call = new OperationCall("", "", underThreshold.size() + " Methodenaufrufe zusammengefasst", super.getValue().getTraceID(), 0);
				call.setPercent((float) percent);
				result.add(new LazyOperationCallTreeItem(call));
			}
			
		} else {
			for (final OperationCall child : super.getValue().getChildren()) {
				result.add(new LazyOperationCallTreeItem(child));
			}
		}
		
		super.getChildren().setAll(result);
	}
}
