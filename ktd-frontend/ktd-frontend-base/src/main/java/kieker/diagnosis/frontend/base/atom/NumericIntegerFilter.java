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

package kieker.diagnosis.frontend.base.atom;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter.Change;

/**
 * A simple filter which accepts only numeric integer values (and a single minus sign).
 *
 * @author Nils Christian Ehmke
 */
final class NumericIntegerFilter implements UnaryOperator<Change> {

	private static final Pattern NUMMERIC_PATTERN = Pattern.compile( "(-)?\\d*" );

	@Override
	public Change apply( final Change change ) {
		if ( onlyNumeric( change.getControlNewText( ) ) ) {
			return change;
		} else {
			return null;
		}
	}

	private boolean onlyNumeric( final String text ) {
		return NUMMERIC_PATTERN.matcher( text ).matches( );
	}

}
