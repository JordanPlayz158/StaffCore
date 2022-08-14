pipeline {
  agent any
  stages {
    stage('Build JAR') {
      steps {
        sh 'mvn clean package'
      }
    }

  }
}