package kieker.diagnosis.gui.bugreporting;

import java.io.IOException;
import java.net.URISyntaxException;

public interface BugReportingDialogControllerIfc {

	void visitGitLab( ) throws IOException, URISyntaxException;

	void visitTrac( ) throws IOException, URISyntaxException;

	void closeDialog( );

}