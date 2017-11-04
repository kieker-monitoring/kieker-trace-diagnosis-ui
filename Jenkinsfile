pipeline {
  agent {
    label 'kieker-slave-docker'
  }
  stages {
    stage('Build') {
      steps {
        sh 'docker run --rm -u `id -u` -v $WORKSPACE:/opt/kieker kieker/kieker-build:ta-v2 /bin/bash -c "cd /opt/kieker; ./mvnw clean install"'
        archiveArtifacts artifacts: 'kieker-trace-diagnosis-release-engineering/target/Kieker-Trace-Diagnosis-*-linux.tar.gz, kieker-trace-diagnosis-release-engineering/target/Kieker-Trace-Diagnosis-*-windows.zip', fingerprint: true
	  }
    }
  }

  post {
    always {
      deleteDir()
    }
  }
}
