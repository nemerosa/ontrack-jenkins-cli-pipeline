import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)
    int critical = ParamUtils.getIntParam(params, "critical", 0)
    int high = ParamUtils.getIntParam(params, "high", 0)
    int medium = ParamUtils.getIntParam(params, "medium", 0)
    int low = ParamUtils.getIntParam(params, "low", 0)

    // GraphQL query
    String query = '''
        mutation ValidateBuildWithCHML(
            $project: String!,
            $branch: String!,
            $build: String!,
            $validation: String!,
            $description: String!,
            $runInfo: RunInfoInput,
            $critical: Int!,
            $high: Int!,
            $medium: Int!,
            $low: Int!
        ) {
            validateBuildWithCHML(input: {
                project: $project,
                branch: $branch,
                build: $build,
                validation: $validation,
                description: $description,
                runInfo: $runInfo,
                critical: $critical,
                high: $high,
                medium: $medium,
                low: $low
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
    Validation validation = new Validation("ontrack-cli-validate-chml")
    Map<String,?> variables = validation.variables(this, params, false)

    // CHML values
    variables.critical = critical
    variables.high = high
    variables.medium = medium
    variables.low = low

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'validateBuildWithCHML')

    // Validation run properties
    Validation.setValidationRunProperties(this, params, response, 'validateBuildWithPercentage')
}
