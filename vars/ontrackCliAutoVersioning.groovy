import net.nemerosa.ontrack.jenkins.pipeline.autoversioning.AutoVersioningContext
import net.nemerosa.ontrack.jenkins.pipeline.autoversioning.AutoVersioningDependency
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:], Closure configuration) {

    // Parameters
    String project = ParamUtils.getParam(params, "project", env.ONTRACK_PROJECT_NAME as String)
    String branch = ParamUtils.getParam(params, "branch", env.ONTRACK_BRANCH_NAME as String)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // Not for pull requests
    if (env.BRANCH_NAME ==~ 'PR-.*') {
        echo "No Ontrack auto versioning on promotion for pull requests."
        return
    }

    // Logging
    Closure logger = { String message -> }
    if (logging) {
        logger = { String message ->
            println(message)
        }
    }

    // Block evaluation
    def context = new AutoVersioningContext(this)
    configuration.delegate = context
    configuration()

    // Checking the branches
    Set<String> branches = context.branches
    if (branches && !branches.any { branchName -> env.BRANCH_NAME ==~ branchName }) {
        echo "ontrackCliAutoVersioning: current branch ${env.BRANCH_NAME} does not match ${branches} - not setting up PR creation on promotion"
        return
    }

    // Query

    String query = '''
        mutation SetAutoVersioning(
            $project: String!,
            $branch: String!,
            $configurations: [AutoVersioningSourceConfigInput!]!,
        ) {
            setAutoVersioningConfigByName(input: {
                project: $project,
                branch: $branch,
                configurations: $configurations,
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
    ]

    // Getting the list of dependencies

    List<AutoVersioningDependency> dependencies = context.dependencies ?: []
    variables.configurations = dependencies.collect { it.toMap() }

    // Logging before GraphQL call
    logger("Variables: $variables")

    // GraphQL call

    def response = ontrackCliGraphQL(
            logging: logging,
            query: query,
            variables: variables,
    )

    // Checks for errors

    GraphQL.checkForMutationErrors(response, 'setAutoVersioningConfigByName')

}