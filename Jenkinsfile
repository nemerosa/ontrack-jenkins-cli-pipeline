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
                    ontrackCliValidateTests(stamp: 'BUILD')
                    ontrackCliValidateCHML(stamp: 'CHML', critical: 2, high: 13)
                    ontrackCliValidatePercentage(stamp: 'PERCENTAGE', value: 87)
                    ontrackCliValidateMetrics(stamp: 'METRICS', metrics: [
                        ui: 88,
                        backend: 67.3,
                        network: 15.0,
                    ])
                }
            }
        }

    }

}