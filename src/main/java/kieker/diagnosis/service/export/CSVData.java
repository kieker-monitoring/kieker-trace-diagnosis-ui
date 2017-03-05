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

package kieker.diagnosis.service.export;

import java.util.Arrays;

/**
 * @author Nils Christian Ehmke
 */
public final class CSVData {

	private final String[] ivHeader;
	private final String[][] ivRows;

	public CSVData( final String[] aHeader, final String[][] aRows ) {
		ivHeader = Arrays.copyOf( aHeader, aHeader.length );
		ivRows = copyArray( aRows );
	}

	public String[] getHeader( ) {
		return Arrays.copyOf( ivHeader, ivHeader.length );
	}

	public String[][] getRows( ) {
		return copyArray( ivRows );
	}

	private String[][] copyArray( final String[][] aRows ) {
		final String[][] rows = new String[aRows.length][];

		for ( int i = 0; i < aRows.length; i++ ) {
			rows[i] = Arrays.copyOf( aRows[i], aRows[i].length );
		}

		return rows;
	}

}
