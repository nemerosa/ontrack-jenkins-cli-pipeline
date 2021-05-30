import net.nemerosa.ontrack.jenkins.pipeline.utils.OntrackUtils

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
}
