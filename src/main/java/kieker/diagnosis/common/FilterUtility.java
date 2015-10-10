package kieker.diagnosis.common;

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.control.TextField;
import kieker.diagnosis.model.PropertiesModel;

public final class FilterUtility {

	private FilterUtility() {}
	
	public static <T> Predicate<T> useFilter(final TextField filter, final Function<T, String> function) {
		final String text = filter.getText();

		if ((text == null) || text.isEmpty()) {
			return (x -> true);
		} else {
			final boolean regularExpressionsActive = PropertiesModel.getInstance().isActivateRegularExpressions();
			if (regularExpressionsActive) {
				return (x -> function.apply(x).matches(text));
			} else {
				return (x -> function.apply(x).toLowerCase().contains(text.toLowerCase()));
			}
		}
	}
	
}
