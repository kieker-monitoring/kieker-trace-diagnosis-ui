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

import java.util.concurrent.TimeUnit;

/**
 * This is a data transfer object holding the settings of the application.
 *
 * @author Nils Christian Ehmke
 */
public final class Settings {

	private TimestampAppearance ivTimestampAppearance;
	private TimeUnit ivTimeUnit;
	private ClassAppearance ivClassAppearance;
	private MethodAppearance ivMethodAppearance;
	private boolean ivShowUnmonitoredTimeProperty;
	private MethodCallAggregation ivMethodCallAggregation;
	private int ivMaxNumberOfMethodCalls;
	private float ivMethodCallThreshold;

	public TimestampAppearance getTimestampAppearance( ) {
		return ivTimestampAppearance;
	}

	public void setTimestampAppearance( final TimestampAppearance aTimestampAppearance ) {
		ivTimestampAppearance = aTimestampAppearance;
	}

	public TimeUnit getTimeUnit( ) {
		return ivTimeUnit;
	}

	public void setTimeUnit( final TimeUnit aTimeUnit ) {
		ivTimeUnit = aTimeUnit;
	}

	public ClassAppearance getClassAppearance( ) {
		return ivClassAppearance;
	}

	public void setClassAppearance( final ClassAppearance aClassAppearance ) {
		ivClassAppearance = aClassAppearance;
	}

	public MethodAppearance getMethodAppearance( ) {
		return ivMethodAppearance;
	}

	public void setMethodAppearance( final MethodAppearance aMethodAppearance ) {
		ivMethodAppearance = aMethodAppearance;
	}

	public boolean isShowUnmonitoredTimeProperty( ) {
		return ivShowUnmonitoredTimeProperty;
	}

	public void setShowUnmonitoredTimeProperty( final boolean aShowUnmonitoredTimeProperty ) {
		ivShowUnmonitoredTimeProperty = aShowUnmonitoredTimeProperty;
	}

	public MethodCallAggregation getMethodCallAggregation( ) {
		return ivMethodCallAggregation;
	}

	public void setMethodCallAggregation( final MethodCallAggregation aMethodCallAggregation ) {
		ivMethodCallAggregation = aMethodCallAggregation;
	}

	public int getMaxNumberOfMethodCalls( ) {
		return ivMaxNumberOfMethodCalls;
	}

	public void setMaxNumberOfMethodCalls( final int aMaxNumberOfMethodCalls ) {
		ivMaxNumberOfMethodCalls = aMaxNumberOfMethodCalls;
	}

	public float getMethodCallThreshold( ) {
		return ivMethodCallThreshold;
	}

	public void setMethodCallThreshold( final float aMethodCallThreshold ) {
		ivMethodCallThreshold = aMethodCallThreshold;
	}

}
