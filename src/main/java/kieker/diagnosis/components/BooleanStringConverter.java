package kieker.diagnosis.components;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.util.StringConverter;
import kieker.diagnosis.util.Mapper;

public final class BooleanStringConverter extends StringConverter<Boolean> {

	private static Mapper<Boolean, String> booleanMapper;

	static {
		final String bundleBaseName = "locale.kieker.diagnosis.components.components";
		final ResourceBundle resourceBundle = ResourceBundle.getBundle(bundleBaseName, Locale.getDefault());

		final String yes = resourceBundle.getString("yes");
		final String no = resourceBundle.getString("no");

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