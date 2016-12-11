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

package kieker.diagnosis.util.stages;

import java.io.File;

import kieker.common.record.IMonitoringRecord;
import teetime.framework.AbstractCompositeStage;
import teetime.framework.OutputPort;
import teetime.stage.InitialElementProducer;
import teetime.stage.className.ClassNameRegistryRepository;
import teetime.stage.io.filesystem.Dir2RecordsFilter;

/**
 * This is a composite stage which deserializes monitoring records from a specific directory and forwards them to the output port.
 *
 * @author Nils Christian Ehmke
 */
public final class ReadingComposite extends AbstractCompositeStage {

	private final InitialElementProducer<File> ivProducer;
	private final Dir2RecordsFilter ivReader;

	public ReadingComposite(final File aImportDirectory) {
		this.ivProducer = new InitialElementProducer<>(aImportDirectory);
		this.ivReader = new Dir2RecordsFilter(new ClassNameRegistryRepository());

		super.connectPorts(this.ivProducer.getOutputPort(), this.ivReader.getInputPort());
	}

	public OutputPort<IMonitoringRecord> getOutputPort() {
		return this.ivReader.getOutputPort();
	}

}
