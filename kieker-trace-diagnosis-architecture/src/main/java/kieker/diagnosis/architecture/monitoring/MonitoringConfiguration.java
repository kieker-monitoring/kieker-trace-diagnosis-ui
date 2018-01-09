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

package kieker.diagnosis.architecture.monitoring;

/**
 * This DTO contains the configuration for the monitoring within the application.
 *
 * @author Nils Christian Ehmke
 */
public final class MonitoringConfiguration {

	private String ivOutputDirectory;
	private boolean ivActive;
	private Timer ivTimer;
	private Writer ivWriter;
	private int ivMaxEntriesPerFile;
	private int ivQueueSize;
	private int ivBuffer;

	public String getOutputDirectory( ) {
		return ivOutputDirectory;
	}

	public void setOutputDirectory( final String aOutputDirectory ) {
		ivOutputDirectory = aOutputDirectory;
	}

	public boolean isActive( ) {
		return ivActive;
	}

	public void setActive( final boolean aActive ) {
		ivActive = aActive;
	}

	public Timer getTimer( ) {
		return ivTimer;
	}

	public void setTimer( final Timer aTimer ) {
		ivTimer = aTimer;
	}

	public Writer getWriter( ) {
		return ivWriter;
	}

	public void setWriter( final Writer aWriter ) {
		ivWriter = aWriter;
	}

	public int getMaxEntriesPerFile( ) {
		return ivMaxEntriesPerFile;
	}

	public void setMaxEntriesPerFile( final int aMaxEntriesPerFile ) {
		ivMaxEntriesPerFile = aMaxEntriesPerFile;
	}

	public int getQueueSize( ) {
		return ivQueueSize;
	}

	public void setQueueSize( final int aQueueSize ) {
		ivQueueSize = aQueueSize;
	}

	public int getBuffer( ) {
		return ivBuffer;
	}

	public void setBuffer( final int aBuffer ) {
		ivBuffer = aBuffer;
	}

}
