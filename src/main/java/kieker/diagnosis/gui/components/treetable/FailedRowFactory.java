/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.gui.components.treetable;

import javafx.scene.control.TreeTableRow;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;
import kieker.diagnosis.service.data.domain.AbstractOperationCall;

/**
 * @author Nils Christian Ehmke
 */
public class FailedRowFactory<S> implements Callback<TreeTableView<S>, TreeTableRow<S>> {

	@Override
	public TreeTableRow<S> call( final TreeTableView<S> param ) {
		return new FailedTreeTableRow( );
	}

	private class FailedTreeTableRow extends TreeTableRow<S> {

		@Override
		protected void updateItem( S item, boolean empty ) {
			super.updateItem( item, empty );

			if ( item instanceof AbstractOperationCall<?> ) {
				final AbstractOperationCall<?> call = (AbstractOperationCall<?>) item;

				getStyleClass( ).remove( "failed" );

				if ( call.isFailed( ) ) {
					getStyleClass( ).add( "failed" );
				}
			}
		}

	}

}