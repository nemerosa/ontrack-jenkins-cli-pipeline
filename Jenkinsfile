@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {

    agent any

    stages {

        stage("Setup") {
            steps {
                ontrackSetup(logging: true, tracing: true)
                sh 'ontrack-cli version --client'
            }
        }

    }

}