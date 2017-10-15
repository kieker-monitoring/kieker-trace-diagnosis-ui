package kieker.diagnosis.architecture.ui.components;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.FloatStringConverter;

/**
 * This is an implementation of a {@link TextField} which allows only to enter {@code float} values.
 *
 * @author Nils Christian Ehmke
 */
public final class FloatTextField extends TextField {

	public FloatTextField( ) {
		// We combine a converter with a filter. The converter will make sure that only valid numbers are in the field once it looses focus. The filter will
		// make sure that only numbers can be entered in the first place. As the pattern requires us also to add a minus sign though, we cannot control
		// everything just with the filter.
		setTextFormatter( new TextFormatter<>( new FloatStringConverter( ), null, new NumericFloatingPointFilter( ) ) );
	}

	/**
	 * Sets the value of the text field.
	 *
	 * @param aLong
	 *            The new value.
	 */
	public void setValue( final Float aLong ) {
		setText( Float.toString( aLong ) );
	}

	/**
	 * Delivers the current value of the text field.
	 *
	 * @return The current value.
	 */
	public Float getValue( ) {
		// We still have to consider the case of an empty text
		final String text = getText( );
		if ( !text.isEmpty( ) ) {
			return Float.parseFloat( text );
		} else {
			return null;
		}
	}

}
