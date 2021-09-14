import net.nemerosa.ontrack.jenkins.pipeline.properties.MessagePropertyUtils
import net.nemerosa.ontrack.jenkins.pipeline.properties.MetaInfoPropertyUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL

def call(Map<String, ?> params = [:]) {

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String name = ParamUtils.getParam(params, "name", env.BUILD_NUMBER as String)
    String description = ParamUtils.getConditionalParam(params, "description", false, '')
    String release = ParamUtils.getConditionalParam(params, "release", false, null)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

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
			$commit: String!,
			$messageProperty: Boolean!,
			$messagePropertyType: String!,
			$messagePropertyText: String!,
			$metaInfoProperty: Boolean!,
			$metaInfoPropertyItems: [MetaInfoPropertyItemInput!]!,
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
			setBuildMessageProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				type: $messagePropertyType,
				text: $messagePropertyText,
			}) @include(if: $messageProperty) {
				errors {
					message
				}
			}
			setBuildMetaInfoProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				append: false,
				items: $metaInfoPropertyItems,
			}) @include(if: $metaInfoProperty) {
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
    variables.release = ''
    if (release) {
        variables.releaseProperty = true
        variables.release = release
    }

    // Git commit

    variables.commitProperty = false
    variables.commit = ''
    String gitCommit = ParamUtils.getConditionalParam(params, "gitCommit", false, env.GIT_COMMIT as String)
    if (gitCommit && gitCommit != 'none') {
        variables.commitProperty = true
        variables.commit = gitCommit
    }

    // Message property

    variables.messageProperty = MessagePropertyUtils.setVariables(params, variables)

    // Meta info property

    variables.metaInfoProperty = MetaInfoPropertyUtils.setVariables(params, variables)

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'createBuildOrGet')
    GraphQL.checkForMutationErrors(response, 'setBuildReleaseProperty')
    GraphQL.checkForMutationErrors(response, 'setBuildGitCommitProperty')
    GraphQL.checkForMutationErrors(response, 'setBuildMessageProperty')
    GraphQL.checkForMutationErrors(response, 'setBuildMetaInfoProperty')

}
