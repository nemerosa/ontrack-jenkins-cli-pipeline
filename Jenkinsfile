@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {

    agent any

    stages {

        stage("Setup") {
            steps {
                ontrackSetup(logging: true)
                sh 'ontrack-cli version --cli'
                sh 'ontrack-cli config list'
            }
        }

    }

}