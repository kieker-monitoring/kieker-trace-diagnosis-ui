/***************************************************************************
 * Copyright 2014 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.model;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.TimeUnit;

import kieker.diagnosis.model.PropertiesModel;

import org.junit.Test;

public final class PropertiesModelTest {

	@Test
	public void transactionalSettingShouldWork() {
		final BooleanObserver observer = new BooleanObserver();
		final PropertiesModel model = new PropertiesModel();
		model.addObserver(observer);

		model.startModification();

		model.setTimeUnit(TimeUnit.NANOSECONDS);
		assertThat(observer.isFlag(), is(false));

		model.commitModification();

		assertThat(observer.isFlag(), is(true));
	}

	@Test
	public void usualSettingShouldNotifyObservers() {
		final BooleanObserver observer = new BooleanObserver();
		final PropertiesModel model = new PropertiesModel();
		model.addObserver(observer);

		model.setTimeUnit(TimeUnit.NANOSECONDS);
		assertThat(observer.isFlag(), is(true));
	}

	@Test
	public void settingShouldBePersisted() {
		final PropertiesModel fstModel = new PropertiesModel();
		fstModel.setTimeUnit(TimeUnit.NANOSECONDS);
		fstModel.setTimeUnit(TimeUnit.MICROSECONDS);

		final PropertiesModel sndModel = new PropertiesModel();
		assertThat(sndModel.getTimeUnit(), is(TimeUnit.MICROSECONDS));
	}

	private static class BooleanObserver implements Observer {

		private boolean flag = false;

		@Override
		public void update(final Observable o, final Object arg) {
			this.flag = true;
		}

		public boolean isFlag() {
			return this.flag;
		}

	}

}
