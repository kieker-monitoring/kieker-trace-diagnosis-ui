[![Build Status](https://travis-ci.org/kieker-monitoring/kieker-trace-diagnosis-ui.svg?branch=refactoring)](https://travis-ci.org/kieker-monitoring/kieker-trace-diagnosis-ui)
[![Coverage](https://codecov.io/gh/kieker-monitoring/kieker-trace-diagnosis-ui/branch/refactoring/graphs/badge.svg?branch=refactoring)](https://codecov.io/gh/kieker-monitoring/kieker-trace-diagnosis-ui/branch/refactoring)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

# Description

We developed the Kieker Trace Diagnosis tool to display Kieker monitoring logs in a tabular way and to help you find performance issues in those logs. You can filter and sort the data in various ways. We put a main focus on memory usage and performance, which means that bigger logs in the range of several million method calls can still be processed. We also made sure that even slightly damaged and incomplete monitoring logs are handled in a correct way.

Currently the tool supports monitoring logs written by Kieker's binary and ascii writers. The used records have to be Kieker's <i>TraceMetadata</i>, <i>BeforeOperationEvent</i>, <i>AfterOperationEvent</i>, and <i>AfterOperationFailedEvent</i>. Our tool ignores all other records.

# Download

Releases of Kieker Trace Diagnosis can be downloaded via
https://github.com/kieker-monitoring/kieker-trace-diagnosis-ui/releases

The latest version is 5.0.0 and requires Java 11. 

# Issues

If you have any issues, please check our [wiki](https://github.com/kieker-monitoring/kieker-trace-diagnosis-ui/wiki). If that does not solve your problem or if you have any feature requests, please visit our issue management system at https://kieker-monitoring.atlassian.net.

# IDE Setup for Contributors

Kieker Trace Diagnosis is a Maven project and can thus be imported into an IDE of your choice. However, since 4.0.0 the tool uses and requires Java 11. It is recommended to install an OpenJDK 11 and, for instance, the latest Eclipse with the e(fx)clipse plugin. Please install also Project Lombok into your IDE and the platform dependent OpenJFX SDK. If you use Eclipse, make sure that Eclipse itself is started with Java 11 (by modifying the eclipse.ini). In order to start the application from within your IDE, you need to provide the following VM parameters: 
```
--module-path <PATH to OpenJFX SDK>  --add-modules=javafx.controls
```
