import net.nemerosa.ontrack.jenkins.pipeline.utils.OntrackUtils
import net.nemerosa.ontrack.jenkins.pipeline.utils.ParamUtils

def call(Map<String,?> params = [:]) {
    boolean logging = ParamUtils.getBooleanParam(params, "logging", false)

    // CLI download
    ontrackCliDownload(params)
    // CLI setup
    ontrackCliConnect(params)

    // Computing the Ontrack project name from the Git URL
    env.ONTRACK_PROJECT_NAME = OntrackUtils.getProjectName(env.GIT_URL)
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_PROJECT_NAME = ${env.ONTRACK_PROJECT_NAME}")
    }

    // Computing the Ontrack branch name from the branch name
    env.ONTRACK_BRANCH_NAME = OntrackUtils.getBranchName(env.BRANCH_NAME)
    if (logging) {
        println("[ontrack-cli-setup] ONTRACK_BRANCH_NAME = ${env.ONTRACK_BRANCH_NAME}")
    }
}
