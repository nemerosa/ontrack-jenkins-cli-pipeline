import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    String dataType = ParamUtils.getConditionalParam(params, "dataType", false, null)
    boolean dataValidation = ParamUtils.getBooleanParam(params, "dataValidation", true)
    Object data = params.data

    boolean computeStatusWhenMissing = !dataType || !dataValidation

    // GraphQL query
    String query = '''
        mutation CreateValidationRun(
            $project: String!,
            $branch: String!,
            $build: String!,
            $validation: String!,
            $status: String,
            $description: String,
            $runInfo: RunInfoInput,
            $dataTypeId: String,
            $data: JSON
        ) {
            createValidationRun(input: {
                project: $project,
                branch: $branch,
                build: $build,
                validationStamp: $validation,
                validationRunStatus: $status,
                description: $description,
                dataTypeId: $dataTypeId,
                data: $data,
                runInfo: $runInfo
            }) {
                validationRun {
                    id
                }
                errors {
                    message
                }
            }
        }
    '''

    // Validation parameters
    Validation validation = new Validation("ontrack-cli-validate")
    Map<String,?> variables = validation.variables(this, params, computeStatusWhenMissing)

    // Data
    if (dataType) {
        if (!data) throw new RuntimeException("dataType is provided but data is missing.")
        variables.dataType = dataType
        variables.data = data
    }

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'createValidationRun')

    // Validation run properties
    Validation.setValidationRunProperties(this, params, response, 'createValidationRun')

}
