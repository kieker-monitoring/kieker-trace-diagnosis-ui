package kieker.diagnosis.frontend.base.mixin;

import javafx.scene.Node;
import javafx.scene.control.Label;

public interface IconMixin {

	/**
	 * This method creates an icon which can for instance be used as a graphic for a node.
	 *
	 * @param aIcon
	 *            The type of the icon.
	 *
	 * @return A new icon.
	 */
	default Node createIcon( final Icon aIcon ) {
		final Label label = new Label( );

		label.setText( aIcon.getUnicode( ) );
		label.getStyleClass( ).add( "font-awesome-icon" );

		return label;
	}
	
	public enum Icon {

		SEARCH( "\uf002" ),
		FOLDER_OPEN( "\uf07c" ),
		ZIP_ARCHIVE( "\uf1c6" ),
		TIMES( "\uf00d" ),
		COGS( "\uf085" ),
		QUESTION_CIRCLE( "\uf059" ),
		INFO_CIRCLE( "\uf05a" ),
		CHART( "\uf080" );

		private final String unicode;

		private Icon( final String unicode ) {
			this.unicode = unicode;
		}

		public String getUnicode( ) {
			return unicode;
		}

	}
	
}
