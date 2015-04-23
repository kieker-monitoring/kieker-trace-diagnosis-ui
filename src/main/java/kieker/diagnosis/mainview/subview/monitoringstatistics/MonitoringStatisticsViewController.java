/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.mainview.subview.monitoringstatistics;

import java.io.File;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import kieker.diagnosis.model.DataModel;

/**
 * @author Nils Christian Ehmke
 */
public final class MonitoringStatisticsViewController {

	private static final String[] UNITS = { "Bytes", "Kilobytes", "Megabytes", "Gigabytes" };
	private static final float SIZE_OF_BYTE = 1024.0f;

	private final DataModel dataModel = DataModel.getInstance();

	@FXML private TextField monitoringlog;
	@FXML private TextField monitoringsize;

	public void initialize() {
		final ObjectProperty<File> importDirectory = this.dataModel.getImportDirectory();
		this.monitoringlog.textProperty().bind(Bindings.createStringBinding(() -> this.assemblePathString(importDirectory.get()), importDirectory));
		this.monitoringsize.textProperty().bind(Bindings.createStringBinding(() -> this.assembleSizeString(importDirectory.get()), importDirectory));
	}

	private String assemblePathString(final File file) {
		return (file == null) ? "N/A" : file.getAbsolutePath();
	}

	private String assembleSizeString(final File file) {
		String importDirectorySizeString = "N/A";
		if (file == null) {
			return importDirectorySizeString;
		}
		final float size = this.calculateDirectorySize(file);

		float newSize = size;
		for (final String unit : MonitoringStatisticsViewController.UNITS) {
			if (newSize >= MonitoringStatisticsViewController.SIZE_OF_BYTE) {
				newSize /= MonitoringStatisticsViewController.SIZE_OF_BYTE;
			} else {
				importDirectorySizeString = String.format("%.1f %s", newSize, unit);
				break;
			}
		}

		return importDirectorySizeString;
	}

	private long calculateDirectorySize(final File file) {
		if (file.isFile()) {
			return file.length();
		}

		long sum = 0;
		for (final File child : file.listFiles()) {
			sum += this.calculateDirectorySize(child);
		}
		return sum;
	}

}
