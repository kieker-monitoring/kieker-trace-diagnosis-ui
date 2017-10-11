package kieker.diagnosis.test.ui.main;

import org.testfx.api.FxRobot;

import kieker.diagnosis.test.architecture.ui.Button;

public class OkCancelDialog {

	private final Button ivOkButton;
	private final Button ivCancelButton;

	public OkCancelDialog( final FxRobot aRobot ) {
		ivOkButton = new Button( aRobot, "OK" );
		ivCancelButton = new Button( aRobot, "Cancel" );
	}

	public Button getOkButton( ) {
		return ivOkButton;
	}

	public Button getCancelButton( ) {
		return ivCancelButton;
	}

}
