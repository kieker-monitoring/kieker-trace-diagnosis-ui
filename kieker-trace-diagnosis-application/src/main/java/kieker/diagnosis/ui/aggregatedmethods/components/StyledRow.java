package kieker.diagnosis.ui.aggregatedmethods.components;

import javafx.scene.control.TableRow;
import kieker.diagnosis.service.data.AggregatedMethodCall;

public final class StyledRow extends TableRow<AggregatedMethodCall> {

	@Override
	protected void updateItem( final AggregatedMethodCall aItem, final boolean aEmpty ) {
		super.updateItem( aItem, aEmpty );

		getStyleClass( ).remove( "failed" );

		if ( aItem != null && aItem.getException( ) != null ) {
			getStyleClass( ).add( "failed" );
		}
	}

}
