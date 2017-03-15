/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.application.gui.components.treetable;

import kieker.diagnosis.application.service.data.domain.AggregatedOperationCall;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.control.TreeItem;

/**
 * @author Nils Christian Ehmke
 */
public final class LazyAggregatedOperationCallTreeItem extends AbstractLazyOperationCallTreeItem<AggregatedOperationCall> {

	public LazyAggregatedOperationCallTreeItem( final AggregatedOperationCall aValue ) {
		super( aValue );
	}

	@Override
	protected void initializeChildren( ) {
		final List<TreeItem<AggregatedOperationCall>> result = new ArrayList<>( );

		for ( final AggregatedOperationCall child : super.getValue( ).getChildren( ) ) {
			result.add( new LazyAggregatedOperationCallTreeItem( child ) );
		}

		super.getChildren( ).setAll( result );
	}

}
