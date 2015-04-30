/***************************************************************************
 * Copyright 2015 Kieker Project (http://kieker-monitoring.net)
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

package kieker.common.record.io.database;

import java.nio.BufferOverflowException;
import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.util.registry.IRegistry;
import kieker.common.util.Version;

import kieker.common.record.io.database.DatabaseEventRecord;

/**
 * @author Christian Zirkelbach
 * 
 * @since 1.10
 */
public class DatabaseAfterEventRecord extends DatabaseEventRecord  {
	/** Descriptive definition of the serialization size of the record. */
	public static final int SIZE = TYPE_SIZE_STRING // IOperationSignature.operationSignature
			 + TYPE_SIZE_STRING // IClassSignature.classSignature
			 + TYPE_SIZE_STRING // ICallRecord.calleeOperationSignature
			 + TYPE_SIZE_STRING // ICallRecord.calleeClassSignature
			 + TYPE_SIZE_INT // IObjectRecord.objectId
			 + TYPE_SIZE_INT // ICallObjectRecord.calleeObjectId
			 + TYPE_SIZE_LONG // IEventRecord.timestamp
			 + TYPE_SIZE_STRING // DatabaseAfterEventRecord.returnValue
			 + TYPE_SIZE_STRING // DatabaseAfterEventRecord.callArgs
	;
	private static final long serialVersionUID = -572576643076313158L;
	
	public static final Class<?>[] TYPES = {
		String.class, // IOperationSignature.operationSignature
		String.class, // IClassSignature.classSignature
		String.class, // ICallRecord.calleeOperationSignature
		String.class, // ICallRecord.calleeClassSignature
		int.class, // IObjectRecord.objectId
		int.class, // ICallObjectRecord.calleeObjectId
		long.class, // IEventRecord.timestamp
		String.class, // DatabaseAfterEventRecord.returnValue
		String.class, // DatabaseAfterEventRecord.callArgs
	};
	
	/* user-defined constants */
	/* default constants */
	public static final String RETURN_VALUE = "";
	public static final String CALL_ARGS = "";
	/* property declarations */
	private final String returnValue;
	private final String callArgs;

	/**
	 * Creates a new instance of this class using the given parameters.
	 * 
	 * @param operationSignature
	 *            operationSignature
	 * @param classSignature
	 *            classSignature
	 * @param calleeOperationSignature
	 *            calleeOperationSignature
	 * @param calleeClassSignature
	 *            calleeClassSignature
	 * @param objectId
	 *            objectId
	 * @param calleeObjectId
	 *            calleeObjectId
	 * @param timestamp
	 *            timestamp
	 * @param returnValue
	 *            returnValue
	 * @param callArgs
	 *            callArgs
	 */
	public DatabaseAfterEventRecord(final String operationSignature, final String classSignature, final String calleeOperationSignature, final String calleeClassSignature, final int objectId, final int calleeObjectId, final long timestamp, final String returnValue, final String callArgs) {
		super(operationSignature, classSignature, calleeOperationSignature, calleeClassSignature, objectId, calleeObjectId, timestamp);
		this.returnValue = returnValue == null?"":returnValue;
		this.callArgs = callArgs == null?"":callArgs;
	}

	/**
	 * This constructor converts the given array into a record.
	 * It is recommended to use the array which is the result of a call to {@link #toArray()}.
	 * 
	 * @param values
	 *            The values for the record.
	 */
	public DatabaseAfterEventRecord(final Object[] values) { // NOPMD (direct store of values)
		super(values, TYPES);
		this.returnValue = (String) values[7];
		this.callArgs = (String) values[8];
	}
	
	/**
	 * This constructor uses the given array to initialize the fields of this record.
	 * 
	 * @param values
	 *            The values for the record.
	 * @param valueTypes
	 *            The types of the elements in the first array.
	 */
	protected DatabaseAfterEventRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		super(values, valueTypes);
		this.returnValue = (String) values[7];
		this.callArgs = (String) values[8];
	}

	/**
	 * This constructor converts the given array into a record.
	 * 
	 * @param buffer
	 *            The bytes for the record.
	 * 
	 * @throws BufferUnderflowException
	 *             if buffer not sufficient
	 */
	public DatabaseAfterEventRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferUnderflowException {
		super(buffer, stringRegistry);
		this.returnValue = stringRegistry.get(buffer.getInt());
		this.callArgs = stringRegistry.get(buffer.getInt());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Object[] toArray() {
		return new Object[] {
			this.getOperationSignature(),
			this.getClassSignature(),
			this.getCalleeOperationSignature(),
			this.getCalleeClassSignature(),
			this.getObjectId(),
			this.getCalleeObjectId(),
			this.getTimestamp(),
			this.getReturnValue(),
			this.getCallArgs()
		};
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void writeBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferOverflowException {
		buffer.putInt(stringRegistry.get(this.getOperationSignature()));
		buffer.putInt(stringRegistry.get(this.getClassSignature()));
		buffer.putInt(stringRegistry.get(this.getCalleeOperationSignature()));
		buffer.putInt(stringRegistry.get(this.getCalleeClassSignature()));
		buffer.putInt(this.getObjectId());
		buffer.putInt(this.getCalleeObjectId());
		buffer.putLong(this.getTimestamp());
		buffer.putInt(stringRegistry.get(this.getReturnValue()));
		buffer.putInt(stringRegistry.get(this.getCallArgs()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Class<?>[] getValueTypes() {
		return TYPES; // NOPMD
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getSize() {
		return SIZE;
	}
	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This record uses the {@link kieker.common.record.IMonitoringRecord.Factory} mechanism. Hence, this method is not implemented.
	 */
	@Override
	@Deprecated
	public void initFromArray(final Object[] values) {
		throw new UnsupportedOperationException();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @deprecated This record uses the {@link kieker.common.record.IMonitoringRecord.BinaryFactory} mechanism. Hence, this method is not implemented.
	 */
	@Override
	@Deprecated
	public void initFromBytes(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferUnderflowException {
		throw new UnsupportedOperationException();
	}

	public final String getReturnValue() {
		return this.returnValue;
	}
	
	public final String getCallArgs() {
		return this.callArgs;
	}
	
}
