@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {

    agent any

    stages {

        stage("Setup") {
            steps {
                ontrackSetup(logging: true, tracing: true)
                sh 'echo $ONTRACK_CLI_DIR'
                sh 'echo $ONTRACK_CLI_NAME'
                sh 'echo $ONTRACK_CLI'
                sh 'echo $PATH'
                sh 'ls -lrt $ONTRACK_CLI_DIR'
                sh 'ontrack-cli version --client'
            }
        }

    }

}