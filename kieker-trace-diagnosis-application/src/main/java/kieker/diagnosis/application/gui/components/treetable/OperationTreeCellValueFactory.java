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

package kieker.diagnosis.application.gui.components.treetable;

import kieker.diagnosis.application.service.nameconverter.NameConverterService;
import kieker.diagnosis.application.service.properties.OperationNames;
import kieker.diagnosis.application.service.properties.OperationNamesProperty;
import kieker.diagnosis.architecture.gui.components.AutowireCandidate;
import kieker.diagnosis.architecture.service.properties.PropertiesService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import javafx.beans.NamedArg;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn.CellDataFeatures;
import javafx.util.Callback;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author Nils Christian Ehmke
 */
public final class OperationTreeCellValueFactory implements Callback<CellDataFeatures<?, String>, ObservableValue<String>>, AutowireCandidate {

	private static final Logger cvLogger = LoggerFactory.getLogger( DurationTreeCellValueFactory.class );

	@Autowired
	private NameConverterService ivNameConverterService;

	@Autowired
	private PropertiesService ivPropertiesService;

	private final String ivProperty;

	public OperationTreeCellValueFactory( @NamedArg ( value = "property" ) final String aProperty ) {
		ivProperty = aProperty.substring( 0, 1 ).toUpperCase( Locale.ROOT ) + aProperty.substring( 1 );
	}

	@Override
	public ObservableValue<String> call( final CellDataFeatures<?, String> aCall ) {
		try {
			final TreeItem<?> item = aCall.getValue( );
			final Method getter = item.getValue( ).getClass( ).getMethod( "get" + ivProperty, new Class<?>[0] );
			String operationName = (String) getter.invoke( item.getValue( ), new Object[0] );

			if ( ivPropertiesService.loadApplicationProperty( OperationNamesProperty.class ) == OperationNames.SHORT ) {
				operationName = ivNameConverterService.toShortOperationName( operationName );
			}

			return new ReadOnlyObjectWrapper<>( operationName );
		} catch ( final NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex ) {
			OperationTreeCellValueFactory.cvLogger.warn( "Could not read property", ex );
			return null;
		}
	}

}
