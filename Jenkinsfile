pipeline {
  agent {
    label 'kieker-slave-docker'
  }
  stages {
    stage('Build') {
      steps {
        sh 'docker run --rm -u `id -u` -v $WORKSPACE:/opt/kieker kieker/kieker-build:ta-v3 /bin/bash -c "cd /opt/kieker; ./mvnw clean install"'
      }
    }
  }

  post {
    always {
      deleteDir()
    }
  }
}
