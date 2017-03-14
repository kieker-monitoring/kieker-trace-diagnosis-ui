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

package kieker.diagnosis.application.gui.components.converter;

import kieker.diagnosis.architecture.util.Mapper;

import java.util.Locale;
import java.util.ResourceBundle;

import javafx.util.StringConverter;

/**
 * @author Nils Christian Ehmke
 *
 * @param <T>
 *            The type of the objects to convert from and to {@link String}.
 */
public abstract class AbstractStringConverter<T> extends StringConverter<T> {

	private final Mapper<T, String> ivMapper = new Mapper<>( );

	public AbstractStringConverter( ) {
		final String bundleBaseName = "kieker.diagnosis.application.gui.components.components";
		final ResourceBundle resourceBundle = ResourceBundle.getBundle( bundleBaseName, Locale.getDefault( ) );

		fillMapper( ivMapper, resourceBundle );
	}

	protected abstract void fillMapper( final Mapper<T, String> aMapper, final ResourceBundle aResourceBundle );

	@Override
	public final T fromString( final String aString ) {
		return ivMapper.invertedResolve( aString );
	}

	@Override
	public final String toString( final T aObject ) {
		return ivMapper.resolve( aObject );
	}

}
