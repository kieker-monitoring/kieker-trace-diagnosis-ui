/***************************************************************************
 * Copyright 2015-2023 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.data;

import java.nio.file.Path;
import java.util.ResourceBundle;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import kieker.diagnosis.backend.base.service.Service;
import kieker.diagnosis.backend.data.exception.CorruptStreamException;
import kieker.diagnosis.backend.data.exception.ImportFailedException;
import kieker.diagnosis.backend.data.reader.Reader;
import kieker.diagnosis.backend.data.reader.Repository;
import lombok.RequiredArgsConstructor;

/**
 * This is the service responsible for importing monitoring logs and holding the necessary data from the import.
 *
 * @author Nils Christian Ehmke
 */
@Singleton
@RequiredArgsConstructor ( onConstructor = @__ ( @Inject ) )
public class MonitoringLogService implements Service {

	private static final ResourceBundle RESOURCES = ResourceBundle.getBundle( MonitoringLogService.class.getName( ) );

	private final Repository repository;

	public void importMonitoringLog( final Path directoryOrFile, final ImportType type ) throws CorruptStreamException, ImportFailedException {
		final long tin = System.currentTimeMillis( );

		try {
			final Reader reader = new Reader( );

			switch ( type ) {
				case DIRECTORY:
					reader.readRecursiveFromDirectory( directoryOrFile, repository );
				break;
				case ZIP_FILE:
					reader.readRecursiveFromZipFile( directoryOrFile, repository );
				break;
				default:
				break;
			}

			repository.setDataAvailable( directoryOrFile, tin );
		} catch ( final CorruptStreamException ex ) {
			// This means, that something went wrong, but that the data is partially available
			repository.setDataAvailable( directoryOrFile, tin );

			throw ex;
		} catch ( final ImportFailedException ex ) {
			throw ex;
		} catch ( final Exception ex ) {
			throw new ImportFailedException( RESOURCES.getString( "errorMessageImportFailed" ), ex );
		}
	}

}
