import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.properties.MessagePropertyUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {

    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING)

    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    String build = ParamUtils.getParam(params, "build", env.ONTRACK_BUILD_NAME as String)

    // GraphQL query

    String query = '''
        mutation BuildMessage(
			$project: String!,
			$branch: String!, 
			$build: String!,
			$messagePropertyType: String!,
			$messagePropertyText: String!,
		) {
			setBuildMessageProperty(input: {
				project: $project,
				branch: $branch,
				build: $build,
				type: $messagePropertyType,
				text: $messagePropertyText,
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
    ]

    // Message property

    MessagePropertyUtils.setVariables(params, variables)

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'setBuildMessageProperty')

}
