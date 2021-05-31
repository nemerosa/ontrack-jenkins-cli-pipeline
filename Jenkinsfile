@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {

    agent any

    stages {

        stage("Setup") {
            steps {
                ontrackCliSetup(logging: true, autoValidationStamps: true)
                ontrackCliBuild()
            }
        }

        stage("Build") {
            steps {
                sh '''
                    ./gradlew test \\
                        --console plain
                '''
            }
            post {
                always {
                    ontrackCliValidate(
                        stamp: 'BUILD',
                        logging: true,
                        tracing: true,
                        dataType: 'net.nemerosa.ontrack.extension.general.validation.TestSummaryValidationDataType',
                        data: [
                            passed : 15,
                            skipped: 8,
                            failed : 1,
                        ]
                    )
                }
            }
        }

    }

}