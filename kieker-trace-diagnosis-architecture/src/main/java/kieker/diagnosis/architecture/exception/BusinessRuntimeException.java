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

package kieker.diagnosis.architecture.exception;

/**
 * This is an exception which marks that something from a business point of view failed. It is only to be used in controllers, where a {@link BusinessException}
 * is transformed into a runtime exception. This can be done, as controllers are proxied with an exception handling mechanism. This exception should not be used
 * outside of controllers.
 *
 * @author Nils Christian Ehmke
 */
public final class BusinessRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public BusinessRuntimeException( final BusinessException aBusinessException ) {
		super( aBusinessException );
	}

}
