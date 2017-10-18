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

package kieker.diagnosis.service.settings;

/**
 * This enumeration represents the possible appearances of classes in the ui.
 *
 * @author Nils Christian Ehmke
 */
public enum ClassAppearance {

	/**
	 * A short representation. This means that only the simple name of the class is shown.
	 */
	SHORT,

	/**
	 * A long representation. This means that the full name of the class is shown.
	 */
	LONG;

	public String convert( final String aClass ) {
		String clazz = aClass;

		// This can only happen when the records contains null values. Ugly but possible.
		if ( clazz == null ) {
			return null;
		}

		if ( this == SHORT ) {
			final int lastPoint = clazz.lastIndexOf( '.' );
			if ( lastPoint != -1 ) {
				clazz = clazz.substring( lastPoint + 1 );
			}
		}

		return clazz.intern( );
	}

}
