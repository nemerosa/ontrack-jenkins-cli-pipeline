import net.nemerosa.ontrack.jenkins.pipeline.utils.OntrackUtils
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

        Cli.call(this, logger, *setupArgs)
    }
}
