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
	}

	post {
		always {
			deleteDir()
		}
	}
	
}
