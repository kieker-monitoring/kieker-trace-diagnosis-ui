Kieker Trace Diagnosis is a tool that has been developed to present Kieker monitoring logs in a tabular way and make them searchable. A focus has been put on memory usage and performance, which means that bigger logs in the range of several million method calls can still be opened.

Currently the tool supports monitoring logs written by Kieker's binary and ascii writers. The used records have to be Kieker's <i>TraceMetadata</i>, <i>BeforeOperationEvent</i>, <i>AfterOperationEvent</i>, and <i>AfterOperationFailedEvent</i>. Other records are ignored by the tool.
