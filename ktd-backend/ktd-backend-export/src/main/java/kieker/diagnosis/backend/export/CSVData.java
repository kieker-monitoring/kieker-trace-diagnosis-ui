/***************************************************************************
 * Copyright 2015-2018 Kieker Project (http://kieker-monitoring.net)
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

package kieker.diagnosis.backend.export;

/**
 * This is a data transfer object holding the necessary data for a CSV export.
 *
 * @author Nils Christian Ehmke
 */
public final class CSVData {

	private String[] header;
	private String[][] values;

	public String[] getHeader( ) {
		return header;
	}

	public void setHeader( final String[] header ) {
		this.header = header;
	}

	public String[][] getValues( ) {
		return values;
	}

	public void setValues( final String[][] values ) {
		this.values = values;
	}

}
