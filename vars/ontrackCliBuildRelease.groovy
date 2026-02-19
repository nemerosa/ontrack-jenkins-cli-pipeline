import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack build release for pull requests."
        return
    }

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    String release = ParamUtils.getParam(params, "version", env.VERSION as String)

    // GraphQL query

    String query = '''
        mutation BuildRelease(
			$project: String!,
			$branch: String!, 
			$build: String!,
			$release: String!,
		) {
			setBuildReleaseProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				release: $release,
			}) {
				errors {
					message
				}
			}
		}
    '''

    // GraphQL variables

    Map<String,?> variables = [
            project: project,
            branch: branch,
            build: build,
            release: release,
    ]

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'setBuildReleaseProperty', ontrackCliIgnoreErrors())

}
