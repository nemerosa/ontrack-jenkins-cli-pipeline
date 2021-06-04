package net.nemerosa.ontrack.jenkins.pipeline.validate

import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

class ValidationStampUtils {

    static void setupGenericValidationStamp(def dsl, boolean logging, String project, String branch, String validation, String description) {
        def response = dsl.ontrackCliGraphQL(
                query: '''
                    mutation SetupValidationStamp(
                        $project: String!,
                        $branch: String!,
                        $validation: String!,
                        $description: String
                    ) {
                        setupValidationStamp(input: {
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
                variables: [
                        project    : project,
                        branch     : branch,
                        validation : validation,
                        description: description,
                ]
        )
        GraphQL.checkForMutationErrors(response, 'setupValidationStamp')
    }

}
