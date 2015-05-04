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

import java.nio.BufferUnderflowException;
import java.nio.ByteBuffer;

import kieker.common.record.AbstractMonitoringRecord;
import kieker.common.record.IMonitoringRecord;
import kieker.common.util.registry.IRegistry;
import kieker.common.util.Version;

import kieker.common.record.flow.ICallObjectRecord;
import kieker.common.record.flow.IEventRecord;

/**
 * @author Christian Zirkelbach
 * 
 * @since 1.11
 */
public abstract class DatabaseEventRecord extends AbstractMonitoringRecord implements IMonitoringRecord.Factory, IMonitoringRecord.BinaryFactory, ICallObjectRecord, IEventRecord {
		private static final long serialVersionUID = -4897958047240905589L;
	
	
	/* user-defined constants */
	/* default constants */
	public static final String OPERATION_SIGNATURE = "";
	public static final String CLASS_SIGNATURE = "";
	public static final String CALLEE_OPERATION_SIGNATURE = "";
	public static final String CALLEE_CLASS_SIGNATURE = "";
	public static final int OBJECT_ID = 0;
	public static final int CALLEE_OBJECT_ID = 0;
	public static final long TIMESTAMP = 0L;
	/* property declarations */
	private final String operationSignature;
	private final String classSignature;
	private final String calleeOperationSignature;
	private final String calleeClassSignature;
	private final int objectId;
	private final int calleeObjectId;
	private final long timestamp;

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
	 */
	public DatabaseEventRecord(final String operationSignature, final String classSignature, final String calleeOperationSignature, final String calleeClassSignature, final int objectId, final int calleeObjectId, final long timestamp) {
		this.operationSignature = operationSignature == null?OPERATION_SIGNATURE:operationSignature;
		this.classSignature = classSignature == null?CLASS_SIGNATURE:classSignature;
		this.calleeOperationSignature = calleeOperationSignature == null?CALLEE_OPERATION_SIGNATURE:calleeOperationSignature;
		this.calleeClassSignature = calleeClassSignature == null?CALLEE_CLASS_SIGNATURE:calleeClassSignature;
		this.objectId = objectId;
		this.calleeObjectId = calleeObjectId;
		this.timestamp = timestamp;
	}

	
	/**
	 * This constructor uses the given array to initialize the fields of this record.
	 * 
	 * @param values
	 *            The values for the record.
	 * @param valueTypes
	 *            The types of the elements in the first array.
	 */
	protected DatabaseEventRecord(final Object[] values, final Class<?>[] valueTypes) { // NOPMD (values stored directly)
		AbstractMonitoringRecord.checkArray(values, valueTypes);
		this.operationSignature = (String) values[0];
		this.classSignature = (String) values[1];
		this.calleeOperationSignature = (String) values[2];
		this.calleeClassSignature = (String) values[3];
		this.objectId = (Integer) values[4];
		this.calleeObjectId = (Integer) values[5];
		this.timestamp = (Long) values[6];
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
	public DatabaseEventRecord(final ByteBuffer buffer, final IRegistry<String> stringRegistry) throws BufferUnderflowException {
		this.operationSignature = stringRegistry.get(buffer.getInt());
		this.classSignature = stringRegistry.get(buffer.getInt());
		this.calleeOperationSignature = stringRegistry.get(buffer.getInt());
		this.calleeClassSignature = stringRegistry.get(buffer.getInt());
		this.objectId = buffer.getInt();
		this.calleeObjectId = buffer.getInt();
		this.timestamp = buffer.getLong();
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

	public final String getOperationSignature() {
		return this.operationSignature;
	}
	
	public final String getClassSignature() {
		return this.classSignature;
	}
	
	public final String getCallerOperationSignature() {
		return this.getOperationSignature();
	}
	
	public final String getCallerClassSignature() {
		return this.getClassSignature();
	}
	
	public final String getCalleeOperationSignature() {
		return this.calleeOperationSignature;
	}
	
	public final String getCalleeClassSignature() {
		return this.calleeClassSignature;
	}
	
	public final int getObjectId() {
		return this.objectId;
	}
	
	public final int getCallerObjectId() {
		return this.getObjectId();
	}
	
	public final int getCalleeObjectId() {
		return this.calleeObjectId;
	}
	
	public final long getTimestamp() {
		return this.timestamp;
	}
	
}
