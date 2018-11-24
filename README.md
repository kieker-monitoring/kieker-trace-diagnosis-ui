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

Kieker Trace Diagnosis is a Maven project and can thus be imported into an IDE of your choice. However, since 4.0.0 the tool uses and requires Java 11. It is recommended to install an OpenJDK 11 and, for instance, the latest Eclipse with the e(fx)clipse plugin. Please install also the platform dependent OpenJFX SDK. If you use Eclipse, make sure that Eclipse itself is started with Java 11 (by modifying the eclipse.ini). In order to start the application from within your IDE, you need to provide the following VM parameters: 
```
--module-path <PATH to OpenJFX SDK>  --add-modules=javafx.controls,javafx.fxml,javafx.web
```
