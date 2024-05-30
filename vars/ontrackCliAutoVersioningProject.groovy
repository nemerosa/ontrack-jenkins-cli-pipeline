import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {

    if (ontrackCliFailsafe()) return

    // Parameters
    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    List<String> branchIncludes = params.branchIncludes as List<String>
    List<String> branchExcludes = params.branchExcludes as List<String>
    String lastActivityDate = params.lastActivityDate as String
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
        mutation SetAutoVersioningProject(
            $project: String!,
            $branchIncludes: [String!],
            $branchExcludes: [String!],
            $lastActivityDate: String,
        ) {
            setProjectAutoVersioningProjectProperty(input: {
                project: $project,
                branchIncludes: $branchIncludes,
                branchExcludes: $branchExcludes,
                lastActivityDate: $lastActivityDate,
            }) {
				errors {
				  message
				}
            }
        }
    '''

    // Variables

    Map<String, ?> variables = [
            project         : project,
            branchIncludes  : branchIncludes,
            branchExcludes  : branchExcludes,
            lastActivityDate: lastActivityDate,
    ]

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'setProjectAutoVersioningProjectProperty')
}