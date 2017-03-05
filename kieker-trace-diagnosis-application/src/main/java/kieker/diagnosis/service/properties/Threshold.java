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

package kieker.diagnosis.service.properties;

/**
 * @author Nils Christian Ehmke
 */
public enum Threshold {

	THRESHOLD_0_5( 0.5f ), THRESHOLD_1( 1f ), THRESHOLD_10( 10f ), THRESHOLD_20( 20f ), THRESHOLD_30( 30f ), THRESHOLD_40( 40f ), THRESHOLD_50( 50f );

	private final float ivPercent;

	private Threshold( final float aPercent ) {
		ivPercent = aPercent;
	}

	public float getPercent( ) {
		return ivPercent;
	}

}
