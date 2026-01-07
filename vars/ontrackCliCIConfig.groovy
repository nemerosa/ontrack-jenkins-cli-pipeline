import net.nemerosa.ontrack.jenkins.pipeline.ci.CIConfigHelper
import net.nemerosa.ontrack.jenkins.pipeline.graphql.GraphQL
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String, ?> params = [:]) {
    String configPath = ParamUtils.getParam(params, 'config', '.yontrack/ci.yaml')
    String scm = params.scm
    boolean skipGitCommit = ParamUtils.getBooleanParam(params, 'skipGitCommit', false)
    boolean logging = ParamUtils.getLogging(params, env.ONTRACK_LOGGING as String)

    Closure logger = {}
    if (logging) {
        logger = {
            echo "[ontrackCliCIConfig] $it"
        }
    }

    logger("Reading CI config at $configPath")
    def configText = readFile(file: configPath)
    logger("CI config: $configText")

    // Collecting the environment
    def environment = env.getEnvironment().findAll { k, _ ->
        k.startsWith('GIT_') ||
                k.startsWith('JOB_') ||
                k.startsWith('NODE_') ||
                k.startsWith('BUILD_') ||
                k == "JENKINS_URL" ||
                k == "BRANCH_NAME" ||
                k == "VERSION"
    }.collect { k, v ->
        [name: k, value: v]
    } + [
            [name: 'GIT_URL', value: env.GIT_URL], // Not part of the env.getEnvironment()
    ]

    // GIT_COMMIT maybe not be injected correctly
    def existingGitCommit = environment.find { it.name == "GIT_COMMIT" }?.value
    if (!existingGitCommit && !skipGitCommit) {
        def gitCommit = sh(
                returnStdout: true,
                script: 'git rev-parse HEAD'
        ).trim()
        if (gitCommit) {
            environment += [name: 'GIT_COMMIT', value: gitCommit]
        }
    }

    def expandedConfigText = CIConfigHelper.expandConfig(this, configText, logger, params.vars ?: [:], environment)
    logger("CI expanded config: $expandedConfigText")

    // Launching the configuration
    def response = ontrackCliGraphQL(
            query: '''
                mutation OntrackCliCIConfig(
                    $config: String!,
                    $ci: String,
                    $scm: String,
                    $env: [CIEnv!]!,
                ) {
                    configureBuild(input: {
                        config: $config,
                        ci: $ci,
                        scm: $scm,
                        env: $env,
                    }) {
                        errors {
                            message
                            exception
                        }
                        build {
                            id
                            name
                            displayName
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
                }
            ''',
            variables: [
                    config: expandedConfigText,
                    ci    : 'jenkins',
                    scm   : scm,
                    env   : environment,
            ],
            logging: logging,
    )

    // Checks for errors
    GraphQL.checkForMutationErrors(response, 'configureBuild', ontrackCliIgnoreErrors() as boolean)

    // Logging of the outcome
    def build = response.data?.configureBuild?.build
    if (!build) ontrackCliError("[ontrackCliCIConfig] No build was returned")
    logger("Build: ${build}")

    // Setting up the environment variables
    env.ONTRACK_PROJECT_ID = build.branch.project.id
    env.ONTRACK_PROJECT_NAME = build.branch.project.name
    env.ONTRACK_BRANCH_ID = build.branch.id
    env.ONTRACK_BRANCH_NAME = build.branch.name
    env.ONTRACK_BRANCH_DISPLAY_NAME = build.branch.displayName
    env.ONTRACK_BUILD_ID = build.id
    env.ONTRACK_BUILD_NAME = build.name
    env.ONTRACK_BUILD_DISPLAY_NAME = build.displayName
}