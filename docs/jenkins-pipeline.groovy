pipeline {
  agent any

  stages {
    stage('Checkout') {
      steps {
        echo 'Clonando repositorio...'
        checkout scm
      }
    }

    stage('Build') {
      steps {
        sh 'echo "Compilación / tarea demo correcta" > build.txt'
      }
    }

    stage('Test') {
      steps {
        sh 'cat build.txt'
      }
    }
  }

  post {
    success {
      echo 'Pipeline terminada correctamente.'
    }
    failure {
      echo 'Pipeline falló.'
    }
  }
}
