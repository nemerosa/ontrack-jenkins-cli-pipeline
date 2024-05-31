import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack for pull requests."
        return
    }

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME)
    String promotion = ParamUtils.getParam(params, "promotion")
    String description = params.description ?: ""
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    // GraphQL query
    String query = '''
        mutation CreatePromotionRun(
            $project: String!,
            $branch: String!,
            $build: String!,
            $promotion: String!,
            $description: String,
        ) {
            createPromotionRun(input: {
                project: $project,
                branch: $branch,
                build: $build,
                promotion: $promotion,
                description: $description,
            }) {
                errors {
                    message
                }
            }
        }
    '''

    // Query variables
    Map<String, ?> variables = [
            project    : project,
            branch     : branch,
            build      : build,
            promotion  : promotion,
            description: description,
    ]

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'createPromotionRun', ontrackCliIgnoreErrors())

}
