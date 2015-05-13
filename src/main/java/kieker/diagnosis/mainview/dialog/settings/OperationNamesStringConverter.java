package kieker.diagnosis.mainview.dialog.settings;

import javafx.util.StringConverter;
import kieker.diagnosis.model.PropertiesModel.OperationNames;

public class OperationNamesStringConverter extends StringConverter<OperationNames> {

	@Override
	public String toString(final OperationNames object) {
		return (object == OperationNames.SHORT) ? "getBook(...)" : "public void kieker.examples.bookstore.Catalog.getBook(boolean)";
	}

	@Override
	public OperationNames fromString(final String string) {
		return ("getBook(...)".equals(string)) ? OperationNames.SHORT : OperationNames.LONG;
	}

}
