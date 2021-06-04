import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)
    int value = ParamUtils.getIntParam(params, "value", 0)

    // GraphQL query
    String query = '''
        mutation ValidateBuildWithPercentage(
            $project: String!,
            $branch: String!,
            $build: String!,
            $validation: String!,
            $description: String!,
            $runInfo: RunInfoInput,
            $value: Int!
        ) {
            validateBuildWithPercentage(input: {
                project: $project,
                branch: $branch,
                build: $build,
                validation: $validation,
                description: $description,
                runInfo: $runInfo,
                value: $value
            }) {
                errors {
                    message
                }
            }
        }
    '''

    // GraphQL variables
    Validation validation = new Validation("ontrack-cli-validate-percentage")
    Map<String,?> variables = validation.variables(this, params, false)

    // Value
    variables.value =  value

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'validateBuildWithPercentage')
}
