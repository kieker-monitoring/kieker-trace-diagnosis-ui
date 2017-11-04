pipeline {

	agent {
		label 'kieker-slave-docker'
	}
  
	environment {
		DOCKER_CONTAINER = 'kieker/kieker-build:ta-v3'
	}
	
	stages {
		
		stage('Docker Pull') {
			steps {
				sh 'docker pull $DOCKER_CONTAINER'
			}
		}
		
		stage('Build') {
			steps {
				sh 'docker run --rm -u `id -u` -v $WORKSPACE:/opt/kieker $DOCKER_CONTAINER /bin/bash -c "cd /opt/kieker; ./mvnw clean install"'
			}
		}
		
		stage('Archive Artifacts') {
			archiveArtifacts artifacts: 'kieker-trace-diagnosis-release-engineering/target/Kieker-Trace-Diagnosis-*-linux.tar.gz, kieker-trace-diagnosis-release-engineering/target/Kieker-Trace-Diagnosis-*-windows.zip', fingerprint: true
		}
		
	}

	post {
		always {
			deleteDir()
		}
	}
	
}
