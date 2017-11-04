# Description

Kieker Trace Diagnosis is a tool that has been developed to present Kieker monitoring logs in a tabular way and make them searchable. A focus has been put on memory usage and performance, which means that bigger logs in the range of several million method calls can still be opened.

Currently the tool supports monitoring logs written by Kieker's binary and ascii writers. The used records have to be Kieker's <i>TraceMetadata</i>, <i>BeforeOperationEvent</i>, <i>AfterOperationEvent</i>, and <i>AfterOperationFailedEvent</i>. Other records are ignored by the tool.

Our issue management system is located at https://kieker-monitoring.atlassian.net.

# Download

Releases of Kieker Trace Diagnosis can be downloaded via
https://github.com/kieker-monitoring/kieker-trace-diagnosis-ui/releases

Snapshots of Kieker Trace Diagnosis can be downloaded via
https://build.se.informatik.uni-kiel.de/jenkins/job/kieker-monitoring/job/kieker-trace-diagnosis-ui/

# IDE Setup for Contributors

Kieker Trace Diagnosis is a Maven project and can thus be imported into an IDE of your choice. In Eclipse, for instance, you can open it by using the menu point <i>Import -> Import as Maven Project</i>. Please make also sure that you have a JDK-9 installed and configured in your IDE.