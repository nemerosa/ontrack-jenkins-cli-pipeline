@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {

    agent any

    stages {

        stage("Setup") {
            steps {
                ontrackSetup(logging: true, tracing: true)
                sh 'echo $PATH'
                sh 'ls -lrt $ONTRACK_CLI_DIR'
                sh 'ontrack-cli version --client'
            }
        }

    }

}