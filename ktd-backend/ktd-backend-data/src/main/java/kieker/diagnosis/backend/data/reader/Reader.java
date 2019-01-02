/***************************************************************************
 * Copyright 2015-2019 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.data.reader;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;

import kieker.diagnosis.backend.monitoring.MonitoringProbe;
import kieker.diagnosis.backend.monitoring.MonitoringUtil;

/**
 * This is an abstract base for readers, which import monitoring logs. It provides some convenient helper methods.
 *
 * @author Nils Christian Ehmke
 */
public abstract class Reader {

	private static final Pattern MAPPING_FILE_PATTERN = Pattern.compile( "\\$(\\d*)=(.*)" );

	protected final TemporaryRepository temporaryRepository;

	public Reader( final TemporaryRepository temporaryRepository ) {
		this.temporaryRepository = temporaryRepository;
	}

	public abstract void readFromDirectory( final File directory ) throws IOException;

	public abstract boolean shouldBeExecuted( final File directory ) throws IOException;

	/**
	 * Reads the Kieker mapping file from the given directory. If the directory contains no such mapping file, an empty
	 * map is returned.
	 *
	 * @param aDirectory
	 *            The directory from which the mapping file should be read.
	 *
	 * @return The mapping between the keys and the Strings.
	 *
	 * @throws IOException
	 *             If the mapping file exists but could not be read.
	 */
	protected final IntObjectMap<String> readMappingFile( final File directory ) throws IOException {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "readMappingFile(java.io.File)" );

		try {
			final IntObjectMap<String> stringMapping = new IntObjectHashMap<>( );

			// Check if the file exists
			final File mappingFile = new File( directory, "kieker.map" );
			if ( mappingFile.exists( ) ) {
				final List<String> lines = Files.readAllLines( mappingFile.toPath( ) );

				for ( final String line : lines ) {
					final Matcher matcher = MAPPING_FILE_PATTERN.matcher( line );

					if ( matcher.find( ) ) {
						// Split the line into key and value
						final String key = matcher.group( 1 );
						final String value = matcher.group( 2 ).intern( );

						// Store the entry in our map
						final int intKey = Integer.parseInt( key );
						stringMapping.put( intKey, value );
					}
				}
			}

			return stringMapping;
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	/**
	 * Delivers a list of all directories, which contain at least one file for each of the given extensions. The search
	 * is performed recursive.
	 *
	 * @param aDirectory
	 *            The root directory.
	 * @param aExtensions
	 *            The extensions to search for.
	 *
	 * @return The resulting list of directories.
	 *
	 * @throws IOException
	 *             If the list of directories could not be determined.
	 */
	protected List<File> findDirectoriesContainingFilesWithExtensions( final File directory, final String... extensions ) throws IOException {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "findDirectoriesContainingFilesWithExtensions(java.io.File, java.lang.String[])" );

		try {
			return Files.walk( directory.toPath( ), Integer.MAX_VALUE, new FileVisitOption[0] ) // Visit all files in
																								// infinite depth...
					.map( path -> path.toFile( ) ).filter( file -> file.isDirectory( ) ) // ...which are directories...
					.filter( dir -> containsFilesWithAllExtensions( dir, extensions ) ) // ...and contain at least one
																						// file for each extension.
					.collect( Collectors.toList( ) );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

	private boolean containsFilesWithAllExtensions( final File directory, final String[] extensions ) {
		for ( final String extension : extensions ) {
			final String lowerExtension = extension.toLowerCase( );
			final File[] filesWithExtension = directory.listFiles( (FilenameFilter) ( aDir, aName ) -> aName.toLowerCase( ).endsWith( lowerExtension ) );

			// Did we find a file with the extension?
			if ( filesWithExtension.length == 0 ) {
				return false;
			}
		}

		// It seems that we found a file for each extension.
		return true;
	}

	/**
	 * Delivers a list of all files with the given extension. The search is performed non-recursive.
	 *
	 * @param aDirectory
	 *            The root directory.
	 * @param aExtension
	 *            The extension to search for.
	 *
	 * @return The resulting list of files.
	 *
	 * @throws IOException
	 *             If the list of files could not be determined.
	 */
	protected File[] findFilesWithExtension( final File directory, final String extension ) throws IOException {
		final MonitoringProbe probe = MonitoringUtil.createMonitoringProbe( getClass( ), "findFilesWithExtension(java.io.File, java.lang.String)" );

		try {
			final String lowerExtension = extension.toLowerCase( );
			return directory.listFiles( (FilenameFilter) ( aDir, aName ) -> aName.toLowerCase( ).endsWith( lowerExtension ) );
		} catch ( final Throwable t ) {
			probe.fail( t );
			throw t;
		} finally {
			probe.stop( );
		}
	}

}
