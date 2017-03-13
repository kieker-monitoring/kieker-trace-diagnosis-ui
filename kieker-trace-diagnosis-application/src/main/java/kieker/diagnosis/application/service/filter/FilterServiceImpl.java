/***************************************************************************
 * Copyright 2015-2016 Kieker Project (http://kieker-monitoring.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ***************************************************************************/

package kieker.diagnosis.application.service.filter;

import kieker.diagnosis.application.service.data.DataService;
import kieker.diagnosis.application.service.data.domain.AbstractOperationCall;
import kieker.diagnosis.application.service.properties.CaseSensitiveProperty;
import kieker.diagnosis.application.service.properties.RegularExpressionsProperty;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jfxtras.scene.control.CalendarTimeTextField;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class FilterServiceImpl implements FilterService {

	@Autowired
	private PropertiesService ivPropertiesService;

	@Autowired
	private DataService ivDataService;

	@Override
	public <T> Predicate<T> useFilter( final TextField aFilter, final Function<T, String> aFunction ) {
		final String text = aFilter.getText( );

		if ( ( text == null ) || text.isEmpty( ) ) {
			return x -> true;
		} else {
			final boolean regularExpressionsActive = ivPropertiesService.loadBooleanApplicationProperty( RegularExpressionsProperty.class );
			if ( regularExpressionsActive ) {
				checkRegularExpression( text );
				return x -> aFunction.apply( x ).matches( text );
			} else {
				final boolean caseSensitivityActive = ivPropertiesService.loadBooleanApplicationProperty( CaseSensitiveProperty.class );
				if ( caseSensitivityActive ) {
					return x -> aFunction.apply( x ).contains( text );
				} else {
					return x -> aFunction.apply( x ).toLowerCase( Locale.getDefault( ) ).contains( text.toLowerCase( Locale.getDefault( ) ) );
				}
			}
		}
	}

	@Override
	public <T extends AbstractOperationCall<T>> Predicate<T> useFilter( final TextField aFilter, final Function<T, String> aFunction,
			final boolean aSearchInChildren ) {
		final Predicate<T> filter = useFilter( aFilter, aFunction );

		if ( aSearchInChildren ) {
			return x -> {
				return checkRecursive( x, filter );
			};
		} else {
			return filter;
		}
	}

	private <T extends AbstractOperationCall<T>> boolean checkRecursive( final T aElement, final Predicate<T> aPredicate ) {
		if ( aPredicate.test( aElement ) ) {
			return true;
		} else {
			for ( final T child : aElement.getChildren( ) ) {
				if ( checkRecursive( child, aPredicate ) ) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public <T> Predicate<T> alwaysTrue( ) {
		return x -> true;
	}

	private void checkRegularExpression( final String aText ) {
		Pattern.compile( aText );
	}

	@Override
	public <T> Predicate<T> useFilter( final DatePicker aDatePicker, final Function<T, Long> aFunction, final boolean aFilterBefore ) {
		final LocalDate value = aDatePicker.getValue( );
		if ( value == null ) {
			return x -> true;
		}

		return x -> {
			final long timestamp = aFunction.apply( x );
			final long timestampInMS = TimeUnit.MILLISECONDS.convert( timestamp, ivDataService.getTimeUnit( ) );
			final Instant instant = Instant.ofEpochMilli( timestampInMS );
			final ZonedDateTime zonedDateTime = ZonedDateTime.ofInstant( instant, ZoneId.systemDefault( ) );
			final LocalDate localDate = LocalDate.from( zonedDateTime );
			if ( aFilterBefore ) {
				return value.isBefore( localDate ) || value.isEqual( localDate );
			} else {
				return value.isAfter( localDate ) || value.isEqual( localDate );
			}
		};
	}

	@Override
	public <T extends AbstractOperationCall<T>> Predicate<T> useFilter( final DatePicker aDatePicker, final Function<T, Long> aFunction,
			final boolean aFilterBefore, final boolean aSearchInChildren ) {
		final Predicate<T> filter = useFilter( aDatePicker, aFunction, aFilterBefore );

		if ( aSearchInChildren ) {
			return x -> {
				return checkRecursive( x, filter );
			};
		} else {
			return filter;
		}
	}

	@Override
	public <T extends AbstractOperationCall<T>> Predicate<T> useFilter( final CalendarTimeTextField aTimeTextField, final Function<T, Long> aFunction,
			final boolean aFilterBefore, final boolean aSearchInChildren ) {
		final Predicate<T> filter = useFilter( aTimeTextField, aFunction, aFilterBefore );

		if ( aSearchInChildren ) {
			return x -> {
				return checkRecursive( x, filter );
			};
		} else {
			return filter;
		}
	}

	@Override
	public <T> Predicate<T> useFilter( final CalendarTimeTextField aTimeTextField, final Function<T, Long> aFunction, final boolean aFilterBefore ) {
		final Calendar calendar = aTimeTextField.getCalendar( );

		if ( calendar == null ) {
			return x -> true;
		}

		final TimeUnit destinationTimeUnit = ivDataService.getTimeUnit( );

		if ( aFilterBefore ) {
			return new BeforeTimeFilter<>( calendar, aFunction, destinationTimeUnit );
		} else {
			return new AfterTimeFilter<>( calendar, aFunction, destinationTimeUnit );
		}
	}

	@Override
	public <T> Predicate<T> useFilter( final RadioButton aShowAllButton, final RadioButton aShowJustSuccessfulButton, final RadioButton aShowJustFailedButton,
			final Function<T, Boolean> aIsFailedFunction ) {
		if ( aShowAllButton.isSelected( ) ) {
			return x -> true;
		}
		Predicate<T> predicate = x -> aIsFailedFunction.apply( x );
		if ( aShowJustSuccessfulButton.isSelected( ) ) {
			predicate = predicate.negate( );
		}
		return predicate;
	}

	@Override
	public <T> Predicate<T> useFilter( final RadioButton aShowAllButton, final RadioButton aShowJustSuccessfulButton, final RadioButton aShowJustFailedButton,
			final RadioButton aShowJustFailureContainingButton, final Function<T, Boolean> aIsFailedFunction,
			final Function<T, Boolean> aContainsFailureFunction ) {
		if ( aShowAllButton.isSelected( ) ) {
			return x -> true;
		}
		if ( aShowJustFailureContainingButton.isSelected( ) ) {
			return x -> aContainsFailureFunction.apply( x );
		}
		Predicate<T> predicate = x -> aIsFailedFunction.apply( x );
		if ( aShowJustSuccessfulButton.isSelected( ) ) {
			predicate = predicate.negate( );
		}
		return predicate;
	}

}
