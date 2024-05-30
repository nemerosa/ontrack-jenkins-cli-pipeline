package net.nemerosa.ontrack.jenkins.pipeline.validate

import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

class ValidationStampUtils {

    static void setupGenericValidationStamp(def dsl, boolean logging, String project, String branch, String validation, String description, String dataType = null, def dataConfig = null) {
        def response = dsl.ontrackCliGraphQL(
                query: '''
                    mutation SetupValidationStamp(
                        $project: String!,
                        $branch: String!,
                        $validation: String!,
                        $description: String,
                        $dataType: String,
                        $dataTypeConfig: JSON
                    ) {
                        setupValidationStamp(input: {
                            project: $project,
                            branch: $branch,
                            validation: $validation,
                            description: $description,
                            dataType: $dataType,
                            dataTypeConfig: $dataTypeConfig
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ''',
                logging: logging,
                variables: [
                        project       : project,
                        branch        : branch,
                        validation    : validation,
                        description   : description,
                        dataType      : dataType,
                        dataTypeConfig: dataConfig,
                ]
        )
        GraphQL.checkForMutationErrors(response, 'setupValidationStamp', dsl.ontrackCliIgnoreErrors())
    }

    static void setupMetricsValidationStamp(def dsl, boolean logging, String project, String branch, String validation, String description) {
        def variables = [
                project    : project,
                branch     : branch,
                validation : validation,
                description: '',
        ]
        def response = dsl.ontrackCliGraphQL(
                query: '''
                    mutation SetupMetricsValidationStamp(
                        $project: String!,
                        $branch: String!,
                        $validation: String!,
                        $description: String
                    ) {
                        setupMetricsValidationStamp(input: {
                            project: $project,
                            branch: $branch,
                            validation: $validation,
                            description: $description
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ''',
                logging: logging,
                variables: variables,
        )
        GraphQL.checkForMutationErrors(response, 'setupMetricsValidationStamp', dsl.ontrackCliIgnoreErrors())
    }

    static void setupPercentageValidationStamp(
            def dsl,
            boolean logging,
            String project,
            String branch,
            String validation,
            String description,
            int failure,
            int warning,
            boolean okIfGreater
    ) {
        def variables = [
                project    : project,
                branch     : branch,
                validation : validation,
                description: '',
                failure    : failure,
                warning    : warning,
                okIfGreater: okIfGreater,
        ]
        def response = dsl.ontrackCliGraphQL(
                query: '''
                    mutation SetupPercentageValidationStamp(
                        $project: String!,
                        $branch: String!,
                        $validation: String!,
                        $description: String,
                        $warning: Int,
                        $failure: Int,
                        $okIfGreater: Boolean!
                    ) {
                        setupPercentageValidationStamp(input: {
                            project: $project,
                            branch: $branch,
                            validation: $validation,
                            description: $description,
                            warningThreshold: $warning,
                            failureThreshold: $failure,
                            okIfGreater: $okIfGreater
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ''',
                logging: logging,
                variables: variables,
        )
        GraphQL.checkForMutationErrors(response, 'setupPercentageValidationStamp', dsl.ontrackCliIgnoreErrors())
    }

    static void setupCHMLValidationStamp(
            def dsl,
            boolean logging,
            String project,
            String branch,
            String validation,
            String description,
            String warningLevel,
            int warningValue,
            String failedLevel,
            int failedValue
    ) {
        def variables = [
                project     : project,
                branch      : branch,
                validation  : validation,
                description : description,
                warningLevel: warningLevel,
                warningValue: warningValue,
                failedLevel : failedLevel,
                failedValue : failedValue,
        ]
        def response = dsl.ontrackCliGraphQL(
                query: '''
                    mutation SetupCHMLValidationStamp(
                        $project: String!,
                        $branch: String!,
                        $validation: String!,
                        $description: String,
                        $warningLevel: CHML!,
                        $warningValue: Int!,
                        $failedLevel: CHML!,
                        $failedValue: Int!
                    ) {
                        setupCHMLValidationStamp(input: {
                            project: $project,
                            branch: $branch,
                            validation: $validation,
                            description: $description,
                            warningLevel: {
                                level: $warningLevel,
                                value: $warningValue
                            },
                            failedLevel: {
                                level: $failedLevel,
                                value: $failedValue
                            }
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ''',
                logging: logging,
                variables: variables,
        )
        GraphQL.checkForMutationErrors(response, 'setupCHMLValidationStamp', dsl.ontrackCliIgnoreErrors())
    }

    static void setupTestsValidationStamp(
            def dsl,
            boolean logging,
            String project,
            String branch,
            String validation,
            String description,
            boolean warningIfSkipped
    ) {
        def variables = [
                project         : project,
                branch          : branch,
                validation      : validation,
                description     : '',
                warningIfSkipped: warningIfSkipped
        ]
        def response = dsl.ontrackCliGraphQL(
                query: '''
                    mutation SetupTestSummaryValidationStamp(
                        $project: String!,
                        $branch: String!,
                        $validation: String!,
                        $description: String,
                        $warningIfSkipped: Boolean!
                    ) {
                        setupTestSummaryValidationStamp(input: {
                            project: $project,
                            branch: $branch,
                            validation: $validation,
                            description: $description,
                            warningIfSkipped: $warningIfSkipped
                        }) {
                            errors {
                                message
                            }
                        }
                    }
                ''',
                logging: logging,
                variables: variables,
        )
        GraphQL.checkForMutationErrors(response, 'setupTestSummaryValidationStamp', dsl.ontrackCliIgnoreErrors())
    }

}
