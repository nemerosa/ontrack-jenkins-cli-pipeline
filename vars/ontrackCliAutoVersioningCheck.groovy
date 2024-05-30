import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack auto versioning check on promotion for pull requests."
        return
    }

    // Parameters
    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    // Query

    String query = '''
        mutation CheckAutoVersioning(
            $project: String!,
            $branch: String!,
            $build: String!,
        ) {
            checkAutoVersioning(input: {
                project: $project,
                branch: $branch,
                build: $build,
            }) {
				errors {
				  message
				}
            }
        }
    '''

    // Variables

    Map<String, ?> variables = [
            project: project,
            branch : branch,
            build: build,
    ]

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'checkAutoVersioningConfig', ontrackCliIgnoreErrors())

}