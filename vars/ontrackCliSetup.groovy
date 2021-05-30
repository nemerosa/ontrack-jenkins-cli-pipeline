import net.nemerosa.ontrack.jenkins.pipeline.utils.OntrackUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.GitHubUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils
import net.nemerosa.ontrack.jenkins.pipeline.cli.Cli

def call(Map<String, ?> params = [:]) {
    boolean setup = ParamUtils.getBooleanParam(params, "setup", true)
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)
    Closure logger = {}
    if (logging) {
        logger = {
            println("[ontrack-cli-setup] $it")
        }
    }

    // CLI download
    ontrackCliDownload(params)
    // CLI setup
    ontrackCliConnect(params)

    // Computing the Ontrack project name from the Git URL
    env.ONTRACK_PROJECT_NAME = ParamUtils.getParam(params, "project", OntrackUtils.getProjectName(env.GIT_URL))
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_PROJECT_NAME = ${env.ONTRACK_PROJECT_NAME}")
    }

    // Computing the Ontrack branch name from the branch name
    env.ONTRACK_BRANCH_NAME = ParamUtils.getParam(params, "branch", OntrackUtils.getBranchName(env.BRANCH_NAME))
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_BRANCH_NAME = ${env.ONTRACK_BRANCH_NAME}")
    }

    // Setting up the branch
    if (setup) {

        // Project & branch creation

        List<String> setupArgs = ['branch', 'setup', '--project', env.ONTRACK_PROJECT_NAME, '--branch', env.ONTRACK_BRANCH_NAME]

        String autoVS = ParamUtils.getConditionalParam(params, "autoValidationStamps", false, "")
        if (autoVS == 'true') {
            setupArgs += "--auto-create-vs"
        } else if (autoVS == 'force') {
            setupArgs += "--auto-create-vs"
            setupArgs += "--auto-create-vs-always"
        } else if (autoVS == 'false') {
            setupArgs += "--auto-create-vs=false"
        }

        String autoPL = ParamUtils.getConditionalParam(params, "autoPromotionLevels", false, "")
        if (autoPL == 'true') {
            setupArgs += "--auto-create-pl"
        } else if (autoPL == 'false') {
            setupArgs += "--auto-create-pl=false"
        }

        Cli.call(this, logger, setupArgs)

        // Git configuration for the project & branch

        String scm = ParamUtils.getParam(params, "scm",  env.ONTRACK_SCM ?: 'github')
        logger("SCM = $scm")

        int scmIndexation = ParamUtils.getIntParam(params, "scmIndexation", 30)
        logger("SCM Indexation = $scmIndexation")

        if (scm == 'github') {
            String scmConfig = ParamUtils.getParam(params, "scmConfiguration",  env.ONTRACK_SCM_CONFIG ?: 'github.com')
            String owner = GitHubUtils.getOwner(env.GIT_URL)
            String repository = GitHubUtils.getRepository(env.GIT_URL)

            String issueService = ParamUtils.getParam(params, "scmIssues",  env.ONTRACK_SCM_ISSUES ?: 'self')

            Cli.call(this, logger, ['project', 'set-property', '--project', env.ONTRACK_PROJECT_NAME, 'github', '--configuration', scmConfig, '--repository', "${owner}/${repository}", '--indexation', scmIndexation, '--issue-service', issueService])
        } else {
            throw new RuntimeException("SCM not supported: $scm")
        }

        // TODO Branch Git configuration

        // TODO Auto promotion configuration

    }
}
