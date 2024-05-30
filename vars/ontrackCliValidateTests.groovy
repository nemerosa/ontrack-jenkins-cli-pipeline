import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.validate.Validation
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    String pattern = ParamUtils.getParam(params, "pattern", "**/build/test-results/**/*.xml")
    boolean allowEmptyResults = ParamUtils.getBooleanParam(params, "allowEmptyResults", true)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    // Parsing the JUnit results
    def results = junit(testResults: pattern, allowEmptyResults: allowEmptyResults)

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack for pull requests."
        return
    }

    // Getting results details
    int passed = results.passCount
    int skipped = results.skipCount
    int failed = results.failCount

    // GraphQL query
    String query = '''
        mutation ValidateBuildWithTests(
            $project: String!,
            $branch: String!,
            $build: String!,
            $validation: String!,
            $description: String!,
            $runInfo: RunInfoInput,
            $passed: Int!,
            $skipped: Int!,
            $failed: Int!
        ) {
            validateBuildWithTests(input: {
                project: $project,
                branch: $branch,
                build: $build,
                validation: $validation,
                description: $description,
                runInfo: $runInfo,
                passed: $passed,
                skipped: $skipped,
                failed: $failed
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
    Validation validation = new Validation("ontrack-cli-validate-tests")
    Map<String,?> variables = validation.variables(this, params, false)

    // Tests args
    variables.passed = passed
    variables.skipped = skipped
    variables.failed = failed

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    if (GraphQL.checkForMutationErrors(response, 'validateBuildWithTests', ontrackCliIgnoreErrors())) {
        // Validation run properties
        Validation.setValidationRunProperties(this, params, response, 'validateBuildWithTests')
    }

    // Returning the results
    return results
}
