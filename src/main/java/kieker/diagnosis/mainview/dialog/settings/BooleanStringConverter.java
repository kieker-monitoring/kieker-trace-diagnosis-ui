package kieker.diagnosis.mainview.dialog.settings;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.util.StringConverter;
import kieker.diagnosis.common.Mapper;

public final class BooleanStringConverter extends StringConverter<Boolean> {

	private static Mapper<Boolean, String> booleanMapper;

	static {
		final Class<SettingsDialogViewController> controllerClass = SettingsDialogViewController.class;
		final String baseName = controllerClass.getCanonicalName().replace("Controller", "");
		final String bundleBaseName = "locale." + baseName.toLowerCase(Locale.ROOT);
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());

		final String yes = resourceBundle.getString("SettingsDialog.yes");
		final String no = resourceBundle.getString("SettingsDialog.no");

		BooleanStringConverter.booleanMapper = new Mapper<>();
		BooleanStringConverter.booleanMapper.map(Boolean.TRUE).to(yes);
		BooleanStringConverter.booleanMapper.map(Boolean.FALSE).to(no);
	}

	@Override
	public Boolean fromString(final String string) {
		return BooleanStringConverter.booleanMapper.invertedResolve(string);
	}

	@Override
	public String toString(final Boolean object) {
		return BooleanStringConverter.booleanMapper.resolve(object);
	}

}