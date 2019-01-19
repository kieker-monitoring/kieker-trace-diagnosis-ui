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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Enumeration;
import java.util.List;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.carrotsearch.hppc.IntObjectHashMap;
import com.carrotsearch.hppc.IntObjectMap;
import com.google.common.io.ByteStreams;

import kieker.diagnosis.backend.data.exception.CorruptStreamException;
import kieker.diagnosis.backend.data.exception.ImportFailedException;

public final class Reader {

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle( Reader.class.getName( ) );
	private static final Pattern MAPPING_FILE_PATTERN = Pattern.compile( "\\$(\\d*)=(.*)" );

	public void readRecursiveFromZipFile( final Path zipFile, final Repository repository ) throws IOException, CorruptStreamException, ImportFailedException {
		final Path directory = extractZipFileToTemporaryDirectory( zipFile );
		try {
			readRecursiveFromDirectory( directory, repository );
		} finally {
			deleteTemporaryDirectory( directory );
		}
	}

	private Path extractZipFileToTemporaryDirectory( final Path zipFilePath ) throws IOException {
		final Path temporaryDirectory = Files.createTempDirectory( "ktd" );

		try ( ZipFile zipFile = new ZipFile( zipFilePath.toFile( ) ) ) {
			final Enumeration<? extends ZipEntry> zipEntries = zipFile.entries( );
			while ( zipEntries.hasMoreElements( ) ) {
				final ZipEntry zipEntry = zipEntries.nextElement( );
				try ( InputStream inputStream = zipFile.getInputStream( zipEntry ) ) {
					try ( FileOutputStream outputStream = new FileOutputStream( new File( temporaryDirectory.toFile( ), zipEntry.getName( ) ) ) ) {
						ByteStreams.copy( inputStream, outputStream );
					}
				}
			}
		}

		return temporaryDirectory;
	}

	private void deleteTemporaryDirectory( final Path path ) throws IOException {
		if ( Files.isDirectory( path ) ) {
			final List<Path> children = Files.list( path ).collect( Collectors.toList( ) );
			if ( !children.isEmpty( ) ) {
				for ( final Path child : children ) {
					deleteTemporaryDirectory( child );
				}
			}
		}

		Files.delete( path );
	}

	public void readRecursiveFromDirectory( final Path directory, final Repository repository ) throws IOException, CorruptStreamException, ImportFailedException {
		repository.clear( );

		final List<Path> mappingFileContainingDirectories = getAllMappingFileContainingDirectories( directory );
		boolean anyFilesProcessed = !mappingFileContainingDirectories.isEmpty( );
		for ( final Path subDirectory : mappingFileContainingDirectories ) {
			final boolean filesProcessed = readFromDirectory( subDirectory, repository );
			anyFilesProcessed |= filesProcessed;
		}

		if ( !anyFilesProcessed ) {
			// No reader felt responsible for the import directory. We inform the user.
			throw new ImportFailedException( RESOURCE_BUNDLE.getString( "errorMessageUnknownMonitoringLog" ) );
		}

		repository.finish( );

		if ( repository.getIgnoredRecords( ) > 0 && repository.getTraceRoots( ).size( ) == 0 ) {
			// No traces have been reconstructed and records have been ignored. We inform the user.
			final String msg = String.format( RESOURCE_BUNDLE.getString( "errorMessageNoTraceAndRecordsIgnored" ), repository.getIgnoredRecords( ) );
			throw new ImportFailedException( msg );
		}
	}

	private List<Path> getAllMappingFileContainingDirectories( final Path directory ) throws IOException {
		try ( Stream<Path> stream = Files.walk( directory ) ) {
			return stream
					.filter( Files::isDirectory )
					.filter( this::containsMappingFile )
					.collect( Collectors.toList( ) );
		}
	}

	private boolean containsMappingFile( final Path directory ) {
		final Path mappingFilePath = getMappingFilePath( directory );
		return Files.isRegularFile( mappingFilePath );
	}

	private Path getMappingFilePath( final Path directory ) {
		return directory.resolve( "kieker.map" );
	}

	private boolean readFromDirectory( final Path directory, final Repository repository ) throws IOException, ImportFailedException {
		final IntObjectMap<String> mapping = readMappingFile( directory );
		repository.clearBeforeNextDirectory( );

		final List<Path> asciiFiles = getAllAsciiFilesFromDirectory( directory );
		final List<Path> binaryFiles = getAllBinaryFilesFromDirectory( directory );

		if ( !asciiFiles.isEmpty( ) ) {
			final AsciiReader asciiReader = new AsciiReader( mapping, repository );
			for ( final Path asciiFile : asciiFiles ) {
				asciiReader.readFromFile( asciiFile );
			}
		}

		if ( !binaryFiles.isEmpty( ) ) {
			final BinaryReader binaryReader = new BinaryReader( mapping, repository );
			for ( final Path binaryFile : binaryFiles ) {
				binaryReader.readFromFile( binaryFile, mapping );
			}
		}

		return !( asciiFiles.isEmpty( ) && binaryFiles.isEmpty( ) );
	}

	private List<Path> getAllAsciiFilesFromDirectory( final Path directory ) throws IOException {
		return Files.list( directory )
				.filter( Files::isRegularFile )
				.filter( this::isAsciiFile )
				.collect( Collectors.toList( ) );
	}

	private boolean isAsciiFile( final Path file ) {
		return file.toString( ).endsWith( ".dat" );
	}

	private List<Path> getAllBinaryFilesFromDirectory( final Path directory ) throws IOException {
		return Files.list( directory )
				.filter( Files::isRegularFile )
				.filter( this::isBinaryFile )
				.collect( Collectors.toList( ) );
	}

	private boolean isBinaryFile( final Path file ) {
		return file.toString( ).endsWith( ".bin" );
	}

	private IntObjectMap<String> readMappingFile( final Path directory ) throws IOException {
		final Path mappingFilePath = getMappingFilePath( directory );
		final List<String> lines = Files.readAllLines( mappingFilePath );

		final IntObjectMap<String> mapping = new IntObjectHashMap<>( lines.size( ) );
		lines.stream( )
				.parallel( )
				.map( MAPPING_FILE_PATTERN::matcher )
				.filter( Matcher::find )
				.sequential( )
				.forEach( matcher -> {
					final String key = matcher.group( 1 );
					final String value = matcher.group( 2 ).intern( );

					final int intKey = Integer.parseInt( key );
					mapping.put( intKey, value );
				} );

		return mapping;
	}

}
