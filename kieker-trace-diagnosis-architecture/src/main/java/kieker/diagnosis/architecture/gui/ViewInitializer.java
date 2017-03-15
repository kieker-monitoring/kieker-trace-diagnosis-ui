/***************************************************************************
 * Copyright 2015-2017 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.architecture.gui;

import kieker.diagnosis.architecture.gui.components.AutowireCandidate;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javafx.scene.Node;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TitledPane;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.layout.Pane;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanInitializationException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;

/**
 * @author Nils Christian Ehmke
 */
@Component
final class ViewInitializer {

	@Autowired
	private AutowireCapableBeanFactory ivBeanFactory;

	public void initialize( final AbstractView aView ) throws BeansException {
		final Node node = aView.getNode( );

		final Class<?> clazz = aView.getClass( );
		final Field[] declaredFields = clazz.getDeclaredFields( );

		// Autowire all annotated fields
		for ( final Field field : declaredFields ) {
			if ( field.isAnnotationPresent( AutowiredElement.class ) ) {
				final String lookupString = String.format( "#%s", field.getName( ) );
				final Node element = lookup( node, lookupString );

				if ( element == null ) {
					throw new BeanInitializationException( String.format( "Element '%s' could not be found", field.getName( ) ) );
				}

				try {
					field.setAccessible( true );
					field.set( aView, element );
				} catch ( IllegalArgumentException | IllegalAccessException ex ) {
					throw new BeanInitializationException( "Element could not be autowired", ex );
				}
			}
		}

		// There are some components (cell factories etc.) in the view, which should
		// be autowired in order for them to use services. We have to search for them.
		final List<Object> autowireCandidates = findAutowireCandidates( node );
		for ( final Object object : autowireCandidates ) {
			ivBeanFactory.autowireBean( object );
		}
	}

	private List<Object> findAutowireCandidates( final Node aNode ) {
		List<Object> candidates = new ArrayList<>( );

		if ( aNode instanceof Pane ) {
			for ( final Node child : ( (Pane) aNode ).getChildren( ) ) {
				candidates.addAll( findAutowireCandidates( child ) );
			}
		} else if ( aNode instanceof TableView ) {
			for ( final TableColumn<?, ?> tableColumn : ( (TableView<?>) aNode ).getColumns( ) ) {
				candidates.add( tableColumn.getCellFactory( ) );
				candidates.add( tableColumn.getCellValueFactory( ) );
			}
		} else if ( aNode instanceof TreeTableView ) {
			for ( final TreeTableColumn<?, ?> tableColumn : ( (TreeTableView<?>) aNode ).getColumns( ) ) {
				candidates.add( tableColumn.getCellFactory( ) );
				candidates.add( tableColumn.getCellValueFactory( ) );
			}
		}

		candidates = candidates.stream( ).filter( o -> ( o instanceof AutowireCandidate ) ).collect( Collectors.toList( ) );
		return candidates;
	}

	private static Node lookup( final Node aNode, final String aLookupString ) {
		Node element = aNode.lookup( aLookupString );

		// Did we already found the element?
		if ( element != null ) {
			return element;
		}

		if ( aNode instanceof Pane ) {
			final Pane pane = (Pane) aNode;

			for ( final Node child : pane.getChildren( ) ) {
				element = lookup( child, aLookupString );
				if ( element != null ) {
					break;
				}
			}
		} else if ( aNode instanceof TitledPane ) {
			final TitledPane titledPane = (TitledPane) aNode;
			final Node content = titledPane.getContent( );

			if ( content != null ) {
				element = lookup( content, aLookupString );
			}
		}

		return element;
	}

}
