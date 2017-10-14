package kieker.diagnosis.ui.methods.components;

import javafx.scene.control.TableRow;
import kieker.diagnosis.service.data.MethodCall;

public final class StyledRow extends TableRow<MethodCall> {

	@Override
	protected void updateItem( final MethodCall aItem, final boolean aEmpty ) {
		super.updateItem( aItem, aEmpty );

		getStyleClass( ).remove( "failed" );

		if ( aItem != null && aItem.getException( ) != null ) {
			getStyleClass( ).add( "failed" );
		}
	}

}
