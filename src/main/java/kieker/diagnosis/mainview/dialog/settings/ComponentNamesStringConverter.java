package kieker.diagnosis.mainview.dialog.settings;

import javafx.util.StringConverter;
import kieker.diagnosis.model.PropertiesModel.ComponentNames;

public class ComponentNamesStringConverter extends StringConverter<ComponentNames> {

	@Override
	public String toString(final ComponentNames object) {
		return (object == ComponentNames.SHORT) ? "Catalog" : "kieker.examples.bookstore.Catalog";
	}

	@Override
	public ComponentNames fromString(final String string) {
		return ("Catalog".equals(string)) ? ComponentNames.SHORT : ComponentNames.LONG;
	}

}
