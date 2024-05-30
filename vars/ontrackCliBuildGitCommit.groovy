import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    if (ontrackCliFailsafe()) return

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack build Git commit for pull requests."
        return
    }

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)
    String gitCommit = ParamUtils.getParam(params, "gitCommit", env.GIT_COMMIT as String)

    // GraphQL query

    String query = '''
        mutation BuildGitCommit(
			$project: String!,
			$branch: String!,
			$build: String!,
			$gitCommit: String!,
		) {
			setBuildGitCommitProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				commit: $gitCommit,
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
            gitCommit: gitCommit,
    ]

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'setBuildGitCommitProperty', ontrackCliIgnoreErrors())

}
