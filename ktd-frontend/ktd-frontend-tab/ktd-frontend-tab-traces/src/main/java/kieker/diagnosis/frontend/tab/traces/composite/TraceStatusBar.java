package kieker.diagnosis.frontend.tab.traces.composite;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

public final class TraceStatusBar extends HBox {

	private final Label status;

	public TraceStatusBar( ) {
		status = new Label( );
		status.setMaxWidth( Double.POSITIVE_INFINITY );
		HBox.setHgrow( status, Priority.ALWAYS );
		HBox.setMargin( status, new Insets( 5, 0, 0, 0 ) );

		getChildren( ).add( status );
	}

	/**
	 * Sets the value which is shown in the component.
	 *
	 * @param value
	 *            The new value. Can also be {@code null}.
	 */
	public void setValue( final String value ) {
		status.setText( value );
	}

}
