package kieker.diagnosis.architecture.ui.components;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

import javafx.scene.control.TextFormatter.Change;

/**
 * A simple filter which accepts only numeric integer values (and a single minus sign).
 *
 * @author Nils Christian Ehmke
 */
final class NumericIntegerFilter implements UnaryOperator<Change> {

	private static final Pattern cvNumericPattern = Pattern.compile( "(-)?\\d*" );

	@Override
	public Change apply( final Change aChange ) {
		if ( onlyNumeric( aChange.getControlNewText( ) ) ) {
			return aChange;
		} else {
			return null;
		}
	}

	private boolean onlyNumeric( final String aText ) {
		return cvNumericPattern.matcher( aText ).matches( );
	}

}