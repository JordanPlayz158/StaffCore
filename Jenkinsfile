pipeline {
  agent any
  stages {
    stage('Build JAR') {
      steps {
        sh 'mvn clean package'
      }
    }

    stage('Deploy if tagged') {
      steps {
        sh 'if [[ -z "${TAG_NAME}" ]]; then mvn deploy; fi'
      }
    }

  }
}