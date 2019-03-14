pipeline {
    agent any
    stages {
        stage('Build') {
			steps {
				sh 'mvn clean install'           
			}
        }
		stage('Sonar') {
			steps {
			    sh 'mvn sonar:sonar'
			}
		}
    }
}
