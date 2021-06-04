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
                         ],
                         [
                           name: "GENERIC",
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
            environment {
                ONTRACK_TOKEN = credentials('ONTRACK_TOKEN')
            }
            steps {
                sh '''
                    ./gradlew test \\
                        --console plain
                '''
                // Testing the GraphQL call
                script {
                    def result = ontrackCliGraphQL(
                        query: '''
                            query BranchInfo($project: String!) {
                                branches(project: $project, name: "release.*") {
                                    name
                                }
                            }
                        ''',
                        variables: [
                            project: "ontrack"
                        ],
                    )
                    result.data.branches.each { branch ->
                        echo "Branch = ${branch.name}"
                    }
                }
                // Testing the last branch call
                script {
                    String lastOntrackRelease = ontrackCliLastBranch(project: 'ontrack', pattern: 'release-.*')
                    echo "Last Ontrack release branch: ${lastOntrackRelease}"
                }
            }
            post {
                always {
                    ontrackCliValidate(stamp: 'GENERIC')
                    ontrackCliValidateTests(stamp: 'BUILD')
                    ontrackCliValidateCHML(stamp: 'CHML', critical: 0, high: 0, medium: 13, low: 218)
                    ontrackCliValidatePercentage(stamp: 'PERCENTAGE', value: 34)
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
