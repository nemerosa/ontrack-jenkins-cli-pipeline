import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {

    // Parameters
    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // Logging
    Closure logger = { String message -> }
    if (logging) {
        logger = { String message ->
            println(message)
        }
    }

    // Query

    String query = '''
        mutation CheckAutoVersioning(
            $project: String!,
            $branch: String!,
            $build: String!,
        ) {
            checkAutoVersioningConfig(input: {
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

    GraphQL.checkForMutationErrors(response, 'checkAutoVersioningConfig')

}