/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.frontend.tab.aggregatedmethods.atom;

import javafx.scene.control.TableRow;
import kieker.diagnosis.backend.data.AggregatedMethodCall;

/**
 * This is a row for a table which is aware of an {@link AggregatedMethodCall} being failed. If the method call has an
 * exception, it is styled accordingly.
 *
 * @author Nils Christian Ehmke
 */
public final class StyledRow extends TableRow<AggregatedMethodCall> {

	private final String styleClass;

	/**
	 * Creates a new instance of this class.
	 *
	 * @param styleClass
	 *            The style class which is added if the method call has an exception.
	 */
	public StyledRow( final String styleClass ) {
		this.styleClass = styleClass;
	}

	@Override
	protected void updateItem( final AggregatedMethodCall methodCall, final boolean empty ) {
		super.updateItem( methodCall, empty );

		getStyleClass( ).remove( styleClass );

		if ( methodCall != null && methodCall.getException( ) != null ) {
			getStyleClass( ).add( styleClass );
		}
	}

}
