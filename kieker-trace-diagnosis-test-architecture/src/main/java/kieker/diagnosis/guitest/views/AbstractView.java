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

package kieker.diagnosis.guitest.views;

import javafx.scene.Node;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.google.common.base.Predicate;

import kieker.diagnosis.guitest.components.AbstractGuiComponent;

/**
 * @author Nils Christian Ehmke
 */
public abstract class AbstractView {

	@Autowired
	private BeanFactory ivBeanFactory;

	protected final <T extends AbstractGuiComponent> T getComponent( final Class<T> aComponentClass, final String aId ) {
		return ivBeanFactory.getBean( aComponentClass, aId );
	}

	protected final <T extends AbstractGuiComponent> T getComponent( final Class<T> aComponentClass, final Predicate<Node> aPredicate ) {
		return ivBeanFactory.getBean( aComponentClass, aPredicate );
	}

}
