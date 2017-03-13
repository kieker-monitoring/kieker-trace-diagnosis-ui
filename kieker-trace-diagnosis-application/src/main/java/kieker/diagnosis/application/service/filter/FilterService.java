package kieker.diagnosis.application.service.filter;

import kieker.diagnosis.application.service.data.domain.AbstractOperationCall;

import java.util.function.Function;
import java.util.function.Predicate;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import jfxtras.scene.control.CalendarTimeTextField;

public interface FilterService {

	public <T> Predicate<T> useFilter( TextField aFilter, Function<T, String> aFunction );

	public <T extends AbstractOperationCall<T>> Predicate<T> useFilter( TextField aFilter, Function<T, String> aFunction, boolean aSearchInChildren );

	public <T> Predicate<T> alwaysTrue( );

	public <T> Predicate<T> useFilter( DatePicker aDatePicker, Function<T, Long> aFunction, boolean aFilterBefore );

	public <T extends AbstractOperationCall<T>> Predicate<T> useFilter( DatePicker aDatePicker, Function<T, Long> aFunction, boolean aFilterBefore,
			boolean aSearchInChildren );

	public <T extends AbstractOperationCall<T>> Predicate<T> useFilter( CalendarTimeTextField aTimeTextField, Function<T, Long> aFunction,
			boolean aFilterBefore, boolean aSearchInChildren );

	public <T> Predicate<T> useFilter( CalendarTimeTextField aTimeTextField, Function<T, Long> aFunction, boolean aFilterBefore );

	public <T> Predicate<T> useFilter( RadioButton aShowAllButton, RadioButton aShowJustSuccessfulButton, RadioButton aShowJustFailedButton,
			Function<T, Boolean> aIsFailedFunction );

	public <T> Predicate<T> useFilter( RadioButton aShowAllButton, RadioButton aShowJustSuccessfulButton, RadioButton aShowJustFailedButton,
			RadioButton aShowJustFailureContainingButton, Function<T, Boolean> aIsFailedFunction, Function<T, Boolean> aContainsFailureFunction );

}
