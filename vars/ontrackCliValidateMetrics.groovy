import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)
    Map<String, Double> metrics = params.metrics as Map<String, Double>
    if (!metrics) throw new RuntimeException("Missing metrics")

    // GraphQL query
    String query = '''
        mutation ValidateBuildWithMetrics(
            $project: String!,
            $branch: String!,
            $build: String!,
            $validation: String!,
            $description: String!,
            $runInfo: RunInfoInput,
            $metrics: [MetricsEntryInput!]!
        ) {
            validateBuildWithMetrics(input: {
                project: $project,
                branch: $branch,
                build: $build,
                validation: $validation,
                description: $description,
                runInfo: $runInfo,
                metrics: $metrics
            }) {
                errors {
                    message
                }
            }
        }
    '''

    // Validation parameters
    Validation validation = new Validation("ontrack-cli-validate-metrics")
    Map<String,?> variables = validation.variables(this, params, false)

    // Metrics
    def metricsInput = []
    metrics.each { name, value ->
        metricsInput += [
            name: name,
            value: value,
        ]
    }
    variables.metrics = metricsInput

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'validateBuildWithMetrics')
}
