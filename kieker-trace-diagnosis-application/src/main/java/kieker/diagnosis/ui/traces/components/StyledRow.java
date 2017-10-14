package kieker.diagnosis.ui.traces.components;

import javafx.scene.control.TreeTableRow;
import kieker.diagnosis.service.data.MethodCall;

/**
 * This is a row for a tree table which is aware of a method call being failed. If the method call has an exception, it is styled accordingly.
 *
 * @author Nils Christian Ehmke
 */
public final class StyledRow extends TreeTableRow<MethodCall> {

	@Override
	protected void updateItem( final MethodCall aItem, final boolean aEmpty ) {
		super.updateItem( aItem, aEmpty );

		// Remove a potential style class from an earlier run
		getStyleClass( ).remove( "failed" );

		if ( aItem != null && aItem.getException( ) != null ) {
			getStyleClass( ).add( "failed" );
		}
	}

}
