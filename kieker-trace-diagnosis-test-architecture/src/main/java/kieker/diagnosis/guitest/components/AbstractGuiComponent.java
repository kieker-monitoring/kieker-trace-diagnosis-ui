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

package kieker.diagnosis.guitest.components;

import javafx.scene.Node;

import org.springframework.beans.factory.annotation.Autowired;
import org.testfx.api.FxRobot;

import com.google.common.base.Predicate;

/**
 * @author Nils Christian Ehmke
 */
public abstract class AbstractGuiComponent {

	@Autowired
	private FxRobot ivFxRobot;

	private final Predicate<Node> ivPredicate;

	public AbstractGuiComponent( final String aId ) {
		ivPredicate = node -> aId.equals( node.getId( ) );
	}

	public AbstractGuiComponent( final Predicate<Node> aMatcher ) {
		ivPredicate = aMatcher;
	}

	public final boolean isEnabled( ) {
		return !isDisabled( );
	}

	public final boolean isDisabled( ) {
		return getNode( ).isDisabled( );
	}

	public final void click( ) {
		getFxRobot( ).clickOn( getPredicate( ) );
	}

	protected final Node getNode( ) {
		return getFxRobot( ).lookup( getPredicate( ) ).queryFirst( );
	}

	protected final FxRobot getFxRobot( ) {
		return ivFxRobot;
	}

	protected final Predicate<Node> getPredicate( ) {
		return ivPredicate;
	}

}
