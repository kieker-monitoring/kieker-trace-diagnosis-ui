/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.FloatStringConverter;

/**
 * This is an implementation of a {@link TextField} which allows only to enter {@code float} values.
 *
 * @author Nils Christian Ehmke
 */
public final class FloatTextField extends TextField {

	private final ObjectProperty<Float> ivValueProperty = new SimpleObjectProperty<>( );

	public FloatTextField( ) {
		// We combine a converter with a filter. The converter will make sure that only valid numbers are in the field once it looses focus. The filter will
		// make sure that only numbers can be entered in the first place. As the pattern requires us also to add a minus sign though, we cannot control
		// everything just with the filter.
		setTextFormatter( new TextFormatter<>( new FloatStringConverter( ), null, new NumericFloatingPointFilter( ) ) );
		textProperty( ).bindBidirectional( ivValueProperty, new FloatStringConverter( ) );
	}

	/**
	 * Sets the value of the text field.
	 *
	 * @param aValue The new value.
	 */
	public void setValue( final Float aValue ) {
		ivValueProperty.set( aValue );
	}

	/**
	 * Delivers the current value of the text field.
	 *
	 * @return The current value.
	 */
	public Float getValue( ) {
		return ivValueProperty.get( );
	}

	public ObjectProperty<Float> valueProperty( ) {
		return ivValueProperty;
	}

}
