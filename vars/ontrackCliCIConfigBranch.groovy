import net.nemerosa.ontrack.jenkins.pipeline.ci.CIConfigHelper
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    String configPath = ParamUtils.getParam(params, 'config', '.yontrack/ci.yaml')
    String scm = params.scm
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING as String)

    Closure logger = {}
    if (logging) {
        logger = {
            echo "[ontrackCliCIConfigBranch] $it"
        }
    }

    def ciConfig = CIConfigHelper.buildCIConfig(this, logger, configPath, params.vars ?: [:], true)

    // Launching the configuration
    def response = ontrackCliGraphQL(
            query: '''
                mutation OntrackCliCIConfigBranch(
                    $config: String!,
                    $ci: String,
                    $scm: String,
                    $env: [CIEnv!]!,
                ) {
                    configureBranch(input: {
                        config: $config,
                        ci: $ci,
                        scm: $scm,
                        env: $env,
                    }) {
                        errors {
                            message
                            exception
                        }
                        branch {
                            id
                            name
                            displayName
                            project {
                                id
                                name
                            }
                        }
                    }
                }
            ''',
            variables: [
                    config: ciConfig.expandedConfigText,
                    ci    : 'jenkins',
                    scm   : scm,
                    env   : ciConfig.environment,
            ],
            logging: logging,
    )

    // Checks for errors
    GraphQL.checkForMutationErrors(response, 'configureBranch', ontrackCliIgnoreErrors() as boolean)

    // Logging of the outcome
    def branch = response.data?.configureBranch?.branch
    if (!branch) ontrackCliError("[ontrackCliCIConfigBranch] No branch was returned")
    logger("Branch: ${branch}")

    // Setting up the environment variables
    env.ONTRACK_PROJECT_ID = branch.project.id
    env.ONTRACK_PROJECT_NAME = branch.project.name
    env.ONTRACK_BRANCH_ID = branch.id
    env.ONTRACK_BRANCH_NAME = branch.name
    env.ONTRACK_BRANCH_DISPLAY_NAME = branch.displayName
}