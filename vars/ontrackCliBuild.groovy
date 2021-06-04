import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String name = ParamUtils.getParam(params, "name", env.BUILD_NUMBER as String)
    String description = ParamUtils.getConditionalParam(params, "description", false, '')
    String release = ParamUtils.getConditionalParam(params, "release", false, null)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // Exports the build name

    env.ONTRACK_BUILD_NAME = name

    // GraphQL query

    String query = '''
        mutation BuildSetup(
			$project: String!,
			$branch: String!, 
			$build: String!, 
			$description: String, 
			$releaseProperty: Boolean!,
			$release: String!,
			$commitProperty: Boolean!,
			$commit: String!
		) {
			createBuildOrGet(input: {
				projectName: $project, 
				branchName: $branch, 
				name: $build, 
				description: $description
			}) {
				errors {
				  message
				}
			}
			setBuildReleaseProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				release: $release
			}) @include(if: $releaseProperty) {
				errors {
					message
				}
			}
			setBuildGitCommitProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				commit: $commit
			}) @include(if: $commitProperty) {
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
            build: name,
            description: description,
    ]

    // Release property

    variables.releaseProperty = false
    variables.release = null
    if (release) {
        variables.releaseProperty = true
        variables.release = release
    }

    // Git commit

    variables.commitProperty = false
    variables.commit = null
    String gitCommit = ParamUtils.getConditionalParam(params, "gitCommit", false, env.GIT_COMMIT as String)
    if (gitCommit && gitCommit != 'none') {
        variables.commitProperty = true
        variables.commit = gitCommit
    }

    // GraphQL call

    ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

}
