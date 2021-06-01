@Library("ontrack-jenkins-cli-pipeline@main") _

pipeline {

    agent any

    stages {

        stage("Setup") {
            steps {
                ontrackCliSetup(
                    logging: true,
                     autoValidationStamps: true,
                     validations: [
                         [
                            name: "BUILD",
                            tests: [
                                warningIfSkipped: true,
                            ]
                         ],
                         [
                            name: "CHML",
                            chml: [
                                failed: [
                                    level: 'CRITICAL',
                                    value: 1,
                                ],
                                warning: [
                                    level: 'HIGH',
                                    value: 1,
                                ]
                            ],
                         ],
                         [
                            name: "PERCENTAGE",
                            percentage: [
                                failure: 80,
                                warning: 50,
                                okIfGreater: false,
                            ],
                         ],
                         [
                            name: "METRICS",
                            metrics: true
                         ]
                     ],
                     promotions: [
                        BRONZE: [
                            validations: [
                                "BUILD"
                            ]
                        ],
                        SILVER: [
                            promotions: [
                                "BRONZE",
                            ],
                            validations: [
                                "CHML",
                                "PERCENTAGE",
                                "METRICS",
                            ],
                        ],
                     ]
                )
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
                    ontrackCliValidateMetrics(stamp: 'METRICS', status: 'PASSED', metrics: [
                        ui: 88,
                        backend: 67.3,
                        network: 15.0,
                    ])
                }
            }
        }

    }

}