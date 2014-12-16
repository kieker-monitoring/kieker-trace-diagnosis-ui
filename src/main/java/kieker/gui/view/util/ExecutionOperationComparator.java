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

package kieker.gui.view.util;

import kieker.gui.model.domain.AbstractExecution;

import org.eclipse.swt.SWT;

public class ExecutionOperationComparator extends AbstractDirectedComparator<AbstractExecution<?>> {

	@Override
	public int compare(final AbstractExecution<?> fst, final AbstractExecution<?> snd) {
		int result = fst.getOperation().compareTo(snd.getOperation());
		if (this.getDirection() == SWT.UP) {
			result = -result;
		}
		return result;
	}

}
