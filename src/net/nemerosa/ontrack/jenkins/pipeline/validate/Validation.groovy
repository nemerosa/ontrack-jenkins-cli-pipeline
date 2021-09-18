package net.nemerosa.ontrack.jenkins.pipeline.validate

import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.properties.MessagePropertyUtils
import net.nemerosa.ontrack.jenkins.pipeline.properties.MetaInfoPropertyUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.JenkinsUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.RunInfo

class Validation {

    private final String name

    Validation(String name) {
        this.name = name
    }

    Map<String, ?> variables(def dsl, Map<String, ?> params, boolean computeStatusWhenMissing) {
        String project = ParamUtils.getParam(params, "project", dsl.env.ONTRACK_PROJECT_NAME as String)
        String branch = ParamUtils.getParam(params, "branch", dsl.env.ONTRACK_BRANCH_NAME as String)
        String build = ParamUtils.getParam(params, "build", dsl.env.ONTRACK_BUILD_NAME as String)
        String stamp = ParamUtils.getParam(params, "stamp")
        String description = ParamUtils.getConditionalParam(params, "description", false, '')
        String status = ParamUtils.getConditionalParam(params, "status", false, null)
        boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
        boolean tracing = ParamUtils.getBooleanParam(params, "tracing", false)

        Closure logger = {}
        if (logging) {
            logger = { msg ->
                println("[$name] $msg")
            }
        }

        Closure tracer = {}
        if (logging && tracing) {
            tracer = logger
        }

        // Base variables
        Map<String, ?> variables = [
                project    : project,
                branch     : branch,
                build      : build,
                validation : stamp,
                description: description,
        ]

        // Computing the status if needed
        String actualStatus = status
        if (!actualStatus) {
            if (computeStatusWhenMissing) {
                logger("No status is provided, computing status...")
                actualStatus = JenkinsUtils.getValidationRunStatusFromStage(dsl)
                logger("Computed status: $actualStatus")
            }
        }

        // Setting up the status
        if (actualStatus) {
            variables.status = actualStatus
        }

        // Run info
        RunInfo runInfo = JenkinsUtils.getRunInfo(dsl, tracer)
        tracer("Run info = ${runInfo.toString()}")
        if (runInfo != null && !runInfo.isEmpty()) {
            Map<String, ?> runInfoVariables = [:]
            variables.runInfo = runInfoVariables
            if (runInfo.runTime != null) {
                runInfoVariables.runTime = runInfo.runTime
            }
            if (runInfo.sourceType != null) {
                runInfoVariables.sourceType = runInfo.sourceType
            }
            if (runInfo.sourceUri != null) {
                runInfoVariables.sourceUri = runInfo.sourceUri
            }
            if (runInfo.triggerType != null) {
                runInfoVariables.triggerType = runInfo.triggerType
            }
            if (runInfo.triggerData != null) {
                runInfoVariables.triggerData = runInfo.triggerData
            }
        }

        // OK
        return variables
    }

    static void setValidationRunProperties(def dsl, Map<String, ?> params, def response, String payloadNode, boolean logging = false) {
        // Getting the validation run from the returned payload
        def validationRunId = response.data[payloadNode].validationRun.id as int
        if (validationRunId) {
            // GraphQL variables
            Map<String, Object> variables = [
                    validationRunId: validationRunId,
            ]
            // Supported properties
            variables.messageProperty = MessagePropertyUtils.setVariables(params, variables)
            variables.metaInfoProperty = MetaInfoPropertyUtils.setVariables(params, variables)
            // If at least one property is set
            if (variables.messageProperty || variables.metaInfoProperty) {
                String query = '''
                    mutation SetValidationRunProperties(
                        $validationRunId: Int!,
                        $messageProperty: Boolean!,
                        $messagePropertyType: String!,
                        $messagePropertyText: String!,
                        $metaInfoPropertyItems: [MetaInfoPropertyItemInput!]!,
                    ) {
                        setValidationRunMessagePropertyById(input: {
                            id: $validationRunId,
                            type: $messagePropertyType,
                            text: $messagePropertyText,
                        }) @include(if: $messageProperty) {
                            errors {
                                message
                            }
                        }
                        setValidationRunMetaInfoPropertyById(input: {
                            id: $validationRunId,
                            append: false,
                            items: $metaInfoPropertyItems,
                        }) @include(if: $metaInfoProperty) {
                            errors {
                                message
                            }
                        }
                    }
                '''
                // Call
                def propertyResponse = dsl.ontrackCliGraphQL(
                        logging: logging,
                        query: query,
                        variables: variables,
                )

                // Checks for errors

                GraphQL.checkForMutationErrors(propertyResponse, 'setValidationRunMessagePropertyById')
                GraphQL.checkForMutationErrors(propertyResponse, 'setValidationRunMetaInfoPropertyById')
            }
        }
    }
}
