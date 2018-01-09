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

package kieker.diagnosis.architecture.ui.components;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

/**
 * This is an implementation of a {@link TextField} which allows only to enter {@code int} values.
 *
 * @author Nils Christian Ehmke
 */
public final class IntegerTextField extends TextField {

	public IntegerTextField( ) {
		// We combine a converter with a filter. The converter will make sure that only valid numbers are in the field once it looses focus. The filter will
		// make sure that only numbers can be entered in the first place. As the pattern requires us also to add a minus sign though, we cannot control
		// everything just with the filter.
		setTextFormatter( new TextFormatter<>( new IntegerStringConverter( ), null, new NumericIntegerFilter( ) ) );
	}

	/**
	 * Sets the value of the text field.
	 *
	 * @param aLong
	 *            The new value.
	 */
	public void setValue( final Integer aLong ) {
		setText( Integer.toString( aLong ) );
	}

	/**
	 * Delivers the current value of the text field.
	 *
	 * @return The current value.
	 */
	public Integer getValue( ) {
		// We still have to consider the case of an empty text
		final String text = getText( );
		if ( text != null && !text.isEmpty( ) ) {
			return Integer.parseInt( text );
		} else {
			return null;
		}
	}

}
